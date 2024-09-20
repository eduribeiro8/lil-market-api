package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.rest.exception.CustomerNotFoundException;
import com.eduribeiro8.LilMarket.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customer/{customerId}")
    ResponseEntity<Customer> getCustomer(@PathVariable int customerId){
        Customer customer = customerService.findById(customerId);

        if (customer == null){
            throw new CustomerNotFoundException();
        }

        return ResponseEntity.ok(customer);
    }

    @GetMapping("/customer")
    public List<Customer> getAllCustomers(){
        return customerService.findAll();
    }

    @PostMapping("/customer")
    ResponseEntity<String> saveCustomer(@Valid @RequestBody Customer theCustomer){
        return ResponseEntity.ok("Customer was successfully saved");
    }

    @PutMapping("/customer")
    public ResponseEntity<String> updateCustomer(@Valid @RequestBody Customer theCustomer){
        Customer customer = customerService.findById(theCustomer.getId());

        if (customer == null){
            throw new CustomerNotFoundException();
        }

        customerService.save(theCustomer);

        return ResponseEntity.ok("Customer information was successfully updated!");
    }

    @DeleteMapping("admin/customer/{customerId}")
    public ResponseEntity<String> deleteCustomerById(@PathVariable int customerId){
        Customer customer = customerService.findById(customerId);

        if (customer == null){
            throw new CustomerNotFoundException();
        }

        customerService.deleteById(customerId);
        return ResponseEntity.ok("Customer " + customer.getFirstName() + " has been deleted!");
    }


}
