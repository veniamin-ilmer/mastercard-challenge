package com.mastercard.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Connections {

    Logger logger = LoggerFactory.getLogger(Connections.class);

    /**
     * We are saving all of the connections in this hashmap in both directions.
     * Example:
     * Boston, New York
     * Philadelphia, Newark
     * Newark, Boston
     * Trenton, Albany
     *
     * Will save as:
     * {
     *   boston -> (new york, newark),
     *   new york -> (boston)
     *   philadelphia -> (newark),
     *   newark -> (boston, philadelphia),
     *   trenton -> (albany),
     *   albany -> (trenton),
     * }
     *
     * Note that the information saved is only one layer deep of connections.
     * Functions checking for connections would need to recurse through the layers to find deeper connections.
     * If the function takes too long to recurse, we might save a cached version of the deep connections.
     *
     * Note that everything is saved in lowercase to compare ignoring case.
     */

    private Map<String, Set<String>> connectionMap = new HashMap<>();;

    /**
     * Making filename optional in case we want to addConnections directly.
     */
    public Connections() {
    }

    /**
     * Reads each line from file into loadConnection function.
     * @param filename example file data: "New York, California\nBoston, Philadelphia"
     */
    public Connections(String filename) {
        loadFile(filename);
    }

    /**
     * Reads each line from file into loadConnection function.
     * @param filename example file data: "New York, California\nBoston, Philadelphia"
     */
    private void loadFile(String filename) {
        try {
            InputStream resource = new ClassPathResource(filename).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
            reader.lines().forEach(line -> {
                String[] cities = line.split(",");
                if (cities.length == 2) {
                    addConnection(cities[0], cities[1]);
                } else {
                    logger.error("Unable to load connection: " + line);
                }
            });
        } catch(IOException e) {
            logger.error("Failed to read file " + filename);
        }
    }

    /**
     * Saves connections in hashmap twice. Once in each direction.
     * @param city1 either city. Order doesn't matter.
     * @param city2 either city. Order doesn't matter.
     */
    public void addConnection(String city1, String city2) {
        addConnectionOneDirection(city1, city2);
        addConnectionOneDirection(city2, city1);
    }

    /**
     * Saves a connection from city1 to city2 in a hashmap.
     * Note that everything is saved in lowercase and compared in lowercase, to compare ignoring case.
     * @param city1 origin city
     * @param city2 destination city
     */
    private void addConnectionOneDirection(String city1, String city2) {
        city1 = city1.toLowerCase().trim();
        city2 = city2.toLowerCase().trim();
        if(city1.equals(city2)) {   //No point in saving a self reference.
            return;
        }
        Set set = connectionMap.get(city1);
        if(set == null) {
            set = new HashSet<String>();
            set.add(city2);
            connectionMap.put(city1.toLowerCase(), set);
        } else {
            set.add(city2);
        }
    }

    /**
     * We will be reading through the connectionMap to find connections.
     * Note that connectionMap only has one layer of connections.
     * So we are recursing through the layers to find deeper connections.
     * If this function takes too long to recurse, we might save a cached version of the deep connections.
     * Note that everything is saved in lowercase and compared in lowercase, to compare ignoring case.
     * @param currentCity where are we coming from?
     * @param destination where are we trying to get to?
     * @param avoidCities what cities should I avoid traversing through?
     * @return was a connection found?
     */
    public boolean isConnected(String currentCity, String destination, Set<String> avoidCities) {
        if(currentCity == null || destination == null) {
            logger.error("isConnected was called with null cities.");
            return false;
        }

        //Make it case insensitive
        currentCity = currentCity.toLowerCase().trim();
        destination = destination.toLowerCase().trim();

        //Any city should connect to itself. Even if it's not in the map.
        if(currentCity.equals(destination)) {
            return true;
        }

        Set<String> cities = connectionMap.get(currentCity);

        //There are no connections here.
        if(cities == null) {
            return false;
        }

        if(avoidCities == null) {
            avoidCities = new HashSet();
        } else {
            //Make a copy of the previous set, and write to our copy instead of overwriting parents.
            avoidCities = new HashSet(avoidCities);
        }
        //Since we are traversing this city now, we will avoid trying to traverse this it in the future.
        avoidCities.add(currentCity);

        //First check if in the immediate layer the city exists.
        if(cities.contains(destination)) {    //We found it! Bubble back up!
            return true;
        }

        //Go to the next layer only if we didn't process this city before
        for(String city : cities) {
            if(!avoidCities.contains(city)
                && isConnected(city, destination, avoidCities)) {    //We found it! Keep bubbling it up!
                    return true;
            }
        }
        //By this point, we have tried all the possible connections, and couldn't find anything.
        return false;
    }

}
