package com.bryceblankinship.CS490.individual.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bryceblankinship.CS490.individual.dto.CustomerDTO;
import com.bryceblankinship.CS490.individual.services.CustomerService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    private ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/customers/{id}")
    private ResponseEntity<?> getCustomerById(@PathVariable("id") Integer id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/customers/{id}/rentals")
    private ResponseEntity<List<Map<String, Object>>> getCustomerRentals(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(customerService.getCustomerRentals(id));
    }

    @PostMapping("/customers")
    private ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @PutMapping("/customers/{id}")
    private ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable("id") Integer id,
            @RequestBody CustomerDTO customer) {
        customer.setId(id);
        return ResponseEntity.ok(customerService.updateCustomer(customer));
    }

    @PostMapping("/rentals/{id}/return")
    private ResponseEntity<?> returnRental(@PathVariable("id") Integer rentalId) {
        boolean success = customerService.returnRental(rentalId);
        if (!success) {
            return ResponseEntity.badRequest().body("Failed to return rental");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/customers/{id}")
    private ResponseEntity<?> deleteCustomer(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(customerService.deleteCustomer(id));
    }
}
