package com.example.department;

import com.example.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImplementation implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public DepartmentServiceImplementation(DepartmentRepository departmentRepository, UserService userService, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }


    public Department createDepartment(Department department) {
        if(departmentRepository.findByDepartmentNameIgnoreCase(department.getDepartmentName()) != null) {
            throw new DepartmentAlreadyExist("Department already exists with name: " + department.getDepartmentName());
        }
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        List<Department> allDepartments =  departmentRepository.findAll();

        if(allDepartments.isEmpty())
            throw  new DepartmentNotFound("No departments Found.");
        return allDepartments;
    }

    public Department getDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFound("Department not found with id: " + departmentId));
    }


    public void deleteDepartment(Long departmentId) {
        departmentRepository.deleteById(departmentId);
    }

    @Override
    public Department getDepartmentByName(String deptName) throws DepartmentException {
        Department fetchDept =  departmentRepository.findByDepartmentNameIgnoreCase(deptName);

        if(fetchDept == null)
            throw new DepartmentNotFound("Department not found with name: " + deptName);

        return  fetchDept;
    }

    @Override
    public Department addUserToDepartment(String cdsId, String deptName) {
        Department fetchedDepartment = departmentRepository.findByDepartmentNameIgnoreCase(deptName);
        if (fetchedDepartment == null) {
            throw new DepartmentNotFound("Department not found with name: " + deptName);
        }

        Optional<UserInfo> fetchedUser = userRepository.findByCdsID(cdsId);

        if(fetchedUser.isEmpty())
            throw new UserNotFound("User not found with id: " + cdsId);

        fetchedUser.ifPresent(u -> u.setDepartment(fetchedDepartment));
        fetchedDepartment.getUsers().add(fetchedUser.get());

        return departmentRepository.save(fetchedDepartment);

    }
}
