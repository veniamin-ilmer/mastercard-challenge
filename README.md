# Mastercard Challenge

Completed by [Veniamin Ilmer](http://veniamin-ilmer.github.io/)

### Summary

This application is deployed as a Spring Boot App that expose the endpoint:

http://localhost:8080/connected?origin=city1&destination=city2

It responds with 'yes' if city1 is connected to city2. Otherwise it responds with 'no'.

The [city.txt](src/main/resources/city.txt) is located in src/main/resources

### Links

[Code](src/main/java/com/mastercard/challenge)

[Unit Tests](src/test/java/com/mastercard/challenge/ChallengeApplicationTests.java)

[city.txt](src/main/resources/city.txt)

[Javadocs](https://veniamin-ilmer.github.io/mastercard-challenge/docs/)

### Operation

The application loads all information from city.txt into a hashmap containing a hashset.

We are saving all of the connections in this hashmap in both directions.

    Example:
    Boston, New York
    Philadelphia, Newark
    Newark, Boston
    Trenton, Albany

Will save as:

    {
      boston -> (new york, newark),
      new york -> (boston)
      philadelphia -> (newark),
      newark -> (boston, philadelphia),
      trenton -> (albany),
      albany -> (trenton),
    }

Note that the information saved is only one layer deep of connections.
Functions checking for connections would need to recurse through the layers to find deeper connections.
If the function takes too long to recurse, we might save a cached version of the deep connections.

Note that everything is saved in lowercase to compare ignoring case.

Because of the recursive nature of the connection search, it is limited in how many layers it can search. The unit test goes into 100 layers successfully. If significantly deeper connections are required, the algorithm would need to be updated to take that into account. 

### Self referenced cities

What to do if the user asks if city1 is connected to city1? And city1 is not in the hashmap at all?

For example, you have the standard city.txt:

    Boston, New York
    Philadelphia, Newark
    Newark, Boston
    Trenton, Albany

And this query comes in:

http://localhost:8080/connected?origin=Atlanta&destination=Atlanta

Atlanta is not a city listed in the map. Yet, it feels intuitive to understand that any city should be connected to itself, regardless if it is in the map or not.

That is why I have made the decision to allow for all queries referencing the same city, to return "yes", that there is a connection, regardless of them being on the map or not.

If this needs to be changed, simply change [this line](src/main/java/com/mastercard/challenge/Connections.java#L137).
