package com.trafficcast.operator.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.trafficcast.operator.pojo.User;
import com.trafficcast.operator.pojo.UserSetting;

public class UserDAO {
	public static String BY_NAME = "byName";
	
    public static User getUserByName(User user) throws Exception {
	User userObj = new User();
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select *  from operator_user where";
	    
	    if (user.getName() != null && !user.getName().equals("")) {
		sql += " name=?";
	    }
	    if (user.getPassword() != null && !user.getPassword().equals("")) {
		sql += " and password=?";
	    }
	    sql = sql + " and status = 1";	    
	      
	    stmt = con.prepareStatement(sql.toString());
	    int paraCount = 1;
	    if (user.getName() != null && !user.getName().equals("")) {
		stmt.setString(paraCount, user.getName());
		paraCount++;
	    }
	    if (user.getPassword()!= null && !user.getPassword().equals("")) {
		stmt.setString(paraCount, user.getPassword());
		paraCount++;
	    }

	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
			userObj.setId(rs.getInt("id"));
			userObj.setName(rs.getString("name"));
			userObj.setPassword(rs.getString("password"));
			userObj.setRole(rs.getString("role"));
		    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return userObj;
    }
    public static boolean UpdatePassWord(String name ,String oldPassword,String newPassWord) throws Exception {
    	boolean success=false;
    	Connection con = null;
    	PreparedStatement stmt = null;
    	try {
    	    con = DBConnector.getInstance().connectToIncidentDB();
    	    String sql = "update operator_user set ";
    	    if (newPassWord != null && !newPassWord.equals("")) {
    		sql += " password=?";
    	    }
    	    if (name != null && !name.equals("")) {
    		sql += " where name=?";
    	    }
    	    if (oldPassword != null && !oldPassword.equals("")) {
        		sql += "and password=?";
        	 }  
    	    stmt = con.prepareStatement(sql.toString());
    	    int paraCount = 1;
    	    if (newPassWord != null && !newPassWord.equals("")) {
    		stmt.setString(paraCount, newPassWord);
    		paraCount++;
    	    }
    	    if (name!= null && !name.equals("")) {
    		stmt.setString(paraCount, name);
    		paraCount++;
    	    }
    	    if (oldPassword!= null && !oldPassword.equals("")) {
        		stmt.setString(paraCount, oldPassword);
        		paraCount++;
        	 }
    	    int i = stmt.executeUpdate();
    	    if(i != 0){
    	    	success = true;
    	    } 
	  
    	} catch (Exception e) {
	    throw e;
	} finally {
	    try {		
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
    	return success;
    }
    
    public static List<String> getAllUsers() throws Exception {
	List<String> users = new ArrayList<String>();
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select name from operator_user where status = 1 order by name";
	    stmt = con.createStatement();	    
	    rs = stmt.executeQuery(sql);
	    
	    while (rs.next()) {
		users.add(rs.getString("name"));
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {	    
		    stmt.close();
		}
		if (con != null){
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return users;
    }
    
    public static List<User> getAllUsersByStatus() throws Exception {
		List<User> users = new ArrayList<User>();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToIncidentDB();
			String sql = "select * from operator_user where location is not null and location !='' and report = 1 order by location, name";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setName(rs.getString("name"));
				user.setLocation(rs.getString("location"));
				user.setReport(rs.getInt("report"));
				users.add(user);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return users;
	}
   
    public static UserSetting getUserSettingById(int user_id) throws Exception {
	UserSetting userSetting = new UserSetting();
	Connection con = null;
	PreparedStatement stmt = null;
	PreparedStatement stmtUpdate = null;
	ResultSet rs = null;
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "select markets from operator_user_setting where user_id=?";
	    stmt = con.prepareStatement(sql);
	    stmt.setInt(1, user_id);
	    rs = stmt.executeQuery();

	    if (rs.next()) {		
		userSetting.setMarkets(rs.getString("markets"));
	    } else {
		stmtUpdate = con.prepareStatement("insert into operator_user_setting values(?,'')");
		stmtUpdate.setInt(1, user_id);
		stmtUpdate.executeUpdate();
	    }
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		if (stmt != null) {
		    stmt.close();
		}
		if (stmtUpdate != null) {
		    stmtUpdate.close();
		}
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
	return userSetting;
    }
    
    public static void saveUserSetting(UserSetting userSetting, int user_id) throws Exception {
	Connection con = null;
	PreparedStatement stmt = null;	
	try {
	    con = DBConnector.getInstance().connectToIncidentDB();
	    String sql = "update operator_user_setting set markets=? where user_id=?";
	    stmt = con.prepareStatement(sql);
	    stmt.setString(1, userSetting.getMarkets());
	    stmt.setInt(2, user_id);
	    stmt.executeUpdate();
	} catch (Exception e) {
	    throw e;
	} finally {
	    try {		
		if (stmt != null) {
		    stmt.close();
		}		
		if (con != null) {
		    con.close();
		}
	    } catch (Exception e) {
		throw e;
	    }
	}
    }
    
    public static boolean addUser(User user) throws Exception {
		boolean success = false;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			if (user != null) {
				con = DBConnector.getInstance().connectToIncidentDB();
				String sql = "insert into operator_user (name, password, role, location) values (?,?,?,?)";
				stmt = con.prepareStatement(sql.toString());

				if (user.getName() != null && !user.getName().equals("")) {
					stmt.setString(1, user.getName());
				}
				if (user.getPassword() != null && !user.getPassword().equals("")) {
					stmt.setString(2, user.getPassword());
				}
				if (user.getRole() != null && !user.getRole().equals("")) {
					stmt.setString(3, user.getRole());
				}
				if (user.getLocation() != null && !user.getLocation().equals("")) {
					stmt.setString(4, user.getLocation());
				}

				int i = stmt.executeUpdate();
				if (i != 0) {
					success = true;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return success;
    }
    
    public static boolean updateUser(User user) throws Exception{
    	boolean success = false;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			if (user != null) {
				con = DBConnector.getInstance().connectToIncidentDB();
				String sql = "update operator_user set";
				if (user.getId() > 0) {
					if (user.getName() != null && !user.getName().equals("")){
						sql = sql + " name = " + user.getName() + ",";
					}
					if (user.getPassword() != null && !user.getPassword().equals("")){
						sql = sql + " password = " + user.getPassword() + ",";
					}
					if (user.getRole() != null && !user.getRole().equals("")){
						sql = sql + " role = " + user.getRole() + ",";
					}
					if (user.getLocation() != null && !user.getLocation().equals("")){
						sql = sql + " location = " + user.getLocation()  + ",";
					}
					sql = sql.replaceAll(",$", "");
					sql = sql + " where id = " + user.getId() + " and status = 1";			
					stmt = con.prepareStatement(sql.toString());
					int i = stmt.executeUpdate();
					if (i != 0) {
						success = true;
					}
				}			
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return success;
    }
    
    public static boolean deleteUser(int userId) throws Exception{
    	boolean success = false;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			if (userId > 0){
				con = DBConnector.getInstance().connectToIncidentDB();
				String sql = "update operator_user set status = 0 where id=" + userId + " and status = 1";
				stmt = con.prepareStatement(sql);
				int i = stmt.executeUpdate();
				if (i != 0){
					success = true;
				}
			}
		} catch(Exception e){
			throw e;
		} finally{
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return success;
    }
    
    public static List<User> getSpecificUser(String queryType, String queryData) throws Exception {
    	List<User> users = new ArrayList<User>();
    	Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (queryData != null && !"".equals(queryData.trim())){
				con = DBConnector.getInstance().connectToIncidentDB();
				String sql = "select * from operator_user where status = 1";				
				if (queryType != null) {
					if (queryType.trim().equals(BY_NAME)) {
						sql = sql + " and name like ?";
						stmt = con.prepareStatement(sql);
						stmt.setString(1, "%" + queryData.trim() + "%");
						rs = stmt.executeQuery();
						User user = null;
						while (rs.next()) {
							user = new User();
							user.setId(rs.getInt("id"));
							user.setName(rs.getString("name"));
							user.setPassword(rs.getString("password"));
							user.setRole(rs.getString("role"));
							user.setLocation(rs.getString("location"));
							users.add(user);
						}
					}					
				}				
			}
		} catch(Exception e){
			throw e;
		} finally{
			try {
				if (rs != null){
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
    	return users;
    }
    
    public static List<User> getAllUserInfo() throws Exception{
    	List<User> users = new ArrayList<User>();
    	Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	try {
    	    con = DBConnector.getInstance().connectToIncidentDB();
    	    String sql = "select * from operator_user where status = 1 order by name";
    	    stmt = con.createStatement();	    
    	    rs = stmt.executeQuery(sql);
    	    User user = null;
    	    while (rs.next()) {
    	    	user = new User();
				user.setId(rs.getInt("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
				user.setRole(rs.getString("role"));
				user.setLocation(rs.getString("location"));
				users.add(user);
    	    }  
    	} catch (Exception e) {
    	    throw e;
    	} finally {
    	    try {
    		if (rs != null) {
    		    rs.close();
    		}
    		if (stmt != null) {	    
    		    stmt.close();
    		}
    		if (con != null){
    		    con.close();
    		}
    	    } catch (Exception e) {
    		throw e;
    	    }
    	}
    	return users;
    }
    
}
