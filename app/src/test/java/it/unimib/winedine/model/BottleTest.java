package it.unimib.winedine.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class BottleTest {
    @Test
    public void bottlesWithSameApiIdAreEqualAndShareHashCode() {
        Bottle first = bottleWithId("wine-42");
        Bottle second = bottleWithId("wine-42");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void bottlesWithDifferentOrMissingIdsAreNotEqual() {
        assertNotEquals(bottleWithId("wine-42"), bottleWithId("wine-43"));
        assertNotEquals(new Bottle(), new Bottle());
    }

    private Bottle bottleWithId(String id) {
        Bottle bottle = new Bottle();
        bottle.setId(id);
        return bottle;
    }
}
