package com.wiseach.teamlog.db;

import org.apache.commons.dbutils.handlers.MapListHandler;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sean
 * Date: 12-10-12
 * Time: 上午6:25
 * To change this template use File | Settings | File Templates.
 */
public class DBMonitor {
    public static final List<String> initialDDL = Arrays.asList(
            "CREATE TABLE TAG" +
                    "(" +
                    "    ID bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    NAME varchar(150)," +
                    "    type integer" +
                    ")",
            "CREATE TABLE WORKLOG" +
                    "(" +
                    "    ID bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    USERID bigint," +
                    "    DESCRIPTION varchar(150)," +
                    "    STARTTIME timestamp," +
                    "    ENDTIME timestamp," +
                    "    TAGS varchar(150)," +
                    "    tagId bigint," +
                    "    NICE integer," +
                    "    COMMENTS integer," +
                    "    CREATETIME timestamp" +
                    ")",
            "CREATE TABLE USER" +
                    "(" +
                    "    ID integer AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    USERNAME varchar(50)," +
                    "    EMAIL varchar(100)," +
                    "    PASSWORD varchar(100)," +
                    "    ACTIVATEUUID varchar(100)," +
                    "    RESETUUID varchar(100)," +
                    "    CREATETIME timestamp," +
                    "    DISABLED boolean" +
                    ")",
            "CREATE TABLE USERRELATION" +
                    "(" +
                    "    ID bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    USERID bigint," +
                    "    SHARETOUSERID bigint" +
                    ")",
            "CREATE TABLE RANK" +
                    "(" +
                    "    ID bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    USERID bigint," +
                    "    REFERID bigint," +
                    "    CREATETIME timestamp" +
                    ")",
            "CREATE TABLE COMMENT" +
                    "(" +
                    "    ID bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    USERID bigint," +
                    "    DESCRIPTION varchar(150)," +
                    "    CREATETIME timestamp," +
                    "    REFERID bigint" +
                    ")",
            "CREATE TABLE USERINFO" +
                    "(" +
                    "    ID bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    DESCRIPTION varchar(200)," +
                    "    TELEPHONE varchar(100)," +
                    "    MOBILE varchar(100)," +
                    "    QQ varchar(40)," +
                    "    NOTE varchar(400)," +
                    "    AVATAR varchar(200)" +
                    ")",
            "CREATE TABLE ONLINEUSER" +
                    "(" +
                    "    COOKIEKEY varchar(50) PRIMARY KEY NOT NULL," +
                    "    USERID bigint," +
                    "    USERNAME varchar(50)," +
                    "    IP varchar(50)," +
                    "    EXPIREDTIME timestamp" +
                    ")",
            "CREATE TABLE REFERTAGS" +
                    "(" +
                    "    ID bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    TAGID bigint," +
                    "    REFERID bigint," +
                    "    REFERTYPE integer" +
                    ")");
    public static void initialDB() {
        List tables = PublicDBHelper.query("show tables ", new MapListHandler());

        if (tables.size() == 0) {
            for (String ddl : initialDDL) {
                PublicDBHelper.exec(ddl);
            }
        }
    }
}
