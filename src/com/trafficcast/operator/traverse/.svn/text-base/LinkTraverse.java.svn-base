package com.trafficcast.operator.traverse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.trafficcast.operator.utils.Utils;

public class LinkTraverse {
	
	public static double[] getArrayFromLinestring(String lineStr)
	{
		lineStr = lineStr.replaceAll("LINESTRING\\(","");
		lineStr = lineStr.replaceAll("\\)","");
		lineStr = lineStr.replaceAll(" ",",");

		String[] tokens = lineStr.split(",");

		double lonlat[]= new double[tokens.length];

		for (int j=0;j<tokens.length;j++){
			lonlat[j] = new Double(tokens[j]).doubleValue();
		}
		return lonlat;
	}
	
	
	public static Link getLink(Connection connection, int link_id, String link_dir, String country) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		Link link = null;
		try{
			String query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,hwydir,astext(link_geom) as geom FROM " + 
				Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE link_id = " + link_id + " AND linkdir = '" + link_dir + "'";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()){
				link = new Link();
				link.link_id = rs.getInt("link_id");		
				link.link_dir = rs.getString("linkdir");
				link.length = rs.getDouble("distance");
				link.FNode = rs.getInt("f_node_id");
				link.TNode = rs.getInt("t_node_id");
				link.FName_Base = rs.getString("fname_base");
				link.dir_onsign = rs.getString("hwydir");
				link.link_geom = getArrayFromLinestring(rs.getString("geom"));
				if (link.FName_Base == null)
					link.FName_Base = "";
				link.speedCat = rs.getInt("speedcat");
			}
			return link;
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		finally{
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
		}
		
	}
	
	/****************
	 * Traverse forward to get next links
	 * @param gLink
	 * @return ArrayList<Link>
	 * @throws Exception
	 */
	public static ArrayList<Link> getNextLinks(Connection connection, Link gLink, String country) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = connection.createStatement();
			ArrayList<Link> linkList = new ArrayList<Link>();
			String query = "";
			if (gLink.link_dir.equals("F")){
				query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE f_node_id = '" + gLink.TNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'F'" + 
				" union " + "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE t_node_id = '" + gLink.TNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'T'";
			}
			else if (gLink.link_dir.equals("T")){
				query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE f_node_id = '" + gLink.FNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'F'" + 
				" union " + "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE t_node_id = '" + gLink.FNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'T'";
			}
			//System.out.println(query);
			rs = stmt.executeQuery(query);
			while(rs.next()){
				Link link = new Link();
				link.link_id = rs.getInt("link_id");		
				link.link_dir = rs.getString("linkdir");
				link.length = rs.getDouble("distance");
				link.FNode = rs.getInt("f_node_id");
				link.TNode = rs.getInt("t_node_id");
				link.FName_Base = rs.getString("fname_base");
				link.link_geom = getArrayFromLinestring(rs.getString("geom"));
				link.speedCat = rs.getInt("speedcat");
				if (link.FName_Base == null)
					link.FName_Base = "";
				linkList.add(link);
			}
			rs.close();
			
			// Get alias			
			if (gLink.link_dir.equals("F")){
				query = "select a.*, b.fname_base from (SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE f_node_id = '" + gLink.TNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'F'" + 
				" union " + "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE t_node_id = '" + gLink.TNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'T') a inner join " + Utils.getNTMapVersion() + "_alias_" + country + " b on a.link_id=b.link_id";
			}
			else if (gLink.link_dir.equals("T")){
				query = "select a.*, b.fname_base from (SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE f_node_id = '" + gLink.FNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'F'" + 
				" union " + "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE t_node_id = '" + gLink.FNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'T') a inner join " + Utils.getNTMapVersion() + "_alias_" + country + " b on a.link_id=b.link_id";
			}
			//System.out.println(query);
			rs = stmt.executeQuery(query);
			while(rs.next()){
				Link link = new Link();
				link.link_id = rs.getInt("link_id");
				link.link_dir = rs.getString("linkdir");
				link.length = rs.getDouble("distance");
				link.FNode = rs.getInt("f_node_id");
				link.TNode = rs.getInt("t_node_id");
				link.FName_Base = rs.getString("fname_base");
				link.link_geom = getArrayFromLinestring(rs.getString("geom"));
				link.speedCat = rs.getInt("speedcat");
				if (link.FName_Base == null)
					link.FName_Base = "";
				linkList.add(link);
			}

			rs.close();
			
			stmt.close();
			if (linkList.size() == 0)
				return null;
			return linkList;
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		finally{
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}	

	/****************
	 * Traverse backward to get previous links
	 * @param gLink
	 * @return ArrayList<Link>
	 * @throws Exception
	 */
	public static ArrayList<Link> getPreviousLinks(Connection connection,Link gLink, String country) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = connection.createStatement();
			ArrayList<Link> linkList = new ArrayList<Link>();
			String query = "";
			////System.out.println(gLink.link_id + "," + gLink.link_dir);

			if (gLink.link_dir.equals("F")){
				query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE t_node_id = '" + gLink.FNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'F'" + 
				" union " + "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE f_node_id = '" + gLink.FNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'T'";
			}
			else if (gLink.link_dir.equals("T")){
				query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE t_node_id = '" + gLink.TNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'F'" +
				" union " + "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
				" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE f_node_id = '" + gLink.TNode + "'" +
				" and link_id <> " + gLink.link_id + " and linkdir = 'T' ";
			}
			//System.out.println(query);
			rs = stmt.executeQuery(query);
			while(rs.next()){
				Link link = new Link();
				link.link_id = rs.getInt("link_id");			
				link.link_dir = rs.getString("linkdir");
				link.length = rs.getDouble("distance");
				link.FNode = rs.getInt("f_node_id");
				link.TNode = rs.getInt("t_node_id");
				link.FName_Base = rs.getString("fname_base");
				link.link_geom = getArrayFromLinestring(rs.getString("geom"));
				link.speedCat = rs.getInt("speedcat");
				if (link.FName_Base == null)
					link.FName_Base = "";
				linkList.add(link);
			}
			rs.close();
			stmt.close();
			if (linkList.size() == 0)
				return null;
			return linkList;
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

}
