package com.example.user;

import com.example.DTOs.LogInRequest;
import com.example.DTOs.LogInResponse;
import com.example.DTOs.NewUserInfo;
import com.example.department.Department;
import com.example.department.DepartmentException;
import com.example.department.DepartmentRepository;
import com.example.region.Region;
import com.example.region.RegionException;
import com.example.region.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RegionRepository regionRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository, DepartmentRepository departmentRepository, RegionRepository regionRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.regionRepository = regionRepository;
    }


    public UserInfo createUser(NewUserInfo newUserInfo) {

        UserInfo newUser = new UserInfo();

        if(userRepository.existsByCdsID(newUserInfo.getCdsId()))
            throw new UserException("Username already exists same CDSID");

        Department fetchDepartment = departmentRepository.findByDepartmentNameIgnoreCase(newUserInfo.getDepartment());
        if(fetchDepartment == null)
            throw new DepartmentException("Department not found for "+newUserInfo.getDepartment());

        Region fetchRegion = regionRepository.findRegionsByRegionNameIgnoreCase(newUserInfo.getRegion());
        if(fetchRegion == null)
            throw new RegionException("Region not found for "+newUserInfo.getRegion());

        newUser.setCdsID(newUserInfo.getCdsId());
        newUser.setDepartment(fetchDepartment);
        newUser.setRegion(fetchRegion);
        newUser.setEmail(newUserInfo.getEmail());
        newUser.setFirstName(newUserInfo.getFirstName());
        newUser.setLastName(newUserInfo.getLastName());
        newUser.setRole(newUserInfo.getRole());

        return userRepository.save(newUser);
    }

    public List<UserInfo> getAllUsers() {
        return userRepository.findAll();
    }

    public UserInfo getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserInfo getUserByCdsId(String cdsId) throws UserException {
        Optional<UserInfo> fetchedUser =  userRepository.findByCdsID(cdsId);

        if(fetchedUser.isEmpty()) {
            throw new UserException("User not found with id: " + cdsId);
        }

        return fetchedUser.get();
    }

    @Override
    public LogInResponse logIn(LogInRequest logInRequest) throws UserException {

        LogInResponse logInResponse = new LogInResponse();


        UserInfo fetchedUser = getUserByCdsId(logInRequest.getCdsId());
        if(fetchedUser == null) {
            throw new UserException("User not found with id: " + logInRequest.getCdsId());
        }
        logInResponse.setCdsId(fetchedUser.getCdsID());
        logInResponse.setMessage("Valid User");
        logInResponse.setRole(fetchedUser.getRole());
        logInResponse.setFirstName(fetchedUser.getFirstName());
        logInResponse.setLastName(fetchedUser.getLastName());

        return logInResponse;
    }
}
