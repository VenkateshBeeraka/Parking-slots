package com.fission.learn.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fission.learn.DTO.BuildingDTO;
import com.fission.learn.entity.Building;
import com.fission.learn.entity.User;
import com.fission.learn.exceptions.ResourceNotFoundException;
import com.fission.learn.repository.AuthenticationRepository;
import com.fission.learn.repository.BuildingRepository;

@Service
public class BuildingService {

	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Autowired
	private BuildingRepository buildingRepository;

	public int idheader() {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Integer userId = (Integer) token.getPrincipal();
		return userId;
	}

	public String getRoleHeader() {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		Collection<GrantedAuthority> role = token.getAuthorities();
		for (GrantedAuthority r : role) {
			if (r.getAuthority().equals("admin"))
				return "admin";
		}
		return "user";
	}

	public void createBuilding(BuildingDTO dto) {
		Integer userId = idheader();
		User us = authenticationRepository.findById(userId).orElse(null);
		if (us != null) {

			List<Building> buildings = buildingRepository.findAll();
			Iterator<Building> itrb = buildings.iterator();
			while (itrb.hasNext()) {

				Building bld = itrb.next();
				if (bld.getBuildingNumber().equals(dto.getBuildingNumber())) {
					throw new ResourceNotFoundException("building number cannot be duplicate");
				}
			}
			Building build = new Building();
			build.setBuildingNumber(dto.getBuildingNumber());
			build.setBuildingName(dto.getBuildingName());
			build.setArea(dto.getArea());
			build.setTown(dto.getTown());
			build.setState(dto.getState());
			build.setLandmark(dto.getLandmark());
			build.setPincode(dto.getPincode());
			build.setSlots(dto.getSlots());;
			us.getBuildings().add(build);
			authenticationRepository.save(us);
		}
	}

	public List<BuildingDTO> findbuildings() {
		List<BuildingDTO> list = new ArrayList<>();
		if (getRoleHeader().equals("admin")) {
			User user = authenticationRepository.getById(idheader());

			if (user.getBuildings().isEmpty()) {
				throw new ResourceNotFoundException("No Buildings Associated to user");
			} else {
				List<Building> buildings = user.getBuildings();
				for (Building build : buildings) {
					list.add(new BuildingDTO(build.getId(), build.getBuildingNumber(), build.getBuildingName(),
							build.getArea(), build.getTown(), build.getState(), build.getLandmark(),
							build.getPincode()));
				}
				return list;
			}
		} else {
			if (buildingRepository.findAll().isEmpty()) {
				throw new ResourceNotFoundException("No buildings Available");
			} else {

				List<Building> bld = buildingRepository.findAll();
				for (Building build : bld) {
					list.add(new BuildingDTO(build.getId(), build.getBuildingNumber(), build.getBuildingName(),
							build.getArea(), build.getTown(), build.getState(), build.getLandmark(),
							build.getPincode()));
				}
				return list;
			}
		}
	}

	public List<BuildingDTO> getByBuildingPinCode(Integer pin) {
		ArrayList<BuildingDTO> listofbuildings = new ArrayList<BuildingDTO>();

		if (getRoleHeader().equals("admin")) {
			User us = authenticationRepository.findById(idheader()).orElse(null);
			List<Building> lb = us.getBuildings();
			Iterator<Building> itr = lb.iterator();
			while (itr.hasNext()) {
				Building build = itr.next();

				if (build.getPincode().equals(pin)) {

					listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
							build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
							build.getLandmark(), build.getPincode()));
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid pincode this " + pin + " is not valid");
			} else

				return listofbuildings;

		} else {
			List<Building> listb = buildingRepository.findAll();
			Iterator<Building> itr = listb.iterator();
			while (itr.hasNext()) {

				Building build = itr.next();

				if (build.getPincode().equals(pin)) {

					listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
							build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
							build.getLandmark(), build.getPincode()));
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid pincode this " + pin + " is not valid");
			} else

				return listofbuildings;
		}
	}

	public List<BuildingDTO> getByBuildingName(String name) {

		ArrayList<BuildingDTO> listofbuildings = new ArrayList<BuildingDTO>();

		if (getRoleHeader().equals("admin")) {
			User us = authenticationRepository.findById(idheader()).orElse(null);
			List<Building> lb = us.getBuildings();

			Iterator<Building> itr = lb.iterator();
			while (itr.hasNext()) {
				Building build = itr.next();
				if (build.getBuildingName().equals(name)) {
					listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
							build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
							build.getLandmark(), build.getPincode()));
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid BuildingName this " + name + " is not valid");
			} else {

				return listofbuildings;
			}
		} else {
			List<Building> listB = buildingRepository.findAll();
			Iterator<Building> itr = listB.iterator();
			while (itr.hasNext()) {
				Building build = itr.next();
				if (build.getBuildingName().equals(name)) {
					listofbuildings.add(new BuildingDTO(build.getId(), build.getBuildingNumber(),
							build.getBuildingName(), build.getArea(), build.getTown(), build.getState(),
							build.getLandmark(), build.getPincode()));
				}
			}
			if (listofbuildings.isEmpty()) {
				throw new ResourceNotFoundException("Enter Valid BuildingName this " + name + " is not valid");
			} else {

				return listofbuildings;
			}

		}
	}

	public String deleteBuilding(String buildingNumber) {

		User us = authenticationRepository.findById(idheader()).orElse(null);
		List<Building> li = us.getBuildings();
		Iterator<Building> itr = li.iterator();

		while (itr.hasNext()) {
			Building bld = itr.next();
			if (bld.getBuildingNumber().equals(buildingNumber)) {

				li.remove(bld);
				authenticationRepository.save(us);
				buildingRepository.delete(bld);
				return "Building deleted with building number " + buildingNumber;
			}
		}
		throw new ResourceNotFoundException("No Building Found with Building Number " + buildingNumber);
	}

	public String updateBuildings(String buildingNumber, BuildingDTO dto) {

		Integer userId = idheader();

		User us = authenticationRepository.findById(userId).orElse(null);

		List<Building> lb = us.getBuildings();
		Building building = null;
		Iterator<Building> itr = lb.iterator();
		while (itr.hasNext()) {

			building = itr.next();
			if (building.getBuildingNumber().equals(buildingNumber)) {

				if (dto.getArea() != null) {
					building.setArea(dto.getArea());
				}
				if (dto.getBuildingName() != null) {
					building.setBuildingName(dto.getBuildingName());
				}
				if (dto.getLandmark() != null) {
					building.setLandmark(dto.getLandmark());
				}
				if (dto.getPincode() != null) {
					building.setPincode(dto.getPincode());
				}
				if (dto.getState() != null) {
					building.setState(dto.getState());
				}
				if (dto.getTown() != null) {
					building.setTown(dto.getTown());
				}
				authenticationRepository.save(us);
				return "Sucessfully updated";
			}

		}
		throw new ResourceNotFoundException("Invalid Building Number");

	}}
