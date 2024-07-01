package com.example.rqchallenge.service;


import com.example.rqchallenge.model.*;
import com.example.rqchallenge.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    EmployeeService employeeService = new EmployeeService();

    List<Employee> employees = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        for(int i = 1; i <= 10; i++) {
            employees.add(Employee.builder().id(i).employee_name("Employee" + i).employee_age(i + 40).employee_salary(i * 1000).build());
        }
    }

    @Test
    public void testGetAllEmployees() throws URISyntaxException {
        getEmployees();
        List<Employee> employees = employeeService.getAllEmployees();
        Assertions.assertEquals(employees.size(), 10);
    }

    @Test
    public void testGetEmployeeByNameSearch() throws URISyntaxException {
        getEmployees();
        List<Employee> result = employeeService.getEmployeesByNameSearch("Employee4");
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals("Employee4", result.get(0).getEmployee_name());
    }

    @Test
    public void testGetHighestSalaryOfEmployee() throws URISyntaxException {
        getEmployees();
        int highestSalaryOfEmployees = employeeService.getHighestSalaryOfEmployees();
        Assertions.assertEquals(highestSalaryOfEmployees, 10000);
    }

    @Test
    public void testGetTop10HighestEarningEmployeeNames() {
        getEmployees();
        List<String> top10HighestEarningEmployeeNames = employeeService.getTop10HighestEarningEmployeeNames();
        Assertions.assertEquals(top10HighestEarningEmployeeNames.get(5), "Employee5");
    }

    @Test
    public void testGetEmployeeById() throws URISyntaxException {
        GetEmployeeResponse getEmployeeResponse = new GetEmployeeResponse();
        getEmployeeResponse.setData(Employee.builder().id(15).employee_name("Employee15").employee_age(66).employee_salary(7000).build());
        Mockito.when(restTemplate.exchange(Constants.GET_EMPLOYEE_URL, HttpMethod.GET, null, GetEmployeeResponse.class, "15")).thenReturn(new ResponseEntity<>(getEmployeeResponse, HttpStatus.OK));
        Optional<Employee> employee = employeeService.getEmployeeById("15");
        Assertions.assertEquals(employee.get().getEmployee_name(), "Employee15");
    }

    @Test
    public void testCreateEmployee() throws URISyntaxException {
        Employee employee = Employee.builder().employee_name("NewEmployee").employee_salary(10000).employee_age(55).build();
        CreateEmployeeResponse createEmployeeResponse = new CreateEmployeeResponse();
        createEmployeeResponse.setData(employee);
        Mockito.when(restTemplate.exchange(Constants.CREATE_EMPLOYEES_URL, HttpMethod.POST, new HttpEntity<>(employee), CreateEmployeeResponse.class)).thenReturn(new ResponseEntity<>(createEmployeeResponse, HttpStatus.OK));
        Optional<Employee> newEmployee = employeeService.createEmployee("NewEmployee", "10000", "55");
        Assertions.assertEquals(newEmployee.get().getEmployee_name(), "NewEmployee");
    }

    @Test
    public void testDeleteEmployee() throws URISyntaxException {
        GetEmployeeResponse getEmployeeResponse = new GetEmployeeResponse();
        getEmployeeResponse.setData(Employee.builder().id(10).employee_name("Employee10").employee_age(66).employee_salary(7000).build());
        Mockito.when(restTemplate.exchange(Constants.GET_EMPLOYEE_URL, HttpMethod.GET, null, GetEmployeeResponse.class, "10")).thenReturn(new ResponseEntity<>(getEmployeeResponse, HttpStatus.OK));
        Mockito.when(restTemplate.exchange(Constants.DELETE_EMPLOYEES_URL, HttpMethod.DELETE, null, DeleteEmployeeResponse.class, "10")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        employeeService.deleteEmployee("10");
        Assertions.assertEquals(employeeService.getEmployeeById("10").get().getEmployee_name(), "Employee10");
    }

    private void getEmployees() {
        GetEmployeesResponse getEmployeesResponse = new GetEmployeesResponse();
        getEmployeesResponse.setData(new ArrayList<>(employees));
        Mockito.when(restTemplate.exchange(Constants.GET_EMPLOYEES_URL, HttpMethod.GET, null, GetEmployeesResponse.class)).thenReturn(new ResponseEntity<>(getEmployeesResponse, HttpStatus.OK));
    }
}
