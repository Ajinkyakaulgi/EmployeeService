package com.example.rqchallenge.service;

import com.example.rqchallenge.model.*;
import com.example.rqchallenge.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EmployeeService implements IEmployeeService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<Employee> getAllEmployees() {
        ResponseEntity<GetEmployeesResponse> response = restTemplate.exchange(
                Constants.GET_EMPLOYEES_URL,
                HttpMethod.GET,
                null,
                GetEmployeesResponse.class
        );
        if(response.getBody() != null) {
            return response.getBody().getData();
        } else {
            log.error("Received empty response while getting employees");
            return Collections.emptyList();
        }
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String keyword) {
        List<Employee> employees = getAllEmployees();
        return employees.stream().filter(employee -> employee.getEmployee_name().contains(keyword)).collect(Collectors.toList());
    }

    @Override
    public Optional<Employee> getEmployeeById(String id) {
        GetEmployeeResponse responseBody = restTemplate.exchange(Constants.GET_EMPLOYEE_URL, HttpMethod.GET, null, GetEmployeeResponse.class, id).getBody();
        if(responseBody != null) {
            return Optional.of(responseBody.getData());
        } else {
            log.error("Received null response while retrieving employee with id {}", id);
            return Optional.empty();
        }
    }

    @Override
    public int getHighestSalaryOfEmployees() {
        List<Employee> employees = getAllEmployees();
        return employees.stream().max(Comparator.comparing(Employee::getEmployee_salary)).get().getEmployee_salary();
    }

    @Override
    public List<String> getTop10HighestEarningEmployeeNames() {
        List<Employee> employees = getAllEmployees();
        return employees.stream().sorted(Comparator.comparing(Employee::getEmployee_salary).reversed()).limit(10).map(Employee::getEmployee_name).collect(Collectors.toList());
    }

    @Override
    public Optional<Employee> createEmployee(String name, String salary, String age) {
        Employee employee = Employee.builder().employee_name(name).employee_salary(Integer.parseInt(salary)).employee_age(Integer.parseInt(age)).build();
        ResponseEntity<CreateEmployeeResponse> response = restTemplate.exchange(Constants.CREATE_EMPLOYEES_URL, HttpMethod.POST, new HttpEntity<>(employee), CreateEmployeeResponse.class);
        if(response.getBody() != null) {
            return Optional.of(response.getBody().getData());
        } else {
            log.error("Received null response while trying to create employee with name {}", name);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> deleteEmployee(String id) {
        Optional<Employee> employee = getEmployeeById(id);
        if(employee.isPresent()) {
            ResponseEntity<DeleteEmployeeResponse> response = restTemplate.exchange(Constants.DELETE_EMPLOYEES_URL, HttpMethod.DELETE, null, DeleteEmployeeResponse.class, id);
            if(response.getStatusCode() == HttpStatus.OK) {
                return Optional.of(employee.get().getEmployee_name());
            }
        }
        return Optional.empty();
    }
}
