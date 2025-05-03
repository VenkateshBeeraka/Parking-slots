package com.fission.learn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fission.learn.entity.Slot;

public interface SlotsRepository extends JpaRepository<Slot, Integer> {

}
