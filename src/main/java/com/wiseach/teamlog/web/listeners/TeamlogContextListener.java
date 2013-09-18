package com.wiseach.teamlog.web.listeners;

import com.wiseach.teamlog.db.DBPool;
import com.wiseach.teamlog.utils.DateUtils;
import com.wiseach.teamlog.web.security.UserAuthProcessor;
import com.wiseach.teamlog.web.timer.ExpiredUserCleaner;
import com.wiseach.teamlog.web.timer.UUIDCleaner;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: Arlen Tan
 * 12-8-12 下午2:18
 */
@WebListener
public class TeamlogContextListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        DBPool.startServer();
        UserAuthProcessor.updateRootUri(servletContextEvent.getServletContext().getContextPath());
        UserAuthProcessor.updateFirstUserStatus();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(new ExpiredUserCleaner(), 0, 60 * 30, TimeUnit.SECONDS);//run per half hour.
        scheduler.scheduleWithFixedDelay(new UUIDCleaner(),DateUtils.getDurationToMidNight(),3600*24,TimeUnit.SECONDS);//run every mid night.
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        scheduler.shutdown();
        DBPool.stopServer();
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
