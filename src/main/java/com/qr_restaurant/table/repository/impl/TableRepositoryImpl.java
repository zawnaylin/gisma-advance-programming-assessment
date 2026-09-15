package com.qr_restaurant.table.repository.impl;

import com.qr_restaurant.common.InMemoryRepository;
import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Repository;

@Repository
class TableRepositoryImpl extends InMemoryRepository<TableId, Table> implements TableRepository, TableReadRepository {
}
