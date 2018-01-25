package edu.pjatk.inn.coffeemaker;

import edu.pjatk.inn.coffeemaker.impl.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;
import sorcer.service.ContextException;
import sorcer.service.Exertion;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.eo.operator.*;
import static sorcer.so.operator.eval;

/**
 * @author Łukasz Domeradzki
 */
@RunWith(SorcerTestRunner.class)
@ProjectContext("examples/coffeemaker")
public class FavouriteServiceTest {
    private final static Logger logger = LoggerFactory.getLogger(FavouriteServiceTest.class);

    private FavouriteServiceImpl favouriteService;

    @Before
    public void setUp() throws ContextException {
        favouriteService = new FavouriteServiceImpl();
    }

    @Test
    public void testAddDrinker() {
        CoffeeMaker coffeeMaker = favouriteService.getCoffeeMaker();
        Drinker drinker = new Drinker();
        drinker.setId(6);

        assertTrue(coffeeMaker.addDrinker(drinker));
    }

    @Test
    public void testAddFavourite() {
        CoffeeMaker coffeeMaker = favouriteService.getCoffeeMaker();

        Drinker drinker = new Drinker();
        drinker.setId(7);
        coffeeMaker.addDrinker(drinker);

        Recipe recipe = coffeeMaker.getRecipeForName("espresso");

        Favourite favourite = new Favourite();
        favourite.setDrinker(drinker);
        favourite.setRecipe(recipe);

        assertTrue(coffeeMaker.addFavourite(favourite));
    }

    @Test
    public void testRemoveFavourite() {
        CoffeeMaker coffeeMaker = favouriteService.getCoffeeMaker();

        Drinker drinker = new Drinker();
        drinker.setId(8);
        coffeeMaker.addDrinker(drinker);

        Recipe recipe = coffeeMaker.getRecipeForName("espresso");

        Favourite favourite = new Favourite();
        favourite.setDrinker(drinker);
        favourite.setRecipe(recipe);

        coffeeMaker.addFavourite(favourite);
        assertTrue(coffeeMaker.removeFavourite(favourite));
    }

    @Test
    public void testGetFavourite() {
        CoffeeMaker coffeeMaker = favouriteService.getCoffeeMaker();

        Drinker drinker = new Drinker();
        drinker.setId(9);
        coffeeMaker.addDrinker(drinker);

        Recipe recipe = coffeeMaker.getRecipeForName("espresso");

        Favourite favourite = new Favourite();
        favourite.setDrinker(drinker);
        favourite.setRecipe(recipe);

        coffeeMaker.addFavourite(favourite);

        HashSet<Favourite> favourites = coffeeMaker.getFavourites(drinker);
        assertTrue(favourites.contains(favourite));
    }
}

