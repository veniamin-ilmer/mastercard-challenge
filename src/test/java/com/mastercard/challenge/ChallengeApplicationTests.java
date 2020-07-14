package com.mastercard.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ChallengeApplicationTests {

    /**
     * Standard challenge reading from file.
     */
    @Test
    void testStandardRun() {
        Connections connections = new Connections("city.txt");
        assertTrue(connections.isConnected("Boston", "Newark", null));
        assertTrue(connections.isConnected("Boston", "Philadelphia", null));
        assertFalse(connections.isConnected("Philadelphia", "Albany", null));
        assertFalse(connections.isConnected(null, null, null));
    }

    /**
     * Make sure there is no exception if there is no map.
     */
    @Test
    void testNoCities() {
        Connections connections = new Connections();
        assertFalse(connections.isConnected("Boston", "Newark", null));
    }

    /**
     * Make sure spaces and cases are ignored.
     */
    @Test
    void testSpacesAndCases() {
        Connections connections = new Connections();
        connections.addConnection("  BOSTON ", "NEWARK\n");
        assertTrue(connections.isConnected("\tbosTon", "neWark ", null));
    }

    /**
     * This will make a connection that is 100 layers deep.
     * If connections are going to be significantly larger, depending on the memory config of the operating system,
     * you might run into a stackoverflow.
     * You may need to update the algorithm to take this into account.
     */
    @Test
    void testMultiLayer() {
        Connections connections = new Connections();
        for(int i = 0; i < 100; i++) {
            connections.addConnection("City" + i, "City" + (i+1));
        }
        assertTrue(connections.isConnected("City0", "City100", null));
        assertFalse(connections.isConnected("City0", "City101", null));
    }

    /**
     * Make sure, when working with just one layer deep, we are not really limited how many cities we can connect.
     */
    @Test
    void testCenterCity() {
        Connections connections = new Connections();
        for(int i = 0; i <= 10000; i++) {
            connections.addConnection("Center City", "City" + i);
        }
        assertTrue(connections.isConnected("Center City", "City10000", null));
        assertTrue(connections.isConnected("Center City", "City0", null));
        assertFalse(connections.isConnected("Center City", "City-1", null));
    }

    /**
     * Make sure the connections are bidirectional
     */
    @Test
    void testBackwards() {
        Connections connections = new Connections();
        connections.addConnection("Philadelphia", "Newark");
        connections.addConnection("Newark", "Boston");
        assertTrue(connections.isConnected("Boston", "Philadelphia", null));
    }

    /**
     * Test adding the same connection multiple times doesn't break isConnected function
     */
    @Test
    void testDuplicate() {
        Connections connections = new Connections();
        connections.addConnection("Boston", "Newark");
        connections.addConnection("Boston", "Newark");
        connections.addConnection("Newark", "Boston");
        connections.addConnection(" NeWark", "Boston\t");
        assertTrue(connections.isConnected("Boston", "NeWark", null));
    }

    /**
     * Any city should connect to itself. Even if it's not in the map.
     */
    @Test
    void testSelfReference() {
        Connections connections = new Connections();
        assertTrue(connections.isConnected("Newark", "Newark", null));
        assertTrue(connections.isConnected("Boston", "Boston", null));
    }

    /**
     * Make sure self reference adds don't break isConnected function.
     */
    @Test
    void testSelfReferenceAdd() {
        Connections connections = new Connections();
        connections.addConnection("Boston", "Boston");
        connections.addConnection("Newark", "Newark");
        connections.addConnection("Boston", "Newark");
        assertTrue(connections.isConnected("Newark", "Boston", null));
    }


    @Autowired
    private CityController controller;

    /**
     * Make sure controller exists.
     */
    @Test
    void checkController() {
        assertThat(controller).isNotNull();
    }

}
