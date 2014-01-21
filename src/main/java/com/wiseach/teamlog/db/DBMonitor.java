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
            "CREATE TABLE tag" +
                    "(" +
                    "    id bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    name varchar(150)," +
                    "    type integer" +
                    ")",
            "CREATE TABLE worklog" +
                    "(" +
                    "    id bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    userId bigint," +
                    "    description varchar(150)," +
                    "    startTime timestamp," +
                    "    endTime timestamp," +
                    "    tags varchar(150)," +
                    "    tagId bigint," +
                    "    nice integer," +
                    "    comments integer," +
                    "    createTime timestamp" +
                    ")",
            "CREATE TABLE user" +
                    "(" +
                    "    id integer AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    username varchar(50)," +
                    "    email varchar(100)," +
                    "    password varchar(100)," +
                    "    activateUUID varchar(100)," +
                    "    resetUUID varchar(100)," +
                    "    createTime timestamp," +
                    "    disabled boolean" +
                    ")",
            "CREATE TABLE userRelation" +
                    "(" +
                    "    id bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    userId bigint," +
                    "    shareToUserId bigint" +
                    ")",
            "CREATE TABLE rank" +
                    "(" +
                    "    id bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    userId bigint," +
                    "    referId bigint," +
                    "    createTime timestamp" +
                    ")",
            "CREATE TABLE comment" +
                    "(" +
                    "    id bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    userId bigint," +
                    "    description varchar(150)," +
                    "    createTime timestamp," +
                    "    referId bigint" +
                    ")",
            "CREATE TABLE userInfo" +
                    "(" +
                    "    id bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    description varchar(200)," +
                    "    telephone varchar(100)," +
                    "    mobile varchar(100)," +
                    "    qq varchar(40)," +
                    "    note varchar(400)," +
                    "    avatar varchar(200)" +
                    ")",
            "CREATE TABLE onlineUser" +
                    "(" +
                    "    cookieKey varchar(50) PRIMARY KEY NOT NULL," +
                    "    userId bigint," +
                    "    username varchar(50)," +
                    "    ip varchar(50)," +
                    "    expiredTime timestamp" +
                    ")",
            "CREATE TABLE referTags" +
                    "(" +
                    "    id bigint AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                    "    tagId bigint," +
                    "    referId bigint," +
                    "    referType integer" +
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
