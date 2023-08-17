package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PeopleRepositoryTests {

    private Connection connection;
    private PeopleRepository repo;


    @BeforeEach
    void setUp() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/people_data_base";
        String username = "root";
        String password = "nope1016789";

        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            connection.setAutoCommit(false);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        repo = new PeopleRepository(connection);
    }

    // try {// open connection } catch(Exception ex) {handle exception} finally { clean up or recover }
    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {

            connection.close();
        }
    }


    @Test
    public void canSaveOnePerson() throws SQLException {

        Person rob = new Person("Rob", "Gleason", ZonedDateTime.of(1986, 9, 11, 15, 15, 0, 0, ZoneId.systemDefault()));
        Person savedPerson = repo.save(rob);
        assertThat(savedPerson.getId()).isGreaterThan(0);
    }

    @Test
    public void canSaveTwoPeople() {
        Person rob = new Person("Rob", "Gleason", ZonedDateTime.of(1986, 9, 11, 15, 15, 0, 0, ZoneId.systemDefault()));
        Person blake = new Person("Blake", "Brownson", ZonedDateTime.of(1981, 2, 13, 13, 15, 0, 0, ZoneId.systemDefault()));

        Person savedPerson1 = repo.save(rob);
        Person savedPerson2 = repo.save(blake);
        assertThat(savedPerson1.getId()).isNotEqualTo(savedPerson2.getId());

    }

}
