package com.example.region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImplementation implements RegionService {

    private final RegionRepository regionRepository;

    @Autowired
    public RegionServiceImplementation(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
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
}
