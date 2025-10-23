package com.example.region;

import java.util.List;

public interface RegionService {

    Region createRegion(Region region);
    List<Region> getAllRegions();
    Region getRegionById(Long id);
    void deleteRegion(Long id);

}
