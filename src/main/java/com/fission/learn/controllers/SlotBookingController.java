package com.fission.learn.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fission.learn.service.SlotBookingService;

@RestController
public class SlotBookingController {

	@Autowired
	private SlotBookingService slotBookingService;

	@PreAuthorize("hasAuthority('user')")
	@PostMapping(path = RestUri.BOOKING)
	public ResponseEntity<String> bookslot(@PathVariable String buildingNumber, @PathVariable String slotNumber,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		return ResponseEntity.status(HttpStatus.OK).body(slotBookingService.bookSlot(buildingNumber, slotNumber, date));
	}

	@DeleteMapping(path = RestUri.CANCEL_BOOKING)
	public ResponseEntity<String> cancleBooking(@PathVariable Integer id) {
		slotBookingService.cancelSlotBook(id);
		return ResponseEntity.status(HttpStatus.OK).body("Booking Cancelled with slot_bookingId " + id);
	}
}
