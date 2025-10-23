package com.example.department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.createDepartment(department);
    }

    @GetMapping("/all")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PostMapping("/{deptName}/add-user/{cdsId}")
    public Department addUserToDepartment(@PathVariable String cdsId, @PathVariable String deptName) {
        return departmentService.addUserToDepartment(cdsId, deptName);
    }

    @GetMapping("/{departmentId}")
    public Department getDepartmentById(@PathVariable Long departmentId) {
        return departmentService.getDepartmentById(departmentId);
    }

    @GetMapping("/name/{deptName}")
    public Department getDepartmentByName(@PathVariable String deptName) {
        return departmentService.getDepartmentByName(deptName);
    }

    @DeleteMapping("/delete/{departmentId}")
    public void deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
    }

}
