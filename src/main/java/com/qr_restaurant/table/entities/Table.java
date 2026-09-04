package com.qr_restaurant.table.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.table.enums.TableStatus;
import com.qr_restaurant.table.vo.TableId;


public class Table extends Domain<TableId> {
    private int capacity;
    private TableStatus status;
}
