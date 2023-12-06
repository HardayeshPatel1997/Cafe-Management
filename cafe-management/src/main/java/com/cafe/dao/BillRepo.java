package com.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.cafe.entity.Bill;

public interface BillRepo extends JpaRepository<Bill, Integer> {

	List<Bill> getAllBills();

	List<Bill> getBillByUsername(@Param("username") String username);

}
