package com.techelevator.dao;

import com.techelevator.model.Brewery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

@Service
public class JdbcBreweryDao implements BreweryDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcBreweryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // JbdcTemplate.update returns the number of rows affected. The count variable is used to hold the number of rows.
    // A few methods here use this variable to return a boolean if the amount of rows affected is the correct number for a successful method call (1)


    /**
     * Gets a single brewery by brewery id
     *
     * @param breweryId
     * @return Brewery
     */
    @Override
    public Brewery getBrewery(int breweryId) {
        Brewery brewery = new Brewery();
        String sql = "SELECT brewery_id, brewery.name, email, phone, street_address, city, state, zipcode, history, logo_img, is_active, has_food, owner_id "
                + "FROM brewery "
                + "WHERE brewery_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, breweryId);
        if (results.next()) {
            brewery = mapRowToBrewery(results);
            return brewery;
        } else {
            throw new RuntimeException("Brewery with ID " + breweryId + " was not found.");
        }
    }


    /**
     * gets all breweries
     *
     * @return array list of breweries
     */
    @Override
    public List<Brewery> getBreweries() {
        List<Brewery> breweries = new ArrayList<>();
        String sql = "SELECT brewery_id, brewery.name, email, phone, street_address, city, state, zipcode, history, logo_img, is_active, has_food, owner_id FROM brewery";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Brewery brewery = mapRowToBrewery(results);
            breweries.add(brewery);
        }

        return breweries;
    }

    /**
     * used by an admin to create a brewery with the base template information of a brewery name and the associated brewer (owner_id)
     *
     * @param brewery
     * @return Brewery
     */
    @Override
    public Brewery addBrewery(Brewery brewery) {
        Brewery newBrewery = new Brewery();
        String sql = "INSERT INTO brewery(brewery.name, owner_id) " +
                "VALUES(?, ?)";
        try {
            int newBreweryId = jdbcTemplate.queryForObject(sql, int.class, brewery.getBreweryId(), brewery.getOwnerId());
            newBrewery = getBrewery(newBreweryId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return newBrewery;
    }

    /**
     * used by a brewer to add/update the information associated with their brewery (email, phone, address, etc)
     *
     * @param brewery
     * @return boolean
     */
    @Override
    public boolean updateBrewery(Brewery brewery) {
        String sql = "UPDATE brewery " +
                "SET name = ?, email = ?, phone = ?, street_address = ?, " +
                "city = ?, state = ?, zipcode = ?, history = ?, logo_img = ?, is_active = ?, has_food = ? " +
                "WHERE brewery_id = ?";
        int count = jdbcTemplate.update(sql, brewery.getName(), brewery.getEmail(),
                brewery.getPhone(), brewery.getStreetAddress(),
                brewery.getCity(), brewery.getState(), brewery.getZip(),
                brewery.getHistory(), brewery.getLogo(), brewery.isActive(),
                brewery.isHasFood(), brewery.getBreweryId());
        return count == 1;
    }


    /**
     * used by an admin to delete a brewery
     *
     * @param breweryId
     * @return boolean
     */
    @Override
    public boolean deleteBrewery(int breweryId) {
        String sql = "DELETE FROM hours " +
                "WHERE brewery_id = ?; " +
                "DELETE FROM event " +
                "WHERE brewery_id = ?; " +
                "DELETE FROM image " +
                "WHERE brewery_id = ?; " +
                "DELETE FROM beer_review " +
                "WHERE beer_id in " +
                "(SELECT beer_id " +
                "FROM beer " +
                "WHERE brewery_id = ?); " +
                "DELETE FROM beer " +
                "WHERE brewery_id = ?; " +
                "DELETE FROM brewery " +
                "WHERE brewery_id = ?; ";
        int count = jdbcTemplate.update(sql, breweryId, breweryId, breweryId, breweryId, breweryId, breweryId);
        return count == 1;
    }

    private Brewery mapRowToBrewery(SqlRowSet rowSet) {
        Brewery brewery = new Brewery();

        brewery.setBreweryId(rowSet.getInt("brewery_id"));
        brewery.setName(rowSet.getString("name"));
        brewery.setEmail(rowSet.getString("email"));
        brewery.setPhone(rowSet.getString("phone"));
        brewery.setStreetAddress(rowSet.getString("street_address"));
        brewery.setCity(rowSet.getString("city"));
        brewery.setState(rowSet.getString("state"));
        brewery.setZip(rowSet.getString("zipcode"));
        brewery.setHistory(rowSet.getString("history"));
        brewery.setLogo(rowSet.getString("logo_img"));
        brewery.setActive(rowSet.getBoolean("is_active"));
        brewery.setHasFood(rowSet.getBoolean("has_food"));
        brewery.setOwnerId(rowSet.getInt("owner_id"));

        return brewery;

    }
}
