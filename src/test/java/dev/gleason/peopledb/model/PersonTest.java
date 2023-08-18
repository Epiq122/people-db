package dev.gleason.peopledb.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {


    @Test
    public void testForEquality() {
        ZonedDateTime now = ZonedDateTime.now();
        Person person1 = new Person("p1", "Person1", now);
        Person person2 = new Person("p1", "Person1", now);
        assertThat(person1).isEqualTo(person2);
    }

    @Test
    public void testForInequality() {
        ZonedDateTime now = ZonedDateTime.now();
        Person person1 = new Person("p1", "Person1", now);
        Person person2 = new Person("p2", "Person2", now);
        assertThat(person1).isNotEqualTo(person2);
    }

}
