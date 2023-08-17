package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Person;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PeopleRepositoryTests {

    @Test
    public void canSave() {
        PeopleRepository repo = new PeopleRepository();
        Person rob = new Person("Rob", "Gleason", ZonedDateTime.of(1986, 9, 11, 15, 15, 0, 0, ZoneId.systemDefault()));
        Person savedPerson = repo.save(rob);
        assertThat(savedPerson.getId()).isGreaterThan(0);


    }
}
