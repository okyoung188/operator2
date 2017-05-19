package com.trafficcast.operator.traverse;

/**
 * Represents a link
 * @author Yi Zhang
 */
public class Link extends BaseLink{
	public int FNode;
	public int TNode;
	public String FName_Base;
	public String dir_onsign;
	public double[] link_geom;
	public String tmccode;
	
	public boolean equals(Link lk){
		if (this.link_id == lk.link_id && this.link_dir.equals(lk.link_dir)){
			return true;
		}
		return false;
	}
	
	public int hashCode(){
		return (this.link_id + this.link_dir).hashCode();
	}
	
}
