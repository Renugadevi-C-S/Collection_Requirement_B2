package com.example.region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/regions")
public class RegionController {

    private final RegionService regionService;

    @Autowired
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping
    public Region createRegion(@RequestBody Region newRegion) {
        return regionService.createRegion(newRegion);
    }

    @GetMapping("/all")
    public List<Region> getAllRegions() {
        return regionService.getAllRegions();
    }

    @GetMapping("/{regionId}")
    public Region getRegionById(@PathVariable Long regiionId) {
        return regionService.getRegionById(regiionId);
    }

    @DeleteMapping("/delete/{regionId}")
    public void deleteRegion(@PathVariable Long regionId) {
        regionService.deleteRegion(regionId);
    }


}
