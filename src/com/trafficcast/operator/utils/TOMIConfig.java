package com.trafficcast.operator.utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TOMIConfig {

    private static TOMIConfig config = new TOMIConfig();
    
    private String incDBHost;
    
    private String incDBPort;
    
    private String incDBUsername;
    
    private String incDBPassword;
    
    private String incDBSchema;
    
    private String geoDBHost;
    
    private String geoDBPort;
    
    private String geoDBUsername;
    
    private String geoDBPassword;
    
    private String geoDBSchema;
    
    private String ntlinksDBHost;
    
    private String ntlinksDBPort;
    
    private String ntlinksDBUsername;
    
    private String ntlinksDBPassword;
    
    private String ntlinksDBSchema;
    
    private String carmaDBHost;
    
    private String carmaDBPort;
    
    private String carmaDBUsername;
    
    private String carmaDBPassword;
    
    private String carmaDBSchema;
    
    private String ntMapVersion;
    
    private List<String> tomiCountries;
    
    private TOMIConfig() {
	try {
        	Properties properties = new Properties();	
        	InputStream is = this.getClass().getClassLoader().getResourceAsStream("conf/operator.properties");
        	properties.load(is);
        	this.incDBHost = properties.getProperty("INC_DB_HOST");
        	this.incDBPort = properties.getProperty("INC_DB_PORT");
        	this.incDBUsername = properties.getProperty("INC_DB_USERNAME");
        	this.incDBPassword = properties.getProperty("INC_DB_PASSWORD");
        	this.incDBSchema = properties.getProperty("INC_SCHEMA");
        	this.geoDBHost = properties.getProperty("GEOCODING_DB_HOST");
        	this.geoDBPort = properties.getProperty("GEOCODING_DB_PORT");
        	this.geoDBUsername = properties.getProperty("GEOCODING_DB_USERNAME");
        	this.geoDBPassword = properties.getProperty("GEOCODING_DB_PASSWORD");
        	this.geoDBSchema = properties.getProperty("GEOCODING_SCHEMA");
        	this.ntlinksDBHost = properties.getProperty("NTLINKS_DB_HOST");
        	this.ntlinksDBPort = properties.getProperty("NTLINKS_DB_PORT");
        	this.ntlinksDBUsername = properties.getProperty("NTLINKS_DB_USERNAME");
        	this.ntlinksDBPassword = properties.getProperty("NTLINKS_DB_PASSWORD");
        	this.ntlinksDBSchema = properties.getProperty("NTLINKS_SCHEMA");
        	this.carmaDBHost = properties.getProperty("CARMA_DB_HOST");
        	this.carmaDBPort = properties.getProperty("CARMA_DB_PORT");
        	this.carmaDBUsername = properties.getProperty("CARMA_DB_USERNAME");
        	this.carmaDBPassword = properties.getProperty("CARMA_DB_PASSWORD");
        	this.carmaDBSchema = properties.getProperty("CARMA_SCHEMA");
        	this.ntMapVersion = properties.getProperty("NT_MAP_VERSION");
        	tomiCountries = Arrays.asList(properties.getProperty("TOMI_COUNTRY").split(","));
        	is.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    public static TOMIConfig getInstance() {
	return config;
    }
    
    public List<String> getTomiCountries() {
        return tomiCountries;
    }

    public String getIncDBHost() {
        return incDBHost;
    }

    public String getIncDBPort() {
        return incDBPort;
    }

    public String getIncDBUsername() {
        return incDBUsername;
    }

    public String getIncDBPassword() {
        return incDBPassword;
    }

    public String getIncDBSchema() {
        return incDBSchema;
    }

    public String getGeoDBHost() {
        return geoDBHost;
    }

    public String getGeoDBPort() {
        return geoDBPort;
    }

    public String getGeoDBUsername() {
        return geoDBUsername;
    }

    public String getGeoDBPassword() {
        return geoDBPassword;
    }

    public String getGeoDBSchema() {
        return geoDBSchema;
    }

    public String getNtlinksDBHost() {
        return ntlinksDBHost;
    }

    public String getNtlinksDBPort() {
        return ntlinksDBPort;
    }

    public String getNtlinksDBUsername() {
        return ntlinksDBUsername;
    }

    public String getNtlinksDBPassword() {
        return ntlinksDBPassword;
    }

    public String getNtlinksDBSchema() {
        return ntlinksDBSchema;
    }

    public String getNtMapVersion() {
        return ntMapVersion;
    }

    public static TOMIConfig getConfig() {
        return config;
    }

    public String getCarmaDBHost() {
        return carmaDBHost;
    }

    public String getCarmaDBPort() {
        return carmaDBPort;
    }

    public String getCarmaDBUsername() {
        return carmaDBUsername;
    }

    public String getCarmaDBPassword() {
        return carmaDBPassword;
    }

    public String getCarmaDBSchema() {
        return carmaDBSchema;
    }
    
}
