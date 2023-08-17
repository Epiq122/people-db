package dev.gleason.peopledb.repository;

import dev.gleason.peopledb.model.Person;

import java.sql.Connection;

public class PeopleRepository {
    private Connection connection;

    public PeopleRepository(Connection connection) {
        this.connection = connection;
    }

    public Person save(Person person) {


        return person;
    }
}
