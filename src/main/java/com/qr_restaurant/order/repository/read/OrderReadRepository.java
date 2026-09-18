package com.qr_restaurant.order.repository.read;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.table.application.vo.DiningSessionId;

import java.util.List;
import java.util.Optional;

/**
 * Reads orders. Commands and queries depend on this rather than on Spring Data,
 * so the persistence choice stays behind the repository.
 */
public interface OrderReadRepository {

    /**
     * @return every one of them, in no particular order
     */
    List<Order> findAll();

    /**
     * @param id the id to look for
     * @return the matching one, or empty if there is none
     */
    Optional<Order> findById(OrderId id);

    /**
     * @param diningSessionId the session to list orders for
     * @return every order placed in that session, whatever its status
     */
    List<Order> findByDiningSessionId(DiningSessionId diningSessionId);
}
