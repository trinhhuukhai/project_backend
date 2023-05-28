package com.project.service;

import com.project.model.Customer;
import com.project.model.OrderItem;
import com.project.repository.CustomerRepository;
import com.project.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public ResponseEntity<ResponseResult> findCustomerByShopId(Long id) {
        List<Customer> foundCustomer = customerRepository.findByShopId(id);
        return !foundCustomer.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query order item in shop successfully", foundCustomer, foundCustomer.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseResult("failed", "Cannot find order item in shop with id = "+id, "",foundCustomer.size())
                );
    }

    public ResponseEntity<ResponseResult> deleteCustomer(Long id) {
        boolean exists = customerRepository.existsById(id);
        if(exists) {
            customerRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Delete review successfully", "",1)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("failed", "Cannot find review to delete", "",1)
        );
    }
}

