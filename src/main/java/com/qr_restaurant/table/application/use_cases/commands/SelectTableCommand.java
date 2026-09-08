package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.vo.TableId;

public interface SelectTableCommand {
    void execute(TableId id);
}
