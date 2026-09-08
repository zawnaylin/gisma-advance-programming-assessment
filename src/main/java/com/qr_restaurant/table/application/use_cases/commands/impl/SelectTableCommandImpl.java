package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.vo.TableId;
import org.springframework.stereotype.Service;

@Service
class SelectTableCommandImpl implements SelectTableCommand {

    @Override
    public void execute(TableId id) {
        // TODO: implement
    }
}
