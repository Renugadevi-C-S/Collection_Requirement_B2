package com.example.region;

import com.example.user.UserException;

import java.util.List;

public interface RegionService {

    Region createRegion(Region region);
    List<Region> getAllRegions();
    Region getRegionById(Long id);
    void deleteRegion(Long id);

    Region getRegionByName(String regionName) throws RegionException;

    Region addUserToRegionByCdsId(String regionName, String csdId) throws RegionException, UserException;


}
