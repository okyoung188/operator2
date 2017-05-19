package com.trafficcast.operator.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import com.trafficcast.operator.utils.TOMIConfig;

public class DBConnector {
    private static DBConnector instance = new DBConnector();

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private static final String DB_DRIVER = "jdbc:mysql";    
    
    private DBConnector() {
	
    }

    public static DBConnector getInstance() {
	return instance;
    }
 
    public Connection connectToIncidentDB() throws Exception {	
	String path = DB_DRIVER + "://" + TOMIConfig.getInstance().getIncDBHost() + ":" + TOMIConfig.getInstance().getIncDBPort() + "/" + TOMIConfig.getInstance().getIncDBSchema() + "?dontTrackOpenResources=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8";
	Class.forName(JDBC_DRIVER).newInstance();
	return DriverManager.getConnection(path, TOMIConfig.getInstance().getIncDBUsername(), TOMIConfig.getInstance().getIncDBPassword());
    }
    
    public Connection connectToGeocodingDB() throws Exception {
	String path = DB_DRIVER + "://" + TOMIConfig.getInstance().getGeoDBHost() + ":" + TOMIConfig.getInstance().getGeoDBPort() + "/" + TOMIConfig.getInstance().getGeoDBSchema() + "?dontTrackOpenResources=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8";
	Class.forName(JDBC_DRIVER).newInstance();
	return DriverManager.getConnection(path, TOMIConfig.getInstance().getGeoDBUsername(), TOMIConfig.getInstance().getGeoDBPassword());
    }
    
    public Connection connectToNTLinksDB() throws Exception {
	String path = DB_DRIVER + "://" + TOMIConfig.getInstance().getNtlinksDBHost() + ":" + TOMIConfig.getInstance().getNtlinksDBPort() + "/" + TOMIConfig.getInstance().getNtlinksDBSchema() + "?dontTrackOpenResources=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8";
	Class.forName(JDBC_DRIVER).newInstance();
	return DriverManager.getConnection(path, TOMIConfig.getInstance().getNtlinksDBUsername(), TOMIConfig.getInstance().getNtlinksDBPassword());
    }
   
    public Connection connectToCarmaDB() throws Exception {
	String path = DB_DRIVER + "://" + TOMIConfig.getInstance().getCarmaDBHost() + ":" + TOMIConfig.getInstance().getCarmaDBPort() + "/" + TOMIConfig.getInstance().getCarmaDBSchema() + "?dontTrackOpenResources=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8";
	Class.forName(JDBC_DRIVER).newInstance();
	return DriverManager.getConnection(path, TOMIConfig.getInstance().getCarmaDBUsername(), TOMIConfig.getInstance().getCarmaDBPassword());
    }
}