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

import com.fission.learn.entity.Availability;
import com.fission.learn.entity.Building;
import com.fission.learn.entity.Slot;
import com.fission.learn.entity.SlotBooking;
import com.fission.learn.entity.User;
import com.fission.learn.exceptions.ResourceNotFoundException;
import com.fission.learn.repository.AuthenticationRepository;
import com.fission.learn.repository.AvailabilityRepository;
import com.fission.learn.repository.BuildingRepository;
import com.fission.learn.repository.SlotBookingRepository;

@Service
public class SlotBookingService {

	@Autowired
	private AuthenticationRepository authenticationRepository;
	@Autowired
	private BuildingRepository buildingRepository;
	@Autowired
	private AvailabilityRepository availabilityRepository;
	@Autowired
	private SlotBookingRepository slotBookingRepository;

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

	public String bookSlot(String buildNumber, String slotNo, Date bookingdate) {
		Integer userId = idheader();
		boolean booking = true;
		List<SlotBooking> abook = new ArrayList<SlotBooking>();
		List<Building> listB = buildingRepository.findAll();
		Iterator<Building> listofBuildings = listB.iterator();
		Building build = null;
		while (listofBuildings.hasNext()) {
			build = listofBuildings.next();
			if (build.getBuildingNumber().equals(buildNumber)) {

				if (build.getSlots().isEmpty()) {
					throw new ResourceNotFoundException("No slots are present in this building");
				}
				List<Slot> slots = build.getSlots();
				Iterator<Slot> lists = slots.iterator();
				Slot slot = null;
				while (lists.hasNext()) {
					slot = lists.next();
					if (slot.getSlotNumber().equals(slotNo)) {
						if (slot.getAvailability() == null) {
							throw new ResourceNotFoundException("no availability for this slot");
						}
						Availability avail = slot.getAvailability();

						if (bookingdate.after(avail.getFromDate()) && bookingdate.before(avail.getToDate())) {

							if (avail.getBookings().isEmpty()) {
								User user = authenticationRepository.getById(userId);
								SlotBooking slotbook = new SlotBooking();
								slotbook.setBookingDate(bookingdate);
								slotbook.setUser(user);
								slotbook.setSlot(slot);
								System.out.println("empty");

								abook.add(slotbook);
								avail.setBookings(abook);

								availabilityRepository.save(avail);

								return "Booking Sucessfull";

							} else {

								Iterator<SlotBooking> itrsb = avail.getBookings().iterator();
								SlotBooking sb = null;
								while (itrsb.hasNext()) {
									sb = itrsb.next();
									if (sb.getBookingDate().equals(bookingdate)) {
										booking = false;
										break;
									}
								}
								if (booking) {
									User user = authenticationRepository.getById(userId);

									SlotBooking slotbook = new SlotBooking();
									slotbook.setBookingDate(bookingdate);
									slotbook.setUser(user);
									slotbook.setSlot(slot);
									abook = avail.getBookings();
									abook.add(slotbook);
									avail.setBookings(abook);
									System.out.println("in");
									availabilityRepository.save(avail);
									return "Booking Sucessfull";
								} else {
									throw new ResourceNotFoundException("Already booked");
								}
							}
						}
						else
							throw new ResourceNotFoundException("not available on the date " + bookingdate);
					}
				}
			}
			
		}
		throw new ResourceNotFoundException("Building Number not valid =" + buildNumber);
	}

	public String cancelSlotBook(Integer id) {
		Integer userId = idheader();
		System.out.println(id);
		SlotBooking booking = slotBookingRepository.findById(id).orElse(null);
		System.out.println(id);
		Date date = new Date();
		if (booking != null) {
			if (booking.getUser().getId().equals(userId)) {
				if (date.compareTo(booking.getBookingDate()) < 0) {
					slotBookingRepository.delete(booking);
					return "Booking Cancelled";
				}
			}
		}
		throw new ResourceNotFoundException("enter valid slot booking Id " + id);
	}
}
