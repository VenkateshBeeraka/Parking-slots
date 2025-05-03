package com.fission.learn.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fission.learn.DTO.BuildingDTO;
import com.fission.learn.service.BuildingService;

@RestController
public class BuildingController {

	@Autowired
	private BuildingService buildingService;

	@PostMapping(path = RestUri.BUILDING)
	@PreAuthorize("hasAuthority('admin')")
	public ResponseEntity<String> addBuilding(@Validated @RequestBody BuildingDTO dto) {
		buildingService.createBuilding(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body("Building Added");
	}

	@GetMapping(path = RestUri.BUILDINGS)
	public ResponseEntity<List<BuildingDTO>> Buildings() {
		return ResponseEntity.status(HttpStatus.FOUND).body(buildingService.findbuildings());
	}

//	@PreAuthorize("hasAuthority('admin')")
	@GetMapping(path = RestUri.BUILDING_PIN)
	public ResponseEntity<List<BuildingDTO>> getBuidingsbypincode(@PathVariable Integer pincode) {
		return ResponseEntity.status(HttpStatus.FOUND).body(buildingService.getByBuildingPinCode(pincode));
	}

//	@PreAuthorize("hasAuthority('admin')")
	@GetMapping(path = RestUri.BUILDING_NAME)
	public ResponseEntity<List<BuildingDTO>> getBuidingsbyname(@PathVariable String buildingName) {
		return ResponseEntity.status(HttpStatus.FOUND).body(buildingService.getByBuildingName(buildingName));
	}

	@PreAuthorize("hasAuthority('admin')")
	@PutMapping(path = RestUri.BUILDING_CHANGES)
	public ResponseEntity<String> updatebuilding(@PathVariable String buildingNumber, @RequestBody BuildingDTO dto) {
		buildingService.updateBuildings(buildingNumber, dto);
		return ResponseEntity.status(HttpStatus.OK).body("Building updated");
	}

	@PreAuthorize("hasAuthority('admin')")
	@DeleteMapping(path = RestUri.BUILDING_CHANGES)
	public ResponseEntity<String> deleteBuildingbyno(@PathVariable String buildingNumber) {
		buildingService.deleteBuilding(buildingNumber);
		return ResponseEntity.status(HttpStatus.OK).body("Building Deleted");
	}
}
