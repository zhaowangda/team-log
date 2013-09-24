package com.wiseach.teamlog.db;

import com.wiseach.teamlog.Constants;
import com.wiseach.teamlog.utils.TeamlogLocalizationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: Arlen Tan
 * 12-8-10 上午11:37
 */
public class DBPool {

    public static final String DB_MODEL = TeamlogLocalizationUtils.getSystemParams("db.model").toLowerCase();
    private static JdbcConnectionPool connectionPool;
    private static final ThreadLocal<Connection> connections= new ThreadLocal<Connection>();
    private static final Log log = LogFactory.getLog(DBPool.class);
    private static final String DBURL_TCP = "jdbc:h2:tcp://localhost/~/teamlog";
    private static final String DBURL_EMBEDDED = "jdbc:h2:~/teamlog";
//    private static String DBURL_TCP = "jdbc:h2:tcp://localhost/C:\\projects\\teamlog\\sources\\db\\teamlog";
    public static final String SA = "sa";
    public static final String TCP_LOCALHOST_9092 = "tcp://localhost:9092";
    public static Server TCP_SERVER;

    public static void startServer() {
        try {
            if (TCP_SERVER==null || !TCP_SERVER.isRunning(true)) {
                TCP_SERVER = Server.createTcpServer();
                TCP_SERVER.start();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connectionPool = JdbcConnectionPool.create(DB_MODEL.equals("embedded")?DBURL_EMBEDDED:DBURL_TCP, SA, SA);
        DBMonitor.initialDB();
    }

    public static void stopServer() {
        try {
            Connection connection = getConnection();
            Statement stat = connection.createStatement();
            stat.execute("SHUTDOWN");
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        connectionPool.dispose();
        connectionPool=null;
        try {
            Server.shutdownTcpServer(TCP_LOCALHOST_9092, Constants.EMPTY_STRING,true,true);
            if (TCP_SERVER!=null) TCP_SERVER.shutdown();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
