package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import com.example.repository.EmployeeReposotory;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeReposotory repo;

    public Employee saveEmployee(Employee employee) {
        BigDecimal totalSalary = calculateTotalSalary(employee.getSalary(), employee.getDoj());
        employee.setSalary(totalSalary);
        return repo.save(employee);
    }

    public List<EmployeeDto> calculateTaxDeductionForCurrentFinancialYear() {
		 List<Employee> employees = repo.findAll();
	        List<EmployeeDto> taxDetailsList = new ArrayList<>();

	        for (Employee employee : employees) {
	            BigDecimal yearlySalary = calculateYearlySalary(employee);
	            BigDecimal taxAmount = calculateTaxAmount(yearlySalary);
	            BigDecimal cessAmount = calculateCessAmount(yearlySalary);

	            EmployeeDto taxDetails = new EmployeeDto();
	            taxDetails.setEmployeeCode(employee.getEmployeeId());
	            taxDetails.setFirstName(employee.getFirstName());
	            taxDetails.setLastName(employee.getLastName());
	            taxDetails.setYearlySalary(yearlySalary);
	            taxDetails.setTaxAmount(taxAmount);
	            taxDetails.setCessAmount(cessAmount);

	            taxDetailsList.add(taxDetails);
	        }
	        return taxDetailsList;
    }
    
    private BigDecimal calculateTotalSalary(BigDecimal monthlySalary, LocalDate doj) {
        LocalDate currentDate = LocalDate.now();
        int months = (int) ((currentDate.getYear() - doj.getYear()) * 12 + currentDate.getMonthValue() - doj.getMonthValue());
        if (doj.getDayOfMonth() > 15) {
            months--;
        }
        BigDecimal totalSalary=monthlySalary.multiply(new BigDecimal(months));
		return totalSalary;
        }
        
        private BigDecimal calculateYearlySalary(Employee employee) {
            LocalDate currentDate = LocalDate.now();
            LocalDate doj = employee.getDoj();

            int months = (int) ((currentDate.getYear() - doj.getYear()) * 12 + currentDate.getMonthValue() - doj.getMonthValue());
            if (doj.getDayOfMonth() > 15) {
                months--;
            }

            BigDecimal totalSalary = employee.getSalary().multiply(BigDecimal.valueOf(months));
            return totalSalary;
        }
        private BigDecimal calculateTaxAmount(BigDecimal yearlySalary) {
            BigDecimal taxAmount = BigDecimal.ZERO;

            if (yearlySalary.compareTo(BigDecimal.valueOf(250000)) > 0) {
                BigDecimal taxableAmount = yearlySalary.subtract(BigDecimal.valueOf(250000));
                if (taxableAmount.compareTo(BigDecimal.valueOf(250000)) <= 0) {
                    taxAmount = taxableAmount.multiply(BigDecimal.valueOf(0.05));
                } else if (taxableAmount.compareTo(BigDecimal.valueOf(750000)) <= 0) {
                    taxAmount = BigDecimal.valueOf(12500).add(taxableAmount.subtract(BigDecimal.valueOf(250000)).multiply(BigDecimal.valueOf(0.1)));
                } else {
                    taxAmount = BigDecimal.valueOf(50000).add(taxableAmount.subtract(BigDecimal.valueOf(750000)).multiply(BigDecimal.valueOf(0.2)));
                }
            }

            return taxAmount;
        }
        private BigDecimal calculateCessAmount(BigDecimal yearlySalary) {
            BigDecimal cessAmount = BigDecimal.ZERO;

            if (yearlySalary.compareTo(BigDecimal.valueOf(2500000)) > 0) {
                BigDecimal excessAmount = yearlySalary.subtract(BigDecimal.valueOf(2500000));
                cessAmount = excessAmount.multiply(BigDecimal.valueOf(0.02));
            }

            return cessAmount;
        }
    }
