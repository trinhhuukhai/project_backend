package com.project.controller;

import com.project.response.ResponseResult;
import com.project.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/api/v1/customer")
@CrossOrigin("http://localhost:3000")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseResult> deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id);
    }
}
