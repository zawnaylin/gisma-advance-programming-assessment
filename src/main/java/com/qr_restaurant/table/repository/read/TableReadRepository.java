package com.qr_restaurant.table.repository.read;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;

import java.util.List;
import java.util.Optional;

/**
 * Reads tables. Commands and queries depend on this rather than on Spring Data,
 * so the persistence choice stays behind the repository.
 */
public interface TableReadRepository {

    /**
     * @return every one of them, in no particular order
     */
    List<Table> findAll();

    /**
     * @param id the id to look for
     * @return the matching one, or empty if there is none
     */
    Optional<Table> findById(TableId id);
}
