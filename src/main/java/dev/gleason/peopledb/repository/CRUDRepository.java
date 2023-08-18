package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Entity;

import java.sql.*;
import java.util.Optional;

abstract class CRUDRepository<T extends Entity> {
    protected Connection connection; // can be seen by subclass like PeopleRepository

    public CRUDRepository(Connection connection) {
        this.connection = connection;
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

    abstract T extractEntityFromResultSet(ResultSet rs) throws SQLException;

    /**
     * @return Returns a String that represents the SQL statement to retrieve one entity
     * The SQL must contain one SQL parameter, i.e. "?", that will bind the entity's id
     */
    abstract String getFindBySql();


    abstract void mapForSave(T entity, PreparedStatement ps) throws SQLException;


    abstract String getSaveSql();

}
