package com.wiseach.teamlog.db;

import java.sql.Connection;

/**
 * User: Arlen Tan
 * 12-8-28 上午10:58
 */
public class LongDBCallback implements DBCallback<Long> {
    protected Long result;
    @Override
    public void doAction(Connection connection) {

    }

    @Override
    public Long getResult() {
        return result;
    }
}
