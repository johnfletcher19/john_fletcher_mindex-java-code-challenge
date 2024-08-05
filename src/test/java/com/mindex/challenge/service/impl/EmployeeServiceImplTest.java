package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationUrl;
    private String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{employeeId}";
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testGetReportingStructure() {
        // Create employees that will be direct reports
        Employee directReport1 = new Employee();
        directReport1.setFirstName("John");
        directReport1.setLastName("Doe");
        directReport1.setDepartment("Engineering");
        directReport1.setPosition("Developer");

        Employee directReport2 = new Employee();
        directReport2.setFirstName("Emily");
        directReport2.setLastName("Jones");
        directReport2.setDepartment("Marketing");
        directReport2.setPosition("Specialist");

        // Save direct reports first
        Employee createdDirectReport1 = restTemplate.postForEntity(employeeUrl, directReport1, Employee.class).getBody();
        Employee createdDirectReport2 = restTemplate.postForEntity(employeeUrl, directReport2, Employee.class).getBody();

        assertNotNull(createdDirectReport1.getEmployeeId());
        assertNotNull(createdDirectReport2.getEmployeeId());

        // Create manager and assign direct reports
        Employee manager = new Employee();
        manager.setFirstName("Jane");
        manager.setLastName("Smith");
        manager.setDepartment("Sales");
        manager.setPosition("Manager");
        manager.setDirectReports(List.of(createdDirectReport1, createdDirectReport2));

        // Save the manager with direct reports
        Employee createdManager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();
        assertNotNull(createdManager.getEmployeeId());

        // Fetch and assert the reporting structure for the manager
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, createdManager.getEmployeeId()).getBody();
        assertNotNull(reportingStructure);
        assertEquals(2, reportingStructure.getNumberOfReports());
        assertEquals(createdManager.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
    }

    @Test
    public void testCreateAndGetCompensation() {
        // Create employee
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());

        // Create compensation for employee
        Compensation testCompensation = new Compensation();
        testCompensation.setEmployee(createdEmployee);
        testCompensation.setSalary("120000");
        testCompensation.setEffectiveDate(new Date());

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
        assertNotNull(createdCompensation);
        assertEquals(createdEmployee.getEmployeeId(), createdCompensation.getEmployee().getEmployeeId());
        assertEquals("120000", createdCompensation.getSalary());

        // Get compensation by employee ID
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assertNotNull(readCompensation);
        assertEquals(createdEmployee.getEmployeeId(), readCompensation.getEmployee().getEmployeeId());
        assertEquals("120000", readCompensation.getSalary());
        assertEquals(createdCompensation.getEffectiveDate(), readCompensation.getEffectiveDate());
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
