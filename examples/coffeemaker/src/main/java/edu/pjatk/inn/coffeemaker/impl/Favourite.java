package edu.pjatk.inn.coffeemaker.impl;

public class Favourite {
    private Drinker drinker;
    private Recipe recipe;

    public Drinker getDrinker() {
        return drinker;
    }

    public void setDrinker(Drinker drinker) {
        this.drinker = drinker;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public boolean equals(Favourite favourite) {
        if (favourite == null) {
            return false;
        }

        return this.drinker == favourite.drinker && this.recipe == favourite.recipe;
    }
}
