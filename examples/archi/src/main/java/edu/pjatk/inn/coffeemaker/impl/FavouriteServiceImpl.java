package edu.pjatk.inn.coffeemaker.impl;

import edu.pjatk.inn.coffeemaker.FavouriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;
import java.util.HashSet;

public class FavouriteServiceImpl implements FavouriteService {
    private final static Logger logger = LoggerFactory.getLogger(FavouriteServiceImpl.class);

    public CoffeeMaker getCoffeeMaker() {
        return coffeeMaker;
    }

    private final CoffeeMaker coffeeMaker = new CoffeeMaker();

    public FavouriteServiceImpl() {
        Recipe espresso = new Recipe();
        espresso.setName("espresso");
        espresso.setPrice(50);
        espresso.setAmtCoffee(6);
        espresso.setAmtMilk(1);
        espresso.setAmtSugar(1);
        espresso.setAmtChocolate(0);
        coffeeMaker.addRecipe(espresso);
    }

    @Override
    public Context addFavourite(Context context) throws RemoteException, ContextException {
        try {
            int id = (int) context.getValue("drinker/id");
            String name = (String) context.getValue("recipe/name");
            if (name == null) {
                throw new Exception();
            }

            Drinker drinker = coffeeMaker.getOrCreateDrinker(id);
            if (drinker == null) {
                throw new Exception();
            }

            Recipe recipe = coffeeMaker.getRecipeForName(name);
            if (recipe == null) {
                throw new Exception();
            }

            Favourite favourite = new Favourite();
            favourite.setDrinker(drinker);
            favourite.setRecipe(recipe);

            boolean result = coffeeMaker.addFavourite(favourite);

            if (context.getReturnPath() != null) {
                context.setReturnValue(result);
            }
        } catch (Exception e) {
            throw new ContextException();
        }

        return context;
    }

    @Override
    public Context removeFavourite(Context context) throws RemoteException, ContextException {
        try {
            int id = (int) context.getValue("drinker/id");
            String name = (String) context.getValue("recipe/name");
            if (name == null) {
                throw new Exception();
            }

            Drinker drinker = coffeeMaker.getOrCreateDrinker(id);
            if (drinker == null) {
                throw new Exception();
            }

            Recipe recipe = coffeeMaker.getRecipeForName(name);
            if (recipe == null) {
                throw new Exception();
            }

            Favourite favourite = new Favourite();
            favourite.setDrinker(drinker);
            favourite.setRecipe(recipe);

            boolean result = coffeeMaker.removeFavourite(favourite);

            if (context.getReturnPath() != null) {
                context.setReturnValue(result);
            }
        } catch (Exception e) {
            throw new ContextException();
        }

        return context;
    }

    @Override
    public Context getFavourites(Context context) throws RemoteException, ContextException {
        try {
            int id = (int) context.getValue("drinker/id");

            Drinker drinker = coffeeMaker.getOrCreateDrinker(id);
            if (drinker == null) {
                throw new Exception();
            }

            HashSet<Favourite> favourites = coffeeMaker.getFavourites(drinker);

            if (context.getReturnPath() != null) {
                context.setReturnValue(favourites);
            }
        } catch (Exception e) {
            throw new ContextException();
        }

        return context;
    }
}
