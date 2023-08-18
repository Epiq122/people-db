package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Person;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class PeopleRepository extends CRUDRepository<Person> {
    private static final String INSERT_PERSON_SQL = "INSERT INTO PEOPLE (FIRST_NAME, LAST_NAME, DOB) VALUES (?, ?, ?)";
    public static final String FIND_BY_ID_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE WHERE ID = ?";
    public static final String SELECT_COUNT_SQL = "SELECT COUNT(*) FROM PEOPLE";
    public static final String FIND_ALL_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE";
    private PreparedStatement ps;


    public PeopleRepository(Connection connection) {
        super(connection);

    }

    @Override
    String getSaveSql() {
        return INSERT_PERSON_SQL;
    }

    @Override
    void mapForSave(Person entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, convertDobToTimestamp(entity.getDateOfBirth()));
    }

    @Override
    Person extractEntityFromResultSet(ResultSet rs) throws SQLException {
        long personId = rs.getLong("ID");
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        ZonedDateTime dob = ZonedDateTime.of((rs.getTimestamp("DOB").toLocalDateTime()), ZoneId.of("+0"));
        BigDecimal salary = rs.getBigDecimal("SALARY");
        return new Person(personId, firstName, lastName, dob, salary);
    }

    @Override
    String getFindBySql() {
        return FIND_BY_ID_SQL;
    }


    public List<Person> findAll() {
        List<Person> people = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                people.add(extractEntityFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }


    public long count() {
        long count = 0;
        try {
            PreparedStatement ps = connection.prepareStatement(SELECT_COUNT_SQL);
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

    public void delete(Person person) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM PEOPLE WHERE ID = ?");
            ps.setLong(1, person.getId());
            int deletedRecordCount = ps.executeUpdate();
            System.out.printf("Deleted Records: %s%n", deletedRecordCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(Person... people) {
        try {
            Statement stmt = connection.createStatement();
            String ids = Arrays.
                    stream(people)
                    .map(Person::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            int deletedRecordsCount = stmt.executeUpdate("DELETE FROM PEOPLE WHERE ID IN (:ids)".replace(":ids", ids));
            System.out.println("Deleted Records: " + deletedRecordsCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Person person) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE PEOPLE SET FIRST_NAME = ?, LAST_NAME = ?, DOB = ?, SALARY = ? WHERE ID = ?");
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            ps.setTimestamp(3, convertDobToTimestamp(person.getDateOfBirth()));
            ps.setBigDecimal(4, person.getSalary());
            ps.setLong(5, person.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Timestamp convertDobToTimestamp(ZonedDateTime dob) {
        return Timestamp.valueOf(dob.withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime());
    }
}


