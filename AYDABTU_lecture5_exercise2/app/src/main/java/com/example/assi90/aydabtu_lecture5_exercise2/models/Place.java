package com.example.assi90.aydabtu_lecture5_exercise2.models;

public class Place {
    private long id;
    private String name;

    public Place() {} // implicit default constructor.

    // First explicit constructor
    public Place(String name) {
        this.name = name;
    }

    // Second explicit constructor. Dependign on the parameters given
    // it should automaticaly chose the correct one.
    public Place(long id, String name) {
        this(name);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
