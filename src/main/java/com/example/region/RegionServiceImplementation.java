package com.example.region;

import com.example.user.UserException;
import com.example.user.UserInfo;
import com.example.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionServiceImplementation implements RegionService {

    private final RegionRepository regionRepository;
    private final UserRepository userRepository;

    @Autowired
    public RegionServiceImplementation(RegionRepository regionRepository, UserRepository userRepository) {
        this.regionRepository = regionRepository;
        this.userRepository = userRepository;
    }

    public Region createRegion(Region newRegion) {
        return regionRepository.save(newRegion);
    }

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public Region getRegionById(Long regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new RuntimeException("Region not found with id: " + regionId));
    }

    public void deleteRegion(Long regionId) {
        regionRepository.deleteById(regionId);
    }

    @Override
    public Region getRegionByName(String regionName) throws RegionException {
        Region fetchedRegion =  regionRepository.findRegionsByRegionNameIgnoreCase(regionName);
        if(fetchedRegion == null) {
            throw new RegionException("Region not found with name: " + regionName);
        }
        return fetchedRegion;
    }

    @Override
    public Region addUserToRegionByCdsId(String regionName, String csdId) throws RegionException, UserException {

        Region fetchedRegion =  regionRepository.findRegionsByRegionNameIgnoreCase(regionName);

        if(fetchedRegion == null) {
            throw new RegionException("Region not found with name: " + regionName);
        }

        Optional<UserInfo> fetchedUser = userRepository.findByCdsID(csdId);

        if(fetchedUser.isEmpty())
            throw new UserException("User not found with id: " + csdId);

        fetchedUser.ifPresent(u -> u.setRegion(fetchedRegion));

        fetchedRegion.getUsers().add(fetchedUser.get());

        return regionRepository.save(fetchedRegion);
    }
}
