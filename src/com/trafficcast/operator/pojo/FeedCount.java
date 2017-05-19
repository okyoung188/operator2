package com.trafficcast.operator.pojo;

public class FeedCount {
	
	private String market;
	
	private int rank;
			
	private int roadCloure_onstar;

	private int roadCloure_sxm;
	
	private int accident_onstar;
	
	private int accident_sxm;
	
	private int construction_onstar;
	
	private int construction_sxm;
	
	private String time;
	
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRoadCloure_onstar() {
		return roadCloure_onstar;
	}

	public void setRoadCloure_onstar(int roadCloure_onstar) {
		this.roadCloure_onstar = roadCloure_onstar;
	}

	public int getRoadCloure_sxm() {
		return roadCloure_sxm;
	}

	public void setRoadCloure_sxm(int roadCloure_sxm) {
		this.roadCloure_sxm = roadCloure_sxm;
	}

	public int getAccident_onstar() {
		return accident_onstar;
	}

	public void setAccident_onstar(int accident_onstar) {
		this.accident_onstar = accident_onstar;
	}

	public int getAccident_sxm() {
		return accident_sxm;
	}

	public void setAccident_sxm(int accident_sxm) {
		this.accident_sxm = accident_sxm;
	}

	public int getConstruction_onstar() {
		return construction_onstar;
	}

	public void setConstruction_onstar(int construction_onstar) {
		this.construction_onstar = construction_onstar;
	}

	public int getConstruction_sxm() {
		return construction_sxm;
	}

	public void setConstruction_sxm(int construction_sxm) {
		this.construction_sxm = construction_sxm;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
