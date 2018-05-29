package com.ales.diabetolog;

public class notify {

    public String name;
    public int id;

    public notify(String name, int id) {
        this.name = name;

        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}