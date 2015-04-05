package com.example.daniel.myapplication.dummy;

/**
 * Created by Marden on 4/4/2015.
 */
public class Animal {

    private String name;

    public Animal()
    {
        super();
    }

    public Animal(String name)
    {
        super();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return this.name;
    }
}
