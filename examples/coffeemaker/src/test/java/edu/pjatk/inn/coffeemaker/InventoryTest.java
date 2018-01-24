package edu.pjatk.inn.coffeemaker;

import edu.pjatk.inn.coffeemaker.impl.CoffeeMaker;
import edu.pjatk.inn.coffeemaker.impl.Inventory;
import edu.pjatk.inn.coffeemaker.impl.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;
import sorcer.service.ContextException;
import sorcer.service.Exertion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.eo.operator.*;
import static sorcer.so.operator.eval;

/**
 * @author Łukasz Domeradzki
 */
@RunWith(SorcerTestRunner.class)
@ProjectContext("examples/coffeemaker")
public class InventoryTest {
    private final static Logger logger = LoggerFactory.getLogger(InventoryTest.class);

    private CoffeeMaker coffeeMaker;
    private Inventory inventory;

    @Before
    public void setUp() throws ContextException {
        coffeeMaker = new CoffeeMaker();
        inventory = coffeeMaker.checkInventory();
    }

    @Test
    public void testAddInventoryWithPositiveSugar() {
        assertTrue(coffeeMaker.addInventory(0, 0, 5, 0));
    }

    @Test
    public void testMakeCoffeeCorrectIngredientsAfterwards() {
        Recipe espresso = new Recipe();
        espresso.setName("espresso");
        espresso.setPrice(50);
        espresso.setAmtCoffee(6);
        espresso.setAmtMilk(1);
        espresso.setAmtSugar(1);
        espresso.setAmtChocolate(0);

        coffeeMaker.addRecipe(espresso);
        coffeeMaker.addInventory(10, 10, 10, 10);

        int amtCoffee = inventory.getCoffee();
        coffeeMaker.makeCoffee(espresso, espresso.getPrice());
        assertEquals(amtCoffee - espresso.getAmtCoffee(), inventory.getCoffee());
    }
}

