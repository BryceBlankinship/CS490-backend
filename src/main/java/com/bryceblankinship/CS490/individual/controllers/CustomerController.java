package com.bryceblankinship.CS490.individual.controllers;

import com.bryceblankinship.CS490.individual.dto.CustomerDTO;
import com.bryceblankinship.CS490.individual.services.CustomerService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    private ResponseEntity<List<CustomerDTO>> getAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @DeleteMapping("/customer")
    private ResponseEntity<?> deleteCustomer(@RequestParam("id") Integer id){
        return ResponseEntity.ok(customerService.deleteCustomer(id));
    }

}
