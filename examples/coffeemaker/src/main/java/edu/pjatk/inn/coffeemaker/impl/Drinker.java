package edu.pjatk.inn.coffeemaker.impl;

public class Drinker {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean equals(Drinker drinker) {
        if (drinker == null) {
            return false;
        }

        return this.id == drinker.id;
    }
}
