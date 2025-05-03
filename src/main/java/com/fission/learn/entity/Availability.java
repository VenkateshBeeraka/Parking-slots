package com.fission.learn.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Availability {

	@Id
	@GeneratedValue
	private Integer id;
	private Date fromDate;
	private Date toDate;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = SlotBooking.class)
	@JoinColumn(name = "availabiltyId", referencedColumnName = "id")
	private List<SlotBooking> bookings;

	public Availability() {
		super();
	}

	public Availability(Integer id, Date fromDate, Date toDate, List<SlotBooking> bookings) {
		super();
		this.id = id;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.bookings = bookings;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<SlotBooking> getBookings() {
		return bookings;
	}

	public void setBookings(List<SlotBooking> bookings) {
		this.bookings = bookings;
	}

	@Override
	public String toString() {
		return "Availability [id=" + id + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
}
