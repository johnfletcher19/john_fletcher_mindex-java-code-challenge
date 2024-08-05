package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String employeeId);
    Employee update(Employee employee);

    ReportingStructure getReportingStructure(String employeeId);
    int getNumberOfReports(String employeeId);

    Compensation createCompensation(Compensation compensation);
    Compensation getCompensation(String employeeId);
}
