package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Person;
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

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/people_data_base");
    }

    @Test
    public void canSaveOnePerson() throws SQLException {

        PeopleRepository repo = new PeopleRepository(connection);
        Person rob = new Person("Rob", "Gleason", ZonedDateTime.of(1986, 9, 11, 15, 15, 0, 0, ZoneId.systemDefault()));
        Person savedPerson = repo.save(rob);
        assertThat(savedPerson.getId()).isGreaterThan(0);
    }

    @Test
    public void canSaveTwoPeople() {
        PeopleRepository repo = new PeopleRepository(connection);
        Person rob = new Person("Rob", "Gleason", ZonedDateTime.of(1986, 9, 11, 15, 15, 0, 0, ZoneId.systemDefault()));
        Person blake = new Person("Blake", "Brownson", ZonedDateTime.of(1981, 2, 13, 13, 15, 0, 0, ZoneId.systemDefault()));

        Person savedPerson1 = repo.save(rob);
        Person savedPerson2 = repo.save(blake);
        assertThat(savedPerson1.getId()).isNotEqualTo(savedPerson2.getId());

    }

}
