package com.trafficcast.operator.traverse;

/**
 * Represents a result link, which has one more field distance
 * The distance means the distance between this link and the given start link
 * Override the compareTo method for Collections.sort()
 * @author Yi Zhang
 */
public class ResultLink implements Comparable<ResultLink>{
	public Link link;
	public double distance;
	
	public int compareTo(ResultLink rlink){
		if (this.distance > rlink.distance){
			return 1;
		}
		if (this.distance < rlink.distance){
			return -1;
		}
		if (this.distance == rlink.distance){
			return 0;
		}
		return 0;
	}
	
	/*
	 * Override the equals method
	 */
	public boolean equals(Object o) {
		ResultLink rlink = (ResultLink) o;
		if (link.link_id == rlink.link.link_id && link.link_dir.equals(rlink.link.link_dir))
			return true;
		return false;
	}
	
}
