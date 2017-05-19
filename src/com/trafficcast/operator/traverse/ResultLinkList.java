package com.trafficcast.operator.traverse;

import java.util.ArrayList;

/**
 * Represents a result of the returned ramp link list, which has one more field type
 * Type is defined as integer from 1 to 3, which represents the ramp link list's confidence level.
 * 		Type 1: Match the cross_from name and from_dir, most reliable
 * 		Type 2: Match the cross_from name
 * 		Type 3: A diverging link (which splits into two), least reliable
 * @author Yi Zhang
 */
public class ResultLinkList {
	public ArrayList<Link> resultLinks;
	public int type;
	public Link connectorLink;
	
	public void reverse(){
		int length = resultLinks.size();
		for (int i = 0; i < length/2; i++){
			Link tmpLink = resultLinks.get(i);
			resultLinks.set(i, resultLinks.get(length - i - 1));
			resultLinks.set(length - i - 1,tmpLink);
		}
	}
}
