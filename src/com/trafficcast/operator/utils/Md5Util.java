package com.trafficcast.operator.utils;

import java.security.MessageDigest;

import org.apache.log4j.Logger;

public class Md5Util {
	  private static Logger logger = Logger.getLogger(Md5Util.class);
	  public final static String getMD5(String s) {
	        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
	        try {
	            byte[] btInput = s.getBytes();
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            mdInst.update(btInput);
	            byte[] md = mdInst.digest();
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byteMd5 = md[i];
	                str[k++] = hexDigits[byteMd5 >>> 4 & 0xf];
	                str[k++] = hexDigits[byteMd5 & 0xf];
	            }
	            return new String(str);
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error(e.getMessage());
	            return null;
	        }
	    }
}
