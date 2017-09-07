/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.persistence.ManyToMany;
import org.eclipse.persistence.annotations.CascadeOnDelete;
/**
 *
 * @author Keith
 */
@Entity
public class City implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    /*attributes*/
    private Integer Id;
    private String City;
    private String State;
    
    @OneToMany(mappedBy = "team", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @CascadeOnDelete   // NOTE: This is specific to JPA Provider EclipseLink, thus NOT portable.
    private Collection<Team> team;
    
    public City() {
        team = new HashSet<>();
    }
    public City(String cityName, String stateName) {
        this.City = cityName;
        this.State = stateName;
        team = new HashSet<>();
    }
    
    public String getCity() {
        return City;
    }
    public String getState() {
        return State;
    }
    public Collection<Team> getTeam() {
        return team;
    }
    public void setCity(String cityName ) {
        this.City = cityName;
    }
    public void setState(String stateName) {
        this.State = stateName;
    }
    
    public void setTeam(Collection<Team> teams) {
        for (Team p : teams) {
            add
        }
    }
    public void addTeam(Team teams) {
        roster.add(player);
        if (player.getTeam() != this) {
            player.setTeam(this);
        }
    }    

}
