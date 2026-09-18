package com.qr_restaurant.table.repository.impl;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaDiningSessionRepository extends JpaRepository<DiningSession, DiningSessionId>, DiningSessionReadRepository {
}
