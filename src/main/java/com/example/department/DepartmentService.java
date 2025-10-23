package com.example.department;

import java.util.List;

public interface DepartmentService {
    Department createDepartment(Department department);
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    void deleteDepartment(Long id);

    Department getDepartmentByName(String deptName) throws DepartmentException;

    Department addUserToDepartment(String cdsId, String deptName);
}
