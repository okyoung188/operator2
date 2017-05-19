package com.trafficcast.operator.traverse;

public class PriorityLink implements Comparable<PriorityLink>{
	public Link startLink;
	public Link endLink;
	public double distance;
	public double distance_from_link;
	public int type;
	
	public int compareTo(PriorityLink plink){
		if (this.type > plink.type){
			return 1;
		}
		if (this.type < plink.type){
			return -1;
		}
		if (this.type == 3 && plink.type == 3 && this.endLink.speedCat > this.endLink.speedCat){
			return -1;
		}
		if (this.type == 3 && plink.type == 3 && this.endLink.speedCat < this.endLink.speedCat){
			return 1;
		}
		if (this.type == plink.type && this.distance_from_link > plink.distance_from_link){
			return 1;
		}
		if (this.type == plink.type && this.distance_from_link < plink.distance_from_link){
			return -1;
		}
		if (this.type == plink.type && this.distance_from_link == plink.distance_from_link && this.distance > plink.distance){
			return 1;
		}
		if (this.type == plink.type && this.distance_from_link == plink.distance_from_link && this.distance < plink.distance){
			return -1;
		}
		if (this.type == plink.type && this.distance_from_link == plink.distance_from_link && this.distance == plink.distance){
			return 0;
		}
		return 0;
	}
}
