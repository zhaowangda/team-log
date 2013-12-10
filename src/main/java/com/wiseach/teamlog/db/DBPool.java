package com.wiseach.teamlog.db;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: Arlen Tan
 * 12-8-10 上午11:37
 */
public class DBPool {

    private static BoneCP connectionPool;
    private static final ThreadLocal<Connection> connections= new ThreadLocal<Connection>();

    public static void startServer() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            BoneCPConfig config = new BoneCPConfig();	// create a new configuration object

            config.setJdbcUrl("jdbc:mysql://localhost:3306/teamlog?useUnicode=yes&characterEncoding=utf8");	// set the JDBC url
            config.setUsername("teamlog_admin");			// set the username
            config.setPassword("teamlog");				// set the password

            connectionPool = new BoneCP(config); 	// setup the connection pool
            DBMonitor.initialDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        connectionPool.shutdown();
    }

    public static final Connection getConnection() {
        Connection c = connections.get();
        if (c==null) {
            try {
                c = connectionPool.getConnection();
                connections.set(c);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return c;
    }

    public static final void closeConnection() {
        Connection c = connections.get();
        try {
            if (c!=null && !c.isClosed()) {
                c.setAutoCommit(true);
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connections.set(null);
    }
}
