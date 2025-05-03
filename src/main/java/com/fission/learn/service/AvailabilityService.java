package com.fission.learn.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fission.learn.DTO.AvailabilityDTO;
import com.fission.learn.entity.Availability;
import com.fission.learn.entity.Building;
import com.fission.learn.entity.Slot;
import com.fission.learn.entity.SlotBooking;
import com.fission.learn.entity.User;
import com.fission.learn.exceptions.ResourceNotFoundException;
import com.fission.learn.repository.AuthenticationRepository;
import com.fission.learn.repository.AvailabilityRepository;
import com.fission.learn.repository.BuildingRepository;
import com.fission.learn.repository.SlotsRepository;

@Service
public class AvailabilityService {

	@Autowired
	private AuthenticationRepository authenticationRepository;
	@Autowired
	private BuildingRepository buildingRepository;
	@Autowired
	private SlotsRepository slotRepository;
	@Autowired
	private AvailabilityRepository availabilityRepository;

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

	public String addAvailability(String buildNumber, String slotNo, AvailabilityDTO dto) {
		Availability ava = new Availability();

		User user = authenticationRepository.findById(idheader()).orElse(null);

		List<Building> buildingslist = user.getBuildings();

		Iterator<Building> itr = buildingslist.iterator();

		Building buil = null;
		while (itr.hasNext()) {
			buil = itr.next();
			if (buil.getBuildingNumber().equals(buildNumber)) {
				Slot sl = null;
				List<Slot> slots = buil.getSlots();
				Iterator<Slot> itrs = slots.iterator();

				while (itrs.hasNext()) {
					sl = itrs.next();
					if (sl.getSlotNumber().equals(slotNo)) {
						ava.setFromDate(dto.getFromDate());
						ava.setToDate(dto.getToDate());
						ava.setBookings(dto.getBookings());
						sl.setAvailability(ava);
						slotRepository.save(sl);
						return "avaliability added to buildingNumber " + buildNumber + " Slot Number " + slotNo;
					}
				}
			}
		}
		throw new ResourceNotFoundException("Invalid SlotNumber or Building Number");

	}

	public List<String> findAvailability(String buildingNumber, Date date) {
		String role = getRoleHeader();
		Integer userId = idheader();
		ArrayList<String> slots = new ArrayList<String>();
		if (role.equals("admin")) {
			User user = authenticationRepository.findById(userId).orElse(null);
			if (user.getBuildings().isEmpty())
				throw new ResourceNotFoundException("Building are not present for this Admin");
			List<Building> listB = user.getBuildings();
			Building building = null;
			Iterator<Building> itb = listB.iterator();
			while (itb.hasNext()) {
				building = itb.next();
				if (building.getBuildingNumber().equals(buildingNumber)) {
					if (building.getSlots().isEmpty()) {
						throw new ResourceNotFoundException("No Slots Available for This Building");
					}
					List<Slot> listS = building.getSlots();
					Iterator<Slot> its = listS.iterator();
					Slot slot = null;

					while (its.hasNext()) {
						slot = its.next();
						if (slot.getAvailability() != null) {
							Availability ava = slot.getAvailability();
							if (date.after(ava.getFromDate()) && date.before(ava.getToDate())) {
								if (ava.getBookings().isEmpty()) {
									slots.add(slot.getSlotNumber());
								} else {
									List<SlotBooking> listBook = ava.getBookings();
									Iterator<SlotBooking> it = listBook.iterator();
									SlotBooking bk;
									boolean b = true;
									while (it.hasNext()) {
										bk = it.next();
										if (bk.getBookingDate().equals(date)) {
											b = false;
											break;
										}
									}
									if (b) {
										slots.add(slot.getSlotNumber());
									}
								}

							}
						}
					}
					if (slots.isEmpty()) {
						throw new ResourceNotFoundException("No Slots are present in that Building");
					} else
						return slots;
				}
				throw new ResourceNotFoundException("Invalid Building Id");
			}
		} else

		{
			List<String> result = new ArrayList<String>();
			List<Building> list = buildingRepository.findAll();
			Iterator<Building> itB = list.iterator();
			Building building = null;
			while (itB.hasNext()) {
				building = itB.next();
				if (building.getBuildingNumber().equals(buildingNumber)) {

					List<Slot> listS = building.getSlots();
					Iterator<Slot> itS = listS.iterator();
					Slot s = null;
					while (itS.hasNext()) {
						s = itS.next();
						if (s.getAvailability() != null) {
							Availability availability = s.getAvailability();
							if (date.after(availability.getFromDate()) && date.before(availability.getToDate())) {
								if (availability.getBookings().isEmpty()) {
									result.add(s.getSlotNumber());
								} else {
									List<SlotBooking> listBook = availability.getBookings();
									Iterator<SlotBooking> it = listBook.iterator();
									SlotBooking bk;
									boolean b = true;
									while (it.hasNext()) {
										bk = it.next();
										if (bk.getBookingDate().equals(date)) {
											b = false;
											break;
										}
									}
									if (b) {
										result.add(s.getSlotNumber());
									}
								}

							}

						}
					}
					if (result.isEmpty())
						throw new ResourceNotFoundException("No Slots are present in that Building for booking");
					else
						return result;
				}
			}
			throw new ResourceNotFoundException("No Buildings Available for this user");
		}

		throw new ResourceNotFoundException("role not found");

	}

	public String deleteAvailability(String buildNumber, String slotNo) {
		Integer userId = idheader();
		User us = authenticationRepository.findById(userId).orElse(null);

		if (us.getBuildings().isEmpty()) {
			throw new ResourceNotFoundException("No buildings available");
		}
		List<Building> listB = us.getBuildings();
		Iterator<Building> itB = listB.iterator();
		Building bld = null;
		while (itB.hasNext()) {
			bld = itB.next();
			if (bld.getBuildingNumber().equals(buildNumber)) {
				if (bld.getSlots().isEmpty())
					throw new ResourceNotFoundException("No slots available");
				List<Slot> listS = bld.getSlots();
				Iterator<Slot> itS = listS.iterator();
				Slot slo = null;
				while (itS.hasNext()) {
					slo = itS.next();
					if (slo.getSlotNumber().equals(slotNo)) {
						if (slo.getAvailability().getBookings().isEmpty()) {
							Availability avail = slo.getAvailability();
							slo.setAvailability(null);
							slotRepository.save(slo);
							availabilityRepository.delete(avail);
							return "Deleted Availabilty";
						}
					}
				}
				throw new ResourceNotFoundException("Invalid slot number");

			}
		}
		throw new ResourceNotFoundException("Invalid building number");
	}

}
