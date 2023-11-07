package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customer/{customerId}")
    public Customer getCustomer(@PathVariable int customerId){
        Customer customer = customerService.findById(customerId);

        if (customer == null){
            throw new RuntimeException("Customer customerId - " + customerId + " not found!");
        }

        System.out.println("Returning customer: " + customer);

        return customer;
    }

    @GetMapping("/customer")
    public List<Customer> getAllCustomers(){
        List<Customer> customerList = customerService.findAll();

        if (customerList == null){
            throw new RuntimeException("There are no customers!");
        }

        return customerList;
    }

    @PostMapping("/customer")
    public Customer saveCustomer(@RequestBody Customer theCustomer){
        return customerService.save(theCustomer);
    }

    @PutMapping("/customer")
    public Customer updateCustomer(@RequestBody Customer theCustomer){
        return customerService.save(theCustomer);
    }

    @DeleteMapping("/customer/{customerId}")
    public String deleteCustomerById(@PathVariable int customerId){
        Customer customer = customerService.findById(customerId);

        if (customer == null){
            throw new RuntimeException("Customer id " + customerId + " not found!");
        }

        customerService.deleteById(customerId);

        return "Customer " + customer.getFirstName() + " has been deleted!";
    }


}
