package edu.pjatk.inn.coffeemaker;

import edu.pjatk.inn.coffeemaker.impl.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;
import sorcer.service.*;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.co.operator.*;
import static sorcer.eo.operator.*;
import static sorcer.eo.operator.result;
import static sorcer.mo.operator.add;
import static sorcer.mo.operator.model;
import static sorcer.mo.operator.*;
import static sorcer.mo.operator.result;
import static sorcer.po.operator.*;
import static sorcer.so.operator.exert;

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

		TestCase.assertTrue(coffeeMaker.addDrinker(drinker));
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

		TestCase.assertTrue(coffeeMaker.addFavourite(favourite));
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
		TestCase.assertTrue(coffeeMaker.removeFavourite(favourite));
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
		TestCase.assertTrue(favourites.contains(favourite));
	}

	@Test
	public void testFavouriteService() throws Exception {
		Domain mod = model(
				ent("drinker/id", 9),
				ent("recipe/name", "espresso"),

				ent(
						sig("addFavourite", FavouriteService.class,
							result("favourite", inPaths("drinker/id", "recipe/name"))
						)
				)
		);

		responseUp(mod, "favourite");
		Context out = response(mod);
		logger.info("out: " + out);
		logger.info("result: " + result(mod));
		assertEquals(value(result(mod), "favourite"), true);
	}
}

