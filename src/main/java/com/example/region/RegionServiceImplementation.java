package com.example.region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImplementation implements RegionService {

    private final RegionRepository regionRepository;

    @Autowired
    public RegionServiceImplementation(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public Region addRegion(Region region) {
        return regionRepository.save(region);
    }
}
