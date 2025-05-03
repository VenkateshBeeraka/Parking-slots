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

import com.fission.learn.DTO.SlotDTO;
import com.fission.learn.entity.Building;
import com.fission.learn.entity.Slot;
import com.fission.learn.entity.User;
import com.fission.learn.exceptions.ResourceNotFoundException;
import com.fission.learn.repository.AuthenticationRepository;
import com.fission.learn.repository.BuildingRepository;
import com.fission.learn.repository.SlotsRepository;

@Service
public class SlotService {

	@Autowired
	private AuthenticationRepository authenticationRepository;
	@Autowired
	private BuildingRepository buildingRepository;
	@Autowired
	private SlotsRepository slotRepository;

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

	public String addSlot(String buildingNumber, SlotDTO dto) {
		Slot slot = new Slot();
		int id = idheader();
		User us = authenticationRepository.findById(id).orElse(null);
		if (us == null) {
			throw new ResourceNotFoundException("No user is present in this ID " + id);
		}
		List<Building> li = us.getBuildings();
		Iterator<Building> itr = li.iterator();
		Building bld = null;
		while (itr.hasNext()) {
			bld = itr.next();

			if (bld.getBuildingNumber().equals(buildingNumber)) {

				List<Slot> sl = bld.getSlots();
				slot.setSlotNumber(dto.getSlotNumber());
				slot.setAvailability(dto.getAvailability());
				sl.add(slot);
				slotRepository.saveAll(sl);
				buildingRepository.save(bld);
				return "Slot added to building " + buildingNumber;
			}
		}
		throw new ResourceNotFoundException("No Building are present with building Number " + buildingNumber);
	}

	public List<SlotDTO> getAllSlots(String buildingNumber) {
		List<SlotDTO> list = new ArrayList<SlotDTO>();

		User us = authenticationRepository.findById(idheader()).orElse(null);
		List<Building> listofBuildings = us.getBuildings();
		Iterator<Building> itr = listofBuildings.iterator();
		Building bld = null;
		while (itr.hasNext()) {
			bld = itr.next();
			if (bld.getBuildingNumber().equals(buildingNumber)) {
				List<Slot> slot = bld.getSlots();
				for (Slot s : slot) {
					list.add(new SlotDTO(s.getId(), s.getSlotNumber(), s.getFloornumber(), s.getDivisionNo(),
							s.getAvailability()));
				}
				return list;
			}
		}
		if (listofBuildings.isEmpty()) {
			throw new ResourceNotFoundException("No buildings associated with user");
		}

		throw new ResourceNotFoundException("Invalid Building number");

	}

	public String removeSlots(String buildNumber, String slotNumber) {
		User us = authenticationRepository.findById(idheader()).orElse(null);
		List<Building> li = us.getBuildings();
		Iterator<Building> itr = li.iterator();
		while (itr.hasNext()) {
			Building bld = itr.next();
			if (bld.getBuildingNumber().equals(buildNumber)) {
				List<Slot> ls = bld.getSlots();
				Iterator<Slot> its = ls.iterator();
				Slot sl = null;
				while (its.hasNext()) {
					sl = its.next();
					if (sl.getSlotNumber().equals(slotNumber)) {
						ls.remove(sl);
						authenticationRepository.save(us);
						slotRepository.delete(sl);
						return "slot deleted with buildingnumber " + buildNumber;
					}
				}

			}
		}
		throw new ResourceNotFoundException("Building not found with building Number " + buildNumber);

	}

	public String updateSlot(String buildNumber, String slotNumber, SlotDTO dto) {

		User us = authenticationRepository.findById(idheader()).orElse(null);
		List<Building> li = us.getBuildings();
		Iterator<Building> itr = li.iterator();
		Building bld = null;
		while (itr.hasNext()) {
			bld = itr.next();
			if (bld.getBuildingNumber().equals(buildNumber)) {
				List<Slot> ls = bld.getSlots();
				Iterator<Slot> its = ls.iterator();
				Slot sl = null;
				while (its.hasNext()) {
					sl = its.next();
					if (sl.getSlotNumber().equals(slotNumber)) {

						if (sl.getSlotNumber() != null) {
							sl.setSlotNumber(dto.getSlotNumber());
						}
						if (sl.getDivisionNo() != null) {
							sl.setDivisionNo(dto.getDivisionNo());
						}
						if (sl.getFloornumber() != null) {
							sl.setFloornumber(dto.getFloornumber());
						}

						slotRepository.save(sl);
						buildingRepository.save(bld);
						return "slot updated with slotNumber" + slotNumber;
					}
				}

			}
		}
		throw new ResourceNotFoundException("Invalid buildingNumber or slot Id");
	}

}
