package com.bryceblankinship.CS490.individual.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.bryceblankinship.CS490.individual.dto.CustomerDTO;

@Service
public class CustomerService {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> res = new ArrayList<>();

        jdbc.query("SELECT c.customer_id, c.store_id, c.first_name, c.last_name, c.email, c.address_id, c.active, c.create_date, c.last_update, a.address, a.district, a.city_id, a.postal_code, a.phone, ci.city, ci.country_id, co.country FROM customer c JOIN address a ON c.address_id = a.address_id JOIN city ci ON a.city_id = ci.city_id JOIN country co ON ci.country_id = co.country_id ORDER BY c.customer_id; ", rs -> {
            CustomerDTO customer = new CustomerDTO();
            customer.setId(rs.getInt("customer_id"));
            customer.setStoreId(rs.getInt("store_id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            customer.setEmail(rs.getString("email"));
            customer.setAddressId(rs.getInt("address_id"));
            customer.setActive(rs.getBoolean("active"));
            customer.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
            customer.setLastUpdate(rs.getTimestamp("last_update").toLocalDateTime());
            customer.setAddress(rs.getString("address"));
            customer.setDistrict(rs.getString("district"));
            customer.setCityId(rs.getInt("city_id"));
            customer.setPostalCode(rs.getInt("postal_code"));
            customer.setPhone(rs.getString("phone"));
            customer.setCity(rs.getString("city"));
            customer.setCountryId(rs.getInt("country_id"));
            customer.setCountry(rs.getString("country"));

            res.add(customer);
        });

        return res;
    }

    public CustomerDTO getCustomerById(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<CustomerDTO> customers = new ArrayList<>();
        jdbc.query(
            "SELECT c.customer_id, c.store_id, c.first_name, c.last_name, c.email, " +
            "c.address_id, c.active, c.create_date, c.last_update, " +
            "a.address, a.district, a.city_id, a.postal_code, a.phone, " +
            "ci.city, ci.country_id, co.country " +
            "FROM customer c " +
            "JOIN address a ON c.address_id = a.address_id " +
            "JOIN city ci ON a.city_id = ci.city_id " +
            "JOIN country co ON ci.country_id = co.country_id " +
            "WHERE c.customer_id = :id",
            params,
            rs -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setId(rs.getInt("customer_id"));
                customer.setStoreId(rs.getInt("store_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setAddressId(rs.getInt("address_id"));
                customer.setActive(rs.getBoolean("active"));
                customer.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
                customer.setLastUpdate(rs.getTimestamp("last_update").toLocalDateTime());
                customer.setAddress(rs.getString("address"));
                customer.setDistrict(rs.getString("district"));
                customer.setCityId(rs.getInt("city_id"));
                customer.setPostalCode(rs.getInt("postal_code"));
                customer.setPhone(rs.getString("phone"));
                customer.setCity(rs.getString("city"));
                customer.setCountryId(rs.getInt("country_id"));
                customer.setCountry(rs.getString("country"));
                customers.add(customer);
            }
        );

        return customers.isEmpty() ? null : customers.get(0);
    }

    public List<Map<String, Object>> getCustomerRentals(Integer customerId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("customerId", customerId);

        List<Map<String, Object>> rentals = new ArrayList<>();
        jdbc.query(
            "SELECT r.rental_id, r.rental_date, r.return_date, f.title, f.rental_rate, " +
            "p.amount as payment_amount, p.payment_date " +
            "FROM rental r " +
            "JOIN inventory i ON r.inventory_id = i.inventory_id " +
            "JOIN film f ON i.film_id = f.film_id " +
            "LEFT JOIN payment p ON r.rental_id = p.rental_id " +
            "WHERE r.customer_id = :customerId " +
            "ORDER BY r.rental_date DESC",
            params,
            rs -> {
                Map<String, Object> rental = new HashMap<>();
                rental.put("rentalId", rs.getInt("rental_id"));
                rental.put("filmTitle", rs.getString("title"));
                rental.put("rentalDate", rs.getTimestamp("rental_date").toLocalDateTime());
                rental.put("returnDate", rs.getTimestamp("return_date") != null ? 
                    rs.getTimestamp("return_date").toLocalDateTime() : null);
                rental.put("rentalRate", rs.getDouble("rental_rate"));
                rental.put("paymentAmount", rs.getDouble("payment_amount"));
                rental.put("paymentDate", rs.getTimestamp("payment_date") != null ?
                    rs.getTimestamp("payment_date").toLocalDateTime() : null);
                rentals.add(rental);
            }
        );

        return rentals;
    }

    public boolean returnRental(Integer rentalId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rentalId", rentalId);
        params.addValue("returnDate", LocalDateTime.now());

        return jdbc.update(
            "UPDATE rental SET return_date = :returnDate WHERE rental_id = :rentalId",
            params
        ) > 0;
    }

    public CustomerDTO createCustomer(CustomerDTO customer) {
        // Insert address and retrieve generated key using KeyHolder
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("address", customer.getAddress());
        addressParams.addValue("district", customer.getDistrict());
        addressParams.addValue("cityId", customer.getCityId());
        addressParams.addValue("postalCode", customer.getPostalCode());
        addressParams.addValue("phone", customer.getPhone());
    
        KeyHolder addressKeyHolder = new GeneratedKeyHolder();
        jdbc.update(
            "INSERT INTO address (address, district, city_id, postal_code, phone, location) " +
            "VALUES (:address, :district, :cityId, :postalCode, :phone, ST_POINT(0,0))",
            addressParams,
            addressKeyHolder,
            new String[] {"address_id"}
        );
    
        Integer addressId = addressKeyHolder.getKey().intValue();
    
        // Insert customer and retrieve generated key using KeyHolder
        MapSqlParameterSource customerParams = new MapSqlParameterSource();
        customerParams.addValue("storeId", customer.getStoreId());
        customerParams.addValue("firstName", customer.getFirstName());
        customerParams.addValue("lastName", customer.getLastName());
        customerParams.addValue("email", customer.getEmail());
        customerParams.addValue("addressId", addressId);
        customerParams.addValue("active", customer.getActive());
    
        KeyHolder customerKeyHolder = new GeneratedKeyHolder();
        jdbc.update(
            "INSERT INTO customer (store_id, first_name, last_name, email, address_id, active, create_date) " +
            "VALUES (:storeId, :firstName, :lastName, :email, :addressId, :active, NOW())",
            customerParams,
            customerKeyHolder,
            new String[] {"customer_id"}
        );
    
        Integer customerId = customerKeyHolder.getKey().intValue();
    
        return getCustomerById(customerId);
    }
    

    public CustomerDTO updateCustomer(CustomerDTO customer) {
        // Update address
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("addressId", customer.getAddressId());
        addressParams.addValue("address", customer.getAddress());
        addressParams.addValue("district", customer.getDistrict());
        addressParams.addValue("cityId", customer.getCityId());
        addressParams.addValue("postalCode", customer.getPostalCode());
        addressParams.addValue("phone", customer.getPhone());

        jdbc.update(
            "UPDATE address SET address = :address, district = :district, " +
            "city_id = :cityId, postal_code = :postalCode, phone = :phone " +
            "WHERE address_id = :addressId",
            addressParams
        );

        // Update customer
        MapSqlParameterSource customerParams = new MapSqlParameterSource();
        customerParams.addValue("customerId", customer.getId());
        customerParams.addValue("storeId", customer.getStoreId());
        customerParams.addValue("firstName", customer.getFirstName());
        customerParams.addValue("lastName", customer.getLastName());
        customerParams.addValue("email", customer.getEmail());
        customerParams.addValue("active", customer.getActive());

        jdbc.update(
            "UPDATE customer SET store_id = :storeId, first_name = :firstName, " +
            "last_name = :lastName, email = :email, active = :active, " +
            "last_update = NOW() WHERE customer_id = :customerId",
            customerParams
        );

        return getCustomerById(customer.getId());
    }

    public boolean deleteCustomer(Integer id){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        jdbc.update("DELETE FROM payment WHERE customer_id = :id", params);
        jdbc.update("DELETE FROM rental WHERE customer_id = :id", params);
        jdbc.update("DELETE FROM customer c WHERE c.customer_id = :id", params);

        return true;
    }

}
