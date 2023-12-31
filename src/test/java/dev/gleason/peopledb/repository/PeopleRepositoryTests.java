package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

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
//            connection.setAutoCommit(false);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        repo = new PeopleRepository(connection);
    }


    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {

            connection.close();
        }
    }


    @Test
    public void canSaveOnePerson() {

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

    @Test
    public void canFindPersonById() {
        Person savedPerson = repo.save(new Person("Larry", "Kwan", ZonedDateTime.now()));
        Person foundPerson = repo.findById(savedPerson.getId()).get();
        assertThat(foundPerson.getDateOfBirth()).isCloseTo(savedPerson.getDateOfBirth(), within(1, ChronoUnit.SECONDS));


    }

    @Test
    public void testPersonIdNotFound() {
        Optional<Person> foundPerson = repo.findById(-1L);
        assertThat(foundPerson).isEmpty();

    }

    @Test
    public void canFindAll() {
        repo.save(new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John2", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John3", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John4", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John5", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John6", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John7", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John8", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));

        List<Person> people = repo.findAll();
        assertThat(people.size()).isGreaterThanOrEqualTo(10);
    }

    @Test
    public void canGetCount() {
        long startCount = repo.count();
        repo.save(new Person("John1", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        repo.save(new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        long endCount = repo.count();
        assertThat(endCount).isEqualTo(startCount + 3);
    }


    @Test
    public void canDelete() {
        Person savedPerson = repo.save(new Person("Lazerao", "Jackson", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        long startCount = repo.count();
        repo.delete(savedPerson);
        long endCount = repo.count();
        assertThat(endCount).isEqualTo(startCount - 1);

    }

    @Test
    public void canDeleteMultiplePeople() {
        Person p1 = repo.save(new Person("Lazerao", "Jackson", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        Person p2 = repo.save(new Person("Lazerao", "Jackson", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));

        long startCount = repo.count();
        repo.delete(p1, p2);
        long endCount = repo.count();
        assertThat(endCount).isEqualTo(startCount - 2);

    }


    @Test
    public void canUpdate() {
        Person savedPerson = repo.save(new Person("Lazerao", "Jackson", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));

        Person person1 = repo.findById(savedPerson.getId()).get(); // 0

        savedPerson.setSalary(new BigDecimal("42312.69"));
        repo.update(savedPerson);

        Person person2 = repo.findById(savedPerson.getId()).get(); // 42312.69

        assertThat(person2.getSalary()).isNotEqualTo(person1.getSalary());


    }


}
