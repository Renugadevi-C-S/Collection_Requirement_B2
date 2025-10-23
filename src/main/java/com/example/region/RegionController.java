package com.example.region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
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

    @GetMapping("/name/{regionName}")
    public Region getRegionByName(@PathVariable String regionName) {
        return regionService.getRegionByName(regionName);
    }

    @PostMapping("/{regionName}/add-user/{csdId}")
    public Region addUserToRegion(@PathVariable String regionName, @PathVariable String csdId) {
        return regionService.addUserToRegionByCdsId(regionName, csdId);
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
