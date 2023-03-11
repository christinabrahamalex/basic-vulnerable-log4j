package com.heroes.api.heroesapi.controller;

import org.apache.coyote.http2.Http2AsyncUpgradeHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.heroes.api.heroesapi.persistence.HeroDAO;
import com.heroes.api.heroesapi.model.Hero;

/**
 * Handles the REST API requests for the Hero resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 */

@RestController
@RequestMapping("heroes")
public class HeroController {
    private static final Logger LOG = LogManager.getLogger(HeroController.class.getName());
    private HeroDAO heroDao;

    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param heroDao The {@link HeroDAO Hero Data Access Object} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public HeroController(HeroDAO heroDao) {
        this.heroDao = heroDao;
    }

    /**
     * Responds to the GET request for a {@linkplain Hero hero} for the given id
     * 
     * @param id The id used to locate the {@link Hero hero}
     * 
     * @return ResponseEntity with {@link Hero hero} object and HTTP status of OK if found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Hero> getHero(@PathVariable int id) {
        LOG.info("GET /heroes/" + id);
        try {
            Hero hero = heroDao.getHero(id);
            if (hero != null)
                return new ResponseEntity<Hero>(hero,HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for all {@linkplain Hero heroes}
     * 
     * @return ResponseEntity with array of {@link Hero hero} objects (may be empty) and
     * HTTP status of OK<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("")
    public ResponseEntity<Hero[]> getHeroes(@RequestHeader("X-Api-Version") String apiVersion) {
        LOG.info("GET /heroes" + "/n" + "Api version:" + apiVersion);

        // Replace below with your implementation
        try {
            Hero[] heroes = heroDao.getHeroes();
            if(heroes.length != 0)
                return new ResponseEntity<Hero[]>(heroes,HttpStatus.OK);
        }
        catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    /**
     * Responds to the GET request for all {@linkplain Hero heroes} whose name contains
     * the text in name
     * 
     * @param name The name parameter which contains the text used to find the {@link Hero heroes}
     * 
     * @return ResponseEntity with array of {@link Hero hero} objects (may be empty) and
     * HTTP status of OK<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     * <p>
     * Example: Find all heroes that contain the text "ma"
     * GET http://localhost:8080/heroes/?name=ma
     */
    @GetMapping("/")
    public ResponseEntity<Hero[]> searchHeroes(@RequestParam String name) {
        LOG.info("GET /heroes/?name="+name);

        // Replace below with your implementation
        try {
            Hero[] heroes = heroDao.findHeroes(name);
            if(heroes.length != 0)
                return new ResponseEntity<Hero[]>(heroes,HttpStatus.OK);
        }
        catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    /**
     * Creates a {@linkplain Hero hero} with the provided hero object
     * 
     * @param hero - The {@link Hero hero} to create
     * 
     * @return ResponseEntity with created {@link Hero hero} object and HTTP status of CREATED<br>
     * ResponseEntity with HTTP status of CONFLICT if {@link Hero hero} object already exists<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("")
    public ResponseEntity<Hero> createHero(@RequestBody Hero hero) {
        LOG.info("POST /heroes " + hero);

        // Replace below with your implementation
        try{
            Hero[] heroes = heroDao.getHeroes();
            int duplicate = 0;
            //Added functionality to check if user sends duplicate name and give status of CONFLICT if name already exists as per above documentation
            for(int i=0; i< heroes.length; i++) {
                Hero heroListItem = heroes[i];
                if((hero.getName().contains(heroListItem.getName()) || (hero.getId() == heroListItem.getId())) == true) 
                    duplicate = 1;
            }
            
            if(duplicate == 0) {
                Hero heroRep = heroDao.createHero(hero);
                if(heroRep != null)
                    return new ResponseEntity<Hero>(heroRep, HttpStatus.CREATED);
            } else 
                return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    /**
     * Updates the {@linkplain Hero hero} with the provided {@linkplain Hero hero} object, if it exists
     * 
     * @param hero The {@link Hero hero} to update
     * 
     * @return ResponseEntity with updated {@link Hero hero} object and HTTP status of OK if updated<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("")
    public ResponseEntity<Hero> updateHero(@RequestBody Hero hero) {
        LOG.info("PUT /heroes " + hero);

        // Replace below with your implementation
        try {
            Hero updatedHero = heroDao.updateHero(hero);
            if(updatedHero != null)
                return new ResponseEntity<Hero>(hero, HttpStatus.OK);    
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            
        }
        catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Hero hero} with the given id
     * 
     * @param id The id of the {@link Hero hero} to deleted
     * 
     * @return ResponseEntity HTTP status of OK if deleted<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Hero> deleteHero(@PathVariable int id) {
        LOG.info("DELETE /heroes/" + id);

        // Replace below with your implementation
        try {
            boolean heroDeleteStatus = heroDao.deleteHero(id);
            if(heroDeleteStatus == true)
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}