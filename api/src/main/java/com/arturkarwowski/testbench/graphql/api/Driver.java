package com.arturkarwowski.testbench.graphql.api;

public class Driver {

    private final String id;
    private final String firstName;
    private final String lastName;

    public Driver(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }
}
