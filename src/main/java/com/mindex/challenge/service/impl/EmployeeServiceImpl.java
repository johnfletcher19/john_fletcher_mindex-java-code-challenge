package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee: [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employeeId: [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }
        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employeeId: [{}]", employee);
        return employeeRepository.save(employee);
    }

    // Reporting Structure
    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        LOG.debug("Reading reportingStructure for employeeId: [{}]", employeeId);

        Employee employee = employeeService.read(employeeId);
        int totalReports = employeeService.getNumberOfReports(employeeId);

        ReportingStructure reportingStructure = new ReportingStructure(employee, totalReports);

        return reportingStructure;
    }

    public int getNumberOfReports(String employeeId) {
        int reportCount = 0;

        Employee employee = this.read(employeeId);
        if (employee == null) {
            throw new RuntimeException("EmployeeId is null");
        }

        List<Employee> reports = employee.getDirectReports();
        if (reports != null) {
            for (Employee reportingEmployee : reports) {
                reportCount += 1 + getNumberOfReports(reportingEmployee.getEmployeeId());
            }
        }
        return reportCount;
    }

    // Compensation Service
    @Override
    public Compensation createCompensation(Compensation compensation) {
        LOG.debug("Creating compensation: [{}]", compensation);

        Employee employee = employeeService.read(compensation.getEmployee().getEmployeeId());
        compensation.setEmployee(employee);
        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Compensation getCompensation(String employeeId) {
        LOG.debug("Reading compensation for employeeId: [{}]", employeeId);

        Employee employee = employeeService.read(employeeId);
        Compensation compensation = compensationRepository.findCompensationByEmployeeId(employee);

        if (compensation == null) {
            throw new RuntimeException("No compensation record for employeeId: " + employeeId);
        }

        return compensation;
    }
}
