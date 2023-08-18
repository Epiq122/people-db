package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.exception.UnableToSaveException;
import dev.gleason.peopledb.model.Person;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class PeopleRepository {
    public static final String SAVE_PERSON_SQL = "INSERT INTO PEOPLE (FIRST_NAME,LAST_NAME,DOB) VALUES(?,?,?)";
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

    public Person findById(Long id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM PEOPLE WHERE ID = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("FIRST_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                ZonedDateTime dateOfBirth = ZonedDateTime.ofInstant(resultSet.getTimestamp("DOB").toInstant(), ZoneId.systemDefault());
                Person person = new Person(firstName, lastName, dateOfBirth);
                person.setId(resultSet.getLong("ID"));
                return person;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
