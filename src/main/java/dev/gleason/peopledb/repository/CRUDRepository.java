package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.annotation.SQL;
import dev.gleason.peopledb.model.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

abstract class CRUDRepository<T extends Entity> {
    protected Connection connection; // can be seen by subclass like PeopleRepository

    public CRUDRepository(Connection connection) {
        this.connection = connection;
    }

    // annotation
    private String getSaveSqlByAnnotation() {
        this.getClass().getAnnotation(SQL.class).value();
    }


    public T save(T entity) {
        try {
            PreparedStatement ps = connection.prepareStatement(getSaveSql(), Statement.RETURN_GENERATED_KEYS);
            mapForSave(entity, ps);
            int recordsSaved = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            while (rs.next()) {
                long id = rs.getLong(1);
                entity.setId(id);
            }
            System.out.printf("Records Saved: %d%n", recordsSaved);
            System.out.println(entity);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }


    public Optional<T> findById(Long id) {
        T entity = null;

        try {
            PreparedStatement ps = connection.prepareStatement(getFindBySql());
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                entity = extractEntityFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }


    public List<T> findAll() {
        List<T> entites = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(getFindAllSql());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                entites.add(extractEntityFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entites;
    }

    public long count() {
        long count = 0;
        try {
            PreparedStatement ps = connection.prepareStatement(getCountSql());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
                System.out.printf("Total Count: %s%n", count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void delete(T entity) {
        try {
            PreparedStatement ps = connection.prepareStatement(getDeleteSql());
            ps.setLong(1, entity.getId());
            int deletedRecordCount = ps.executeUpdate();
            System.out.printf("Deleted Records: %s%n", deletedRecordCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(T... entites) {
        try {
            Statement stmt = connection.createStatement();
            String ids = Arrays.
                    stream(entites)
                    .map(T::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            int deletedRecordsCount = stmt.executeUpdate(getDeleteInSql().replace(":ids", ids));
            System.out.println("Deleted Records: " + deletedRecordsCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void update(T entity) {
        try {
            PreparedStatement ps = connection.prepareStatement(getUpdateSql());
            mapForUpdate(entity, ps);
            ps.setLong(5, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    protected abstract String getUpdateSql();

    /**
     * @return Should return a SQL string like: DELETE "FROM PEOPLE WHERE ID IN (:ids)"
     * Be sure to include the '(:ids)' named parameter  and call it ids;
     */

    protected abstract String getDeleteInSql();

    protected abstract String getDeleteSql();

    protected abstract String getCountSql();

    protected abstract String getFindAllSql();

    abstract T extractEntityFromResultSet(ResultSet rs) throws SQLException;

    /**
     * @return Returns a String that represents the SQL statement to retrieve one entity
     * The SQL must contain one SQL parameter, i.e. "?", that will bind the entity's id
     */
    abstract String getFindBySql();


    abstract void mapForSave(T entity, PreparedStatement ps) throws SQLException;

    abstract void mapForUpdate(T entity, PreparedStatement ps) throws SQLException;

    abstract String getSaveSql();

}
