/*
 * Team.java
 *
 * Copyright 2007 Sun Microsystems, Inc. ALL RIGHTS RESERVED Use of 
 * this software is authorized pursuant to the terms of the license 
 * found at http://developers.sun.com/berkeley_license.html.
 *
 * Original downloaded from: http://java.sun.com/developer/technicalArticles/J2SE/Desktop/persistenceapi/
 * Updated by Alvaro Monge to work with JPA 2.0 EclipseLink
 *
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * Class Team annotated to be an Entity. In this case we have the default
 * behavior so the Entity name (and thus table name) is the same as the class
 * name.
 *
 * @author John O'Conner
 * @author Alvaro Monge <alvaro.monge@csulb.edu>
 */
@NamedQueries({
    @NamedQuery(name = Team.GET_BY_NAME, query = "SELECT t FROM Team t WHERE t.teamName = :name"),
    @NamedQuery(name = Team.DELETE_BY_NAME, query = "DELETE FROM Team t WHERE t.teamName = :name"),
    @NamedQuery(name = Team.DELETE_ALL, query = "DELETE FROM Team t")
})
@Entity
public class Team implements Serializable {

    /**
     * Name of JPQL query string to retrieve the players in a named team.
     */
    public static final String GET_BY_NAME = "Team.get_by_name";
    /**
     * Name of JPQL query string to delete a team given its name.
     */
    public static final String DELETE_BY_NAME = "Team.delete_by_name";
    /**
     * Name of JPQL query string to delete all teams.
     */
    public static final String DELETE_ALL = "Team.delete";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @javax.persistence.Column(name = "team_name", nullable = false, unique = true, length = 100)
    private String teamName;
    private String league;
    
    
    /*string value for captain*/
    private String captain;
    /*string value for city*/
    private String city;

    /* For a bidirectional relationship, the annotation below defines 
     * the inverse side of a ManyToOne relationship: a
     * team has many players and relates it to the owning side,
     * in this case the team field of a Player. Thus, given a team T, then
     * the following must be true for every player P in T.players: T == P.getTeam()
     */
    @OneToMany(mappedBy = "team", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @CascadeOnDelete   // NOTE: This is specific to JPA Provider EclipseLink, thus NOT portable.
    private Collection<Player> roster;

    /* 
     * Alternatively, for a unidirectional relationship, we would remove the reference 
     * to Team from Player and you must explicitly define the FK column, otherwise by default JPA will
     * generate a "join table" that unfortunately acts like a ManyToMany relationship in the database!!
     * 
     */
//    @OneToMany
//    @JoinColumn(name="team_fk")
    /**
     * Creates a new instance of Team
     */
    public Team() {
        roster = new HashSet<>();
    }

    /**
     * Creates a new instance of Team with some specified values
     *
     * @param name the name of the Team
     * @param league the name of the sports league in which the team
     * participates
     */
    public Team(String name, String league) {
        this.teamName = name;
        this.league = league;
        roster = new HashSet<>();
    }

    /**
     * Gets the id of this Team
     *
     * @return the id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the id of this Team to the specified value.
     *
     * @param id the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of this Team
     *
     * @return teamName the name of this Team
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Sets the name of the Team
     *
     * @param teamName the name of the Team
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    /**
     * Gets the name of the sports league in which the Team participates.
     *
     * @return league the name of the league
     */
    public String getLeague() {
        return league;
    }

    /**
     * Sets the name of the sports league in which the Team participates
     *
     * @param league
     */
    public void setLeague(String league) {
        this.league = league;
    }

    /**
     * Access the collection of Player objects making up the team
     *
     * @return a collection of Player objects who make up the team
     */
    public Collection<Player> getRoster() {
        return roster;
    }

    /**
     * Set the collection of players for this team. Also ensure that each
     * player's team is set to this team.
     * From:http://en.wikibooks.org/wiki/Java_Persistence/OneToMany "As the
     * relationship is bi-directional so as the application updates one side of
     * the relationship, the other side should also get updated, and be in
     * synch"
     *
     * @param players is the collection of players making up the team.
     */
    public void setRoster(Collection<Player> players) {
        for (Player p : players) {
            addPlayer(p);
        }
    }

    /**
     * Add a player to this team. Also ensure that the player's team is set to
     * this team.
     *
     * @param player is the Player to be added to the team.
     */
    public void addPlayer(Player player) {
        roster.add(player);
        if (player.getTeam() != this) {
            player.setTeam(this);
        }
    }

    /**
     * Remove a player from the team.
     *
     * @param player to be removed from the team.
     * @return whether of not the player's been removed successfully.
     */
    public boolean removePlayer(Player player) {
        boolean success = roster.remove(player);

        if (success && player.getTeam() == this) {
            player.setTeam(null);
        }

        return success;
    }

    /**
     * Returns a hash code value for the object. This implementation computes a
     * hash code value based on the id fields in this object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Team. The result is
     * <code>true</code> if and only if the argument is not null and is a Team
     * object that has the same id field values as this object.
     *
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Team)) {
            return false;
        }
        Team other = (Team) object;
        return this.id != null && this.id.equals(other.id);
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Team[name=" + teamName + ", roster=" + roster + "]";
    }
}
