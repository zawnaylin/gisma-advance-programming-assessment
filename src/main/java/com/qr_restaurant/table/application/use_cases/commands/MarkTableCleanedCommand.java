package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.vo.TableId;

public interface MarkTableCleanedCommand {
    void execute(TableId id);
}
