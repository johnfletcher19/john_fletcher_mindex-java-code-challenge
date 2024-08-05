package com.mindex.challenge;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBootstrapTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Test
    public void test() {
        Employee employee = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        assertNotNull(employee);
        assertEquals("John", employee.getFirstName());
        assertEquals("Lennon", employee.getLastName());
        assertEquals("Development Manager", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());
    }

    @Test
    public void testCompensationRepository() {
        // Retrieve the employee
        Employee employee = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        assertNotNull(employee);
        assertEquals("John", employee.getFirstName());
        assertEquals("Lennon", employee.getLastName());
        assertEquals("Development Manager", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());

        // Create a Compensation object
        Compensation compensation = new Compensation();
        compensation.setEmployee(employee);
        compensation.setSalary("100000");
        compensation.setEffectiveDate(new Date());

        // Save the Compensation to the repository
        compensationRepository.insert(compensation);

        // Retrieve the Compensation by employeeId
        Compensation retrievedCompensation = compensationRepository.findCompensationByEmployeeId(employee);
        assertNotNull(retrievedCompensation);

        // Verify the details of the retrieved Compensation
        assertEquals(employee.getEmployeeId(), retrievedCompensation.getEmployee().getEmployeeId());
        assertEquals("100000", retrievedCompensation.getSalary());
        assertEquals(compensation.getEffectiveDate(), retrievedCompensation.getEffectiveDate());
    }
}