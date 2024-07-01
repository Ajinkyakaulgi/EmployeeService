package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String keyword);

    Optional<Employee> getEmployeeById(String id);

    int getHighestSalaryOfEmployees();

    List<String> getTop10HighestEarningEmployeeNames();

    Optional<Employee> createEmployee(String name, String salary, String age);

    Optional<String> deleteEmployee(String id);
}
