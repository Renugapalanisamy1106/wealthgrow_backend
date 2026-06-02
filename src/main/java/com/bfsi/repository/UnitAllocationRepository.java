package com.bfsi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bfsi.entity.UnitAllocation;

@Repository
public interface UnitAllocationRepository
        extends JpaRepository<UnitAllocation, String> {
}