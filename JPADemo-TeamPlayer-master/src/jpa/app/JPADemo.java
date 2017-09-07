/*
 * Created by Dr. Alvaro Monge <alvaro.monge@csulb.edu>
 * This code is for educational purposes only,
 * please check with me otherwise.
 */

package jpa.app;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import jpa.entities.Player;
import jpa.entities.Team;

/**
 *
 * @author Alvaro Monge <alvaro.monge@csulb.edu>
 */
public class JPADemo {

    private static final Logger THE_LOGGER
            = Logger.getLogger(JPADemo.class.getName());

    // Create the EntityManager
    // sportsPU is a Persistence Unit as defined in persistence.xml that is
    // part of this application (it is the META-INF folder)
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("SportsPU");
    private static final EntityManager ENTITY_MANAGER = EMF.createEntityManager();

    private static final Scanner USER_INPUT = new Scanner(System.in);
    
    /**
     * Constructor to setup the initial set of transient objects. 
     */
    JPADemo() {
        for (Player player : PLAYERS_LAKERS) {
            TEAMS[0].addPlayer(player);  // addPlayer is responsible for setting a player's team
        }

        for (Player player : PLAYERS_CLIPPERS) {
            TEAMS[1].addPlayer(player);
        }
    
    }

    /**
     * A menu-driven program giving user options of executing sample functions on the persistent data
     * @param args no arguments are used
     */
    public static void main(String[] args) {
        JPADemo demo = new JPADemo();
        demo.loadDatabase();

        String userInput;
        do {
            displayMenu();
            userInput = USER_INPUT.nextLine();
            processInput(demo, userInput);
        } while (! userInput.equalsIgnoreCase("quit"));
    }

    /**
     * Given some user USER_INPUT of the function to be executed, sets it up and executes it.
     * @param demo The demo object that has the functions of this demo
     * @param userInput The user's choice of which function to execute
     */
    public static void processInput(JPADemo demo, String userInput) {
        String teamName;
        
        switch (userInput.toLowerCase()) {
            case "reload":
                System.out.println("Removing DB of all records");
                demo.deleteDB();
                System.out.println("Loading DB with sample initial records");
                demo.loadDatabase();
                break;
            case "roster":
                System.out.print("Team name: ");
                teamName = USER_INPUT.nextLine();
                Collection<Player> roster = demo.getRoster(teamName);
                if (roster != null && !roster.isEmpty()) {
                    System.out.println("The roster: ");
                    for (Player player : roster)
                        System.out.println(player);
                } else {
                    System.out.println("No players found");
                }
                break;
            case "remove player":
                System.out.println("Remove a Player");
                System.out.print("First name: ");
                String firstName = USER_INPUT.nextLine(); 
                System.out.print("Last name: "); 
                String lastName = USER_INPUT.nextLine();
                demo.remove(firstName, lastName);
                break;
            case "find":
                System.out.println("Find operation using id's");
                demo.find();
                break;
            case "remove team":
                System.out.println("Remove a Team");
                System.out.print("Team name: ");
                teamName = USER_INPUT.nextLine();
                demo.remove(teamName);
                break;
            default:
                System.out.println("Invalid choice, try again");
        }
    }

    /**
     * Display the menu of choices for the program
     */
    public static void displayMenu() {
        System.out.println();
        for (int i = 0; i < MENU_CHOICES.length; i++) {
            System.out.println(MENU_CHOICES[i] + ": " + CHOICE_SUMMARIES[i]);
        }
        System.out.print("\tYour choice> ");
    }

    /**
     * Method to demonstrate deleting all database objects. This will also
     * clean up the EntityManager by detaching all objects known to be in it.
     */
    private void deleteDB() {
        ENTITY_MANAGER.getTransaction().begin();
        
        Query deletePlayers = ENTITY_MANAGER.createNamedQuery(Player.DELETE_ALL);
        deletePlayers.executeUpdate();

        Query deleteTeams = ENTITY_MANAGER.createNamedQuery(Team.DELETE_ALL);
        deleteTeams.executeUpdate();
        
        ENTITY_MANAGER.getTransaction().commit();
        ENTITY_MANAGER.clear();
        
    }
            
    /**
     * Load the database tables with some initial records provided in the code.
     */
    private void loadDatabase() {
        // An EntityManager object is used to perform persistence tasks such as
        // starting transactions, persisting objects, creating queries, etc.
        ENTITY_MANAGER.getTransaction().begin();
        
        // prior to the statement below, each of the Team objects in the teams array
        // is a transient entity, i.e. just a regular non-persistent Java object.

        // All instances at this point are transient... they're "objects" not "entities"
        for (Team team : TEAMS) {
            ENTITY_MANAGER.persist(team);
        }


        // NOTE: Persisting a Player object without assigning a Team fails at run-time. 
//        Player pete = new Player("Nick", "Young", 29, "Swaggy P");
//        ENTITY_MANAGER.persist(pete);
        
        // Now they are all persisted... even players due to the CascadeType (see relationship defined in Team.java)
        ENTITY_MANAGER.getTransaction().commit();
    }

    /**
     * Retrieves the players who are members of a team.
     * @param teamName The name of the team whose players is to be retrieved
     * @return a List of Players who are members of the named team, or an empty list.
     * 
     */
    private Collection<Player> getRoster(String teamName) {
        // TypedQuery provides strong type checking
        TypedQuery<Player> retrieveTeamQuery = ENTITY_MANAGER.createNamedQuery(Player.GET_PLAYER_LIST, Player.class);
        retrieveTeamQuery.setParameter("name", teamName);
        List<Player> teams = retrieveTeamQuery.getResultList();

        return teams == null || teams.isEmpty() ? null : teams;
    }

    /**
     * Removes Player given his/her name.
     * @param firstName first name of player to be removed from DB
     * @param lastName  last name of player to be removed from DB
     */
    private void remove(String firstName, String lastName) { 
        TypedQuery<Player> deleteStatement = ENTITY_MANAGER.createNamedQuery(Player.DELETE_BY_NAME, Player.class);
        deleteStatement.setParameter("firstName", firstName);
        deleteStatement.setParameter("lastName", lastName);
        
        ENTITY_MANAGER.getTransaction().begin();
        deleteStatement.executeUpdate();
        THE_LOGGER.fine("CHECK DB... you'll see named player removed");

        ENTITY_MANAGER.getTransaction().commit(); // Before a commit, the remove was not guaranteed
    }
    
    /**
     * Remove a named Team if it exists; otherwise, if it doesn't exist then it does nothing.
     * @param teamName the name of the Team to be deleted
     */
    private void remove(String teamName) {
        
        System.out.println("Demo WARNING: This will delete players on the team you're about to delete!");

        TypedQuery<Team> deleteStatement = ENTITY_MANAGER.createNamedQuery(Team.DELETE_BY_NAME, Team.class);
        deleteStatement.setParameter("name", teamName);

        ENTITY_MANAGER.getTransaction().begin();
        int count = deleteStatement.executeUpdate();
        THE_LOGGER.log(Level.FINE, "Number of teams deleted: {0}", count);
        ENTITY_MANAGER.getTransaction().commit();
    }

    /**
     * Method to attempt to find Players with id (the PK) values 1 thru 10.
     * The retrieval stops as soon as a Player with particular id is not found.
     */
    private void find() {
        // Create the EntityManager
        boolean playerFound = true;

        // ENTITY_MANAGER.find() requires the PK value by which an entity can be found
        // In the demo below, since we know PK values are auto generated
        // starting at 1, we attempt to find the first 10 players.
        System.out.println("The first players inserted in the database...");
        for (int primaryKey = 1; primaryKey <= 10 && playerFound; primaryKey++) {
            Player player = ENTITY_MANAGER.find(Player.class, primaryKey);
            if (player != null) {
                System.out.println(player);
            } else {
                playerFound = false;
            }
        }
    }
    
    /**
     * lakersPlayers is an array of Player objects that will all be assigned to
     * the Los Angeles Lakers Team object.
     */
    private static final Player[] PLAYERS_LAKERS = new Player[]{
        new Player("Kobe", "Bryant", 24, "The Black Mamba"),
        new Player("Steve", "Nash", 10, "Canadian wonder"),
        new Player("Lin", "Jeremy", 7, "Linmania")
    };

    /**
     * clippersPlayers is an array of Player objects that will all be assigned to
     * the Los Angeles Clippers Team object.
     */
    private static final Player[] PLAYERS_CLIPPERS = new Player[]{
        new Player("Blake", "Griffin", 32, "The new Highlight Film"),
        new Player("Jamal", "Crawford", 11, "It's raining three pointers"),
        new Player("Chris", "Paul", 3, "The nonstop assist generator")

    };

    /**
     * teams is an array of Team objects, with players to be assigned in a program
     */
    private static final Team[] TEAMS = new Team[]{
        new Team("Los Angeles Lakers", "West"),
        new Team("Los Angeles Clippers", "West"),
        new Team("Miami Heat", "East"),
        new Team("Brooklyn Nets", "East"),
        new Team("Oklahoma City Thunder", "West")
    };
    
    /**
     * cities
     */
    private static final City[] CITIES = new City[]{
        new City("Los Angeles", "California"),
        new City("Miami", "Florida"),
        new City("Brooklyn", "New York"),
        new City("Oklahoma City", "Oklahoma")
            
    };

    /**
     * Menu choices
     */
    private static final String MENU_CHOICES[] = {"reload", "find", "roster", "remove player", "remove team", "quit"};
    
    /**
     * Descriptions of the menu choices
     */
    private static final String CHOICE_SUMMARIES[] = {
        "Reload initial data into the database",
        "Demo of find using object id's",
        "View the name of all players in a team",
        "Remove a player given the name",
        "Remove a team given its name",
        "Quit this program"
    };

    
}
