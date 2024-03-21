package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import com.example.service.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
	
	@Autowired
	private EmployeeService service;
	
	 @PostMapping(path="/create")
	    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
	      
	        Employee savedEmployee = service.saveEmployee(employee);
	        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
	    }
	 
	 
	 
	 @GetMapping("/tax-deduction")
	    public ResponseEntity<List<EmployeeDto>> calculateTaxDeduction() {
	        List<EmployeeDto> taxDetailsList = service.calculateTaxDeductionForCurrentFinancialYear();
	        return new ResponseEntity<>(taxDetailsList, HttpStatus.OK);
	    }

}
