package com.fission.learn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fission.learn.entity.Availability;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer>{

}
