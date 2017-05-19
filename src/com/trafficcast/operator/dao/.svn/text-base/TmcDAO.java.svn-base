package com.trafficcast.operator.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.trafficcast.operator.utils.Utils;

public class TmcDAO {
	private static Logger logger = Logger.getLogger(TmcDAO.class);

	public static String getTMCByLinkIDAndDir(int linkID, String linkDir) throws Exception {
		String tmc = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToNTLinksDB();
			String table = Utils.getNTMapVersion() + "_linkid_tmc_speedcat_northamerica_df3_dissm_merged";
			String sql = "SELECT TMC_UNSIGNED FROM startdb."
					+ table + " where link_id = ? and link_dir = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, linkID);
			stmt.setString(2, linkDir);
			rs = stmt.executeQuery();

			while (rs.next()) {
				tmc += rs.getString("TMC_UNSIGNED") + ",";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				con.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return tmc;
	}
	
	public static String getPathIDByTMC(String tmc) throws Exception {
		String pathid = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnector.getInstance().connectToNTLinksDB();
			String table = Utils.getNTMapVersion() + "_nt_tmc_loc_table_" + tmc.substring(0,3).toLowerCase();
			String sql = "SELECT path_id FROM startdb."
					+ table + " where loc_id = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, tmc.substring(4));
			rs = stmt.executeQuery();

			if (rs.next()) {
				pathid = rs.getString("path_id");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				con.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return pathid;
	}
}
