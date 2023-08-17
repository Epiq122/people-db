package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Person;

import java.sql.*;


public class PeopleRepository {
    private Connection connection;

    public PeopleRepository(Connection connection) {
        this.connection = connection;
    }

    public Person save(Person person) {
        String sql = "INSERT INTO PEOPLE (FIRST_NAME,LAST_NAME,DOB) VALUES(?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
            }
            System.out.printf("Records affected: %d%n", recordsAffected);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return person;
    }
}
