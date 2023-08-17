package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.exception.UnableToSaveException;
import dev.gleason.peopledb.model.Person;

import java.sql.*;


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
//            preparedStatement.setTimestamp(3,
//                    Timestamp.valueOf(person.getDateOfBirth().withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(person.getDateOfBirth().toInstant()));
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
}
