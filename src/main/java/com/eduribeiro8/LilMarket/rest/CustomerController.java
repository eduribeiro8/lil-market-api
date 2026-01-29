package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.dto.CustomerRequestDTO;
import com.eduribeiro8.LilMarket.dto.CustomerResponseDTO;
import com.eduribeiro8.LilMarket.entity.Customer;
import com.eduribeiro8.LilMarket.rest.exception.CustomerNotFoundException;
import com.eduribeiro8.LilMarket.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable int customerId){
        CustomerResponseDTO customer = customerService.findById(customerId);

        return ResponseEntity.ok(customer);
    }

    @GetMapping("/customer")
    public List<CustomerResponseDTO> getAllCustomers(){
        return customerService.findAll();
    }

    @PostMapping("/customer")
    ResponseEntity<CustomerResponseDTO> saveCustomer(@Valid @RequestBody CustomerRequestDTO theCustomer){
        return ResponseEntity.ok(customerService.save(theCustomer));
    }

    @PutMapping("/customer/{customerId}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable int customerId, @Valid @RequestBody CustomerRequestDTO theCustomer){

        return ResponseEntity.ok(customerService.save(theCustomer));
    }

    @DeleteMapping("admin/customer/{customerId}")
    public ResponseEntity<String> deleteCustomerById(@PathVariable int customerId){
        customerService.deleteById(customerId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


}
