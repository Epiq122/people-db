package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.exception.UnableToSaveException;
import dev.gleason.peopledb.model.Person;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PeopleRepository {
    public static final String SAVE_PERSON_SQL = "INSERT INTO PEOPLE (FIRST_NAME,LAST_NAME,DOB) VALUES(?,?,?)";
    public static final String FIND_BY_ID_SQL = "SELECT ID,FIRST_NAME,LAST_NAME,DOB FROM PEOPLE WHERE ID = ?";
    public static final String SELECT_COUNT_SQL = "SELECT COUNT(*) FROM PEOPLE";
    public static final String FIND_ALL_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE";
    private Connection connection;

    public PeopleRepository(Connection connection) {
        this.connection = connection;
    }

    public Person save(Person person) throws UnableToSaveException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PERSON_SQL, Statement.RETURN_GENERATED_KEYS);
            //these are biding to the sql statement
            preparedStatement.setString(1, person.getFirstName());
            preparedStatement.setString(2, person.getLastName());
            ZonedDateTime nowWithTimeZone = ZonedDateTime.now(ZoneId.systemDefault());
            preparedStatement.setTimestamp(3, Timestamp.from(nowWithTimeZone.toInstant()));
            int recordsAffected = preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                person.setId(id);
                System.out.println(person);
            }
            System.out.printf("Records affected: %d%n", recordsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnableToSaveException("Tried to save person : " + person);
        }

        return person;
    }

    public Optional<Person> findById(Long id) {
        Person person = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long personId = resultSet.getLong("ID");
                String firstName = resultSet.getString("FIRST_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                Timestamp dob = resultSet.getTimestamp("DOB");
                ZonedDateTime zonedDateTime = dob.toInstant().atZone(ZoneId.systemDefault());
                person = new Person(firstName, lastName, zonedDateTime);
                person.setId(personId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(person);
    }


    public List<Person> findAll() {
        List<Person> people = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                people.add(extractPersonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    private static Person extractPersonFromResultSet(ResultSet rs) throws SQLException {
        long personId = rs.getLong("ID");
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        ZonedDateTime dob = ZonedDateTime.of((rs.getTimestamp("DOB").toLocalDateTime()), ZoneId.of("+0"));
        BigDecimal salary = rs.getBigDecimal("SALARY");
        return new Person(personId, firstName, lastName, dob, salary);
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
}
