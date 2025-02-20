package com.bryceblankinship.CS490.individual.services;

import com.bryceblankinship.CS490.individual.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

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



    public boolean deleteCustomer(Integer id){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        jdbc.update("DELETE FROM payment WHERE customer_id = :id", params);
        jdbc.update("DELETE FROM rental WHERE customer_id = :id", params);
        jdbc.update("DELETE FROM customer c WHERE c.customer_id = :id", params);

        return true;
    }

}
