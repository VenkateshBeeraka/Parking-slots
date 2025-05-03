package com.fission.learn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fission.learn.entity.Building;

public interface BuildingRepository extends JpaRepository<Building, Integer> {

	List<Building> findByPincode(Integer pincode);
}
