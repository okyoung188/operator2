package com.trafficcast.operator.traverse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trafficcast.operator.utils.Utils;

/*******************************************************************************
 * <p>
 *	This class is the main class of RampIncidentTraverse, the major tasks are match the ramp incidents
 *  correctly to the ramp links.
 *  We adopted the following algorithm:
 *  1) find all the connectors(Type A) both backward and forward in 0.5 miles
 *  2) Start from the connectors to traverse the ramp to find the next connectors in 1.5 miles(Type B)
 *  3) For each connectors (Type B), compare the road name with the cross_from and the direction with
 *     the from_dir. Then connect from Type A connector to Type B connector.
 *  4) Output the most likely one.
 *  Type A: Mainline to Ramp connector
 *  Type B: Ramp tp Mainline connector
 * </p>  
 * @author Yi Zhang    (03/12/2011)
 *******************************************************************************/
public class RampIncidentTraverse {
	/** DB connection to the first server. **/
	private Connection connection1 = null;
	private static final double PI180= 0.017453; /** pi/180*/
	private static final double PI180R = 57.2958; /** 180/pi*/
	private static final double PI=3.1415926;
	private static final int[] ONRAMP_ITIS_CODES = {406,471,472,473,632};
	private static final int[] OFRAMP_ITIS_CODES = {407,474,475,476,633,478,409};
	private static final int[] OFFONR_ITIS_CODES = {408,477};
	private Set<Integer> OnRampSet = null;
	private Set<Integer> OfRampSet = null;
	private Set<Integer> OnOffRSet = null;
	private static final double MAX_LENGTH = 1;
	private static final double MAX_RAMP_LENGTH = 1;

	/*******
	 * Contructor
	 * Pass the Connection to this class to do querying
	 * @param connection
	 */
	public RampIncidentTraverse(Connection connection){
		//System.out.println("SVN repos 1086 - fix main-line ends at all ramp links bug - 07/10/2011");
		this.connection1 = connection;
		OnRampSet = new HashSet<Integer>();
		OfRampSet = new HashSet<Integer>();
		OnOffRSet = new HashSet<Integer>();
		for (int i = 0; i < ONRAMP_ITIS_CODES.length; i++){
			OnRampSet.add(ONRAMP_ITIS_CODES[i]);
		}
		for (int i = 0; i < OFRAMP_ITIS_CODES.length; i++){
			OfRampSet.add(OFRAMP_ITIS_CODES[i]);
		}
		for (int i = 0; i < OFFONR_ITIS_CODES.length; i++){
			OnOffRSet.add(OFFONR_ITIS_CODES[i]);
		}
	}

	/*********************
	 * Get the ramp link list for a given incident location (Link), its itis code, 
	 * the cross_from street name, and the high way bound (from_dir).
	 * @param link
	 * @param itisCode
	 * @param cross_from
	 * @param fromDir
	 * @return ArrayList<ResultLinkList> return an array of ResultLinkList. 
	 *		If code is 408 or 477, the 1st item is on-ramp ResultLinkList, the 2nd is off-ramp ResultLinkList
	 * @throws Exception
	 */
	public ArrayList<ResultLinkList> rampIncidentTraverse(Link link, int itisCode, String cross_from,String cross_to, String fromDir, double lat,double lon, String country) throws Exception{
		ResultLinkList resultLinkList = null;
		ArrayList<ResultLinkList> result = new ArrayList<ResultLinkList>();
		if (cross_from == null){
			cross_from = cross_to;
		}
		if (cross_from.equals("")){
			cross_from = cross_to;
		}
		if (cross_from == null){
			cross_from = "";
		}
		if (OnRampSet.contains(itisCode)){
			resultLinkList = onRampTraverse(link,cross_from,fromDir,lat,lon, country);
			result.add(resultLinkList);
		}
		else if (OfRampSet.contains(itisCode)){
			resultLinkList = offRampTraverse(link,cross_from,fromDir,lat,lon, country);
			resultLinkList.reverse();
			result.add(resultLinkList);
		}
		else if (OnOffRSet.contains(itisCode)){
			ResultLinkList onRampLinkList = onRampTraverse(link,cross_from,fromDir,lat,lon, country);
			ResultLinkList offRampLinkList = offRampTraverse(link,cross_from,fromDir,lat,lon, country);
			offRampLinkList.reverse();
			result.add(onRampLinkList);
			result.add(offRampLinkList);		
		}
		return result;
	}

	private double calDistanceInPoints(double slat,double slon,double elat,double elon){
		double dx = Math.abs(slon - elon);
		double dy = Math.abs(slat - elat);
		double lat = (slat + elat) / 2; 
		double DY = dy * 69.;
		double DX = dx * (69.*Math.cos(lat*PI180));
		return  Math.sqrt(DY*DY + DX*DX);
	}

	private static Pattern pa = Pattern.compile("^\\d+\\D?$", Pattern.DOTALL);
	private boolean checkIfRamp(String fbase_name){
		if (fbase_name == null || fbase_name.equals("")){
			return true;
		}
		Matcher ma = pa.matcher(fbase_name);
		if (ma.find()){
			return true;
		}
		return false;
	}

	/*********************
	 * Get the ramp link list for an on-ramp incident, cross_from and highway bound.
	 * @param link
	 * @param cross_from
	 * @param fromDir
	 * @return ArrayList<String> the return will be as the format: link_id-link_dir
	 * @throws Exception
	 */
	private ResultLinkList onRampTraverse(Link link,String cross_from,String fromDir, double lat, double lon, String country) throws Exception{
		//System.out.println("start onRampTraverse");
		ResultLinkList resultLinkList = new ResultLinkList();
		resultLinkList.resultLinks = new ArrayList<Link>();
		ArrayList<Link> nextLinks = null;
		Link nextLink = link;
		double preDistance = 0;
		double posDistance = 0;
		if (link.link_dir.equals("F")){
			preDistance = calDistanceInPoints(lat,lon,link.link_geom[1],link.link_geom[0]);
			posDistance = calDistanceInPoints(lat,lon,link.link_geom[link.link_geom.length - 1],link.link_geom[link.link_geom.length - 2]);
		}
		else{
			preDistance = calDistanceInPoints(lat,lon,link.link_geom[link.link_geom.length - 1],link.link_geom[link.link_geom.length - 2]);
			posDistance = calDistanceInPoints(lat,lon,link.link_geom[1],link.link_geom[0]);
		}
		ArrayList<ResultLink> connectors = new ArrayList<ResultLink>();
		ArrayList<Link> Links = new ArrayList<Link>();
		ArrayList<Link> connector = null;
		Links.add(nextLink);
		//System.out.println("start to look forward");
		connector = findOnRamp(Links,link.FName_Base, country);
		if (connector.size() != 0){
			for (Link cn : connector){
				//System.out.println("Yes we found connector: " + cn.link_id + "," + cn.link_dir );
				ResultLink rConnector = new ResultLink();
				rConnector.link = cn;
				rConnector.distance = posDistance;
				if (!connectors.contains(rConnector)){
					connectors.add(rConnector); 
				}
			}
		}
		while (true){
			nextLinks = getNextLinks(nextLink, false, country);
			if (nextLinks == null){
				break;
			}
			connector = findOnRamp(nextLinks,link.FName_Base, country);
			if (connector.size() != 0){
				for (Link cn : connector){
					//System.out.println("Yes we found connector: " + cn.link_id + "," + cn.link_dir );
					ResultLink rConnector = new ResultLink();
					rConnector.link = cn;
					rConnector.distance = posDistance;
					if (!connectors.contains(rConnector)){
						connectors.add(rConnector); 
					}
				}
			}
			if (posDistance > MAX_LENGTH){
				break;
			}
			boolean findNext = false;
			posDistance += nextLink.length;
			for (Link lk : nextLinks){
				if (lk.FName_Base.equals(link.FName_Base) || !checkIfRamp(lk.FName_Base)){
					nextLink = lk;
					findNext = true;
					break;
				}
			}
			if (findNext == false){
				break;
			}
		}
		// if go back cannot find the connector
		// traverse back
		//System.out.println("start to look backward");
		nextLink = link;
		connector = null;
		while (true){
			nextLinks = getPreviousLinks(nextLink, false, country);
			if (nextLinks == null){
				break;
			}
			connector = findOnRamp(nextLinks,link.FName_Base, country);
			if (connector.size() != 0){
				for (Link cn : connector){
					//System.out.println("Yes we found connector: " + cn.link_id + "," + cn.link_dir );
					ResultLink rConnector = new ResultLink();
					rConnector.link = cn;
					rConnector.distance = preDistance;
					if (!connectors.contains(rConnector)){
						connectors.add(rConnector); 
					}
				}
			}
			if (preDistance > MAX_LENGTH){
				break;
			}
			preDistance += nextLink.length;
			boolean findNext = false;
			for (Link lk : nextLinks){
				if (lk.FName_Base.equals(link.FName_Base) || !checkIfRamp(lk.FName_Base)){
					nextLink = lk;
					//System.out.println("next link is " + nextLink.link_id);
					findNext = true;
					break;
				}
			}
			if (findNext == false){
				break;
			}
		}

		if (connectors.size() == 0){
			return resultLinkList;
		}
		Collections.sort(connectors);
		ArrayList<PriorityLink> pLinkList = new ArrayList<PriorityLink>();
		Hashtable<Link,Link> preLinkTable = new Hashtable<Link,Link>();
		for (ResultLink lk : connectors){
			//System.out.println("start from " + lk.link.link_id + "," + lk.link.link_dir + "," + lk.link.FName_Base);
			ArrayList<ResultLink> linkList = onRampRouting(preLinkTable,lk.link,cross_from, MAX_RAMP_LENGTH, country);
			if (linkList == null || linkList.isEmpty())
				continue;
			
			boolean p0 = false;
			boolean p1 = false;
			boolean p2 = false;
			Collections.sort(linkList);
			
			if (lk.link.FName_Base.equals(cross_from.replace("Exit ", ""))){
				ResultLink rlk = linkList.get(0);
				//System.out.println("The dest P0 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
				PriorityLink pLink = new PriorityLink();
				pLink.endLink = rlk.link;
				pLink.startLink = lk.link;
				pLink.type = 0;
				pLink.distance = rlk.distance;
				pLink.distance_from_link = lk.distance;
				pLinkList.add(pLink);
				p0 = true;
			}
			
			if(p0 == false){
				for (ResultLink rlk : linkList){
					if (rlk.link.FName_Base.equals(cross_from) &&
							(linkList.size() > 1 || checkLinkBound(rlk.link.link_geom,rlk.link.link_dir,fromDir,rlk.link.dir_onsign))){
						//System.out.println("The dest P1 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
						PriorityLink pLink = new PriorityLink();
						pLink.endLink = rlk.link;
						pLink.startLink = lk.link;
						pLink.type = 1;
						pLink.distance = rlk.distance;
						pLink.distance_from_link = lk.distance;
						pLinkList.add(pLink);
						p1 = true;
						break;
					}
				}
			}

			if (p1 == false && p0 == false){
				for (ResultLink rlk : linkList){
					if (rlk.link.FName_Base.equals(cross_from) && !rlk.link.FName_Base.equals(link.FName_Base)){
						//System.out.println("The dest P2 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
						PriorityLink pLink = new PriorityLink();
						pLink.endLink = rlk.link;
						pLink.startLink = lk.link;
						pLink.type = 2;
						pLink.distance = rlk.distance;
						pLink.distance_from_link = lk.distance;
						pLinkList.add(pLink);
						p2= true;
						break;
					}
				}
			}
			if (p2 == false && p1 == false && p0 == false){
				for (ResultLink rlk : linkList){
					if (!rlk.link.FName_Base.equals(link.FName_Base)){
						//System.out.println("The dest P3 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
						PriorityLink pLink = new PriorityLink();
						pLink.endLink = rlk.link;
						pLink.startLink = lk.link;
						pLink.type = 3;
						pLink.distance = rlk.distance;
						pLink.distance_from_link = lk.distance;
						pLinkList.add(pLink);
						break;
					}
				}
			}
		}
		
		if (pLinkList.size() == 0){
			return resultLinkList;
		}
		PriorityLink pLink = Collections.min(pLinkList);
		Link destLink = pLink.endLink;
		Link origLink = pLink.startLink;
		resultLinkList.type = pLink.type;
		Link curLink = destLink;
		Link orgLink = origLink;
		//System.out.println("start to do the traverse back from " + curLink + "," + pLink.type);
		//System.out.println("start to do the traverse end at " + orgLink);
		while(preLinkTable.get(curLink) != null && 
				!preLinkTable.get(curLink).equals(orgLink)){
			//System.out.println("pre link is " + preLinkTable.get(curLink));
			curLink = preLinkTable.get(curLink);
			if (resultLinkList.resultLinks.contains(curLink)){
				break;
			}
			if (curLink.FName_Base == null){
				resultLinkList.resultLinks.add(curLink);
			}
			else if (curLink.FName_Base.equals("")){
				resultLinkList.resultLinks.add(curLink);
			}
			else if (checkIfRamp(curLink.FName_Base)){
				resultLinkList.resultLinks.add(curLink);
			}		
		}
		resultLinkList.connectorLink = destLink;
		resultLinkList.resultLinks.add(orgLink);
		return resultLinkList;
	}

	/*********************
	 * Get a list of ramp links from a given start link of the ramp, providing cross_from, max distance to traverse.
	 * The returning results are the connection point (from ramp to main road)
	 * @param Hashtable<String,String> preLinkTable - a reference of Hashtable to record the adjacense relationships 
	 * @param Link - a given start link of on-ramp
	 * @param String - cross_from
	 * @param double - max distance allowed for traversing
	 * @return ArrayList<ResultLink>
	 * @throws Exception
	 */
	private ArrayList<ResultLink> onRampRouting(Hashtable<Link, Link> preLinkTable, Link endLink, String cross_from, double maxDistance, String country) throws Exception {
		ArrayList<Link> linkQueue = new ArrayList<Link>();
		ArrayList<ResultLink> resultLinks = new ArrayList<ResultLink>();
		Hashtable<String,Double> distanceTable = new Hashtable<String,Double>();
		distanceTable.put(endLink.link_id + " to " + endLink.link_id,0.0);
		HashSet<String> visitTable = new HashSet<String>();
		linkQueue.add(endLink);
		visitTable.add(endLink.link_id + "-" + endLink.link_dir);
		//System.out.println("start onramp routing");
		while(linkQueue.size() != 0){
			Link newLink = linkQueue.remove(0);
			double distance = distanceTable.get(endLink.link_id + " to " + newLink.link_id);
			//System.out.println("the distance is " + distance);
			if (distance > maxDistance){
				continue;
			}
			ArrayList<Link> previousLinks = getPreviousLinks(newLink, true, country);
			if (previousLinks == null){
				continue;
			}
			if (previousLinks.size() == 1){
				Link lk = previousLinks.get(0);
				if (!visitTable.contains(lk.link_id + "-" + lk.link_dir)){
					preLinkTable.put(lk,newLink);
					distanceTable.put(endLink.link_id + " to " + lk.link_id, distance + newLink.length);
					visitTable.add(lk.link_id + "-" + lk.link_dir);
					String lk_fname = lk.FName_Base;
					if (!lk_fname.equals(endLink.FName_Base) && !checkIfRamp(lk_fname)){
						ResultLink rlink = new ResultLink();
						rlink.link = lk;
						rlink.distance = distance + newLink.length;
						resultLinks.add(rlink);
					}
					else{
						linkQueue.add(lk);
					}
				}
			}
			else if (previousLinks.size() > 1){
				for (Link lk : previousLinks){
					if (!visitTable.contains(lk.link_id + "-" + lk.link_dir)){
						preLinkTable.put(lk,newLink);
						distanceTable.put(endLink.link_id + " to " + lk.link_id, distance + newLink.length);
						visitTable.add(lk.link_id + "-" + lk.link_dir);
						//						if (!lk.FName_Base.equals(preLink.FName_Base)){
						ResultLink rlink = new ResultLink();
						rlink.link = lk;
						rlink.distance = distance + newLink.length;
						resultLinks.add(rlink);
						//						}
						//						else{
						//							linkQueue.add(lk);
						//						}
					}
				}
			}
		}
		return resultLinks;
	}

	/*********************
	 * Get a list of ramp links from a given start link of the ramp, providing cross_from, max distance to traverse.
	 * The returning results are the connection point (from ramp to main road)
	 * @param Hashtable<String,String> preLinkTable - a reference of Hashtable to record the adjacense relationships 
	 * @param Link - a given start link of off-ramp
	 * @param String - cross_from
	 * @param double - max distance allowed for traversing
	 * @return ArrayList<ResultLink>
	 * @throws Exception
	 */
	private ArrayList<ResultLink> offRampRouting(Hashtable<Link,Link> nextLinkTable,Link startLink,String cross_from,double maxDistance, String country) throws Exception{
		ArrayList<Link> linkQueue = new ArrayList<Link>();
		ArrayList<ResultLink> resultLinks = new ArrayList<ResultLink>();
		Hashtable<String,Double> distanceTable = new Hashtable<String,Double>();
		distanceTable.put(startLink.link_id + " to " + startLink.link_id,0.0);
		HashSet<String> visitTable = new HashSet<String>();
		linkQueue.add(startLink);
		visitTable.add(startLink.link_id + "-" + startLink.link_dir);
		//System.out.println("start offramp routing");
		while(linkQueue.size() != 0){
			Link newLink = linkQueue.remove(0);
			double distance = distanceTable.get(startLink.link_id + " to " + newLink.link_id);
			if (distance > maxDistance){
				continue;
			}
			ArrayList<Link> nextLinks = getNextLinks(newLink, true, country);
			if (nextLinks == null){
				continue;
			}
			if (nextLinks.size() == 1){
				Link lk = nextLinks.get(0);
				if (!visitTable.contains(lk.link_id + "-" + lk.link_dir)){
					linkQueue.add(nextLinks.get(0));
					nextLinkTable.put(lk,newLink);
					distanceTable.put(startLink.link_id + " to " + lk.link_id, distance + newLink.length);
					visitTable.add(lk.link_id + "-" + lk.link_dir);
					String lk_fname = lk.FName_Base;
					if (!lk_fname.equals(startLink.FName_Base) && !checkIfRamp(lk_fname)){
						ResultLink rlink = new ResultLink();
						rlink.link = lk;
						rlink.distance = distance + newLink.length;
						resultLinks.add(rlink);
					}
					else{
						linkQueue.add(lk);
					}
				}
			}
			else if (nextLinks.size() > 1){			
				for (Link lk : nextLinks){
					if (!visitTable.contains(lk.link_id + "-" + lk.link_dir)){
						nextLinkTable.put(lk,newLink);
						distanceTable.put(startLink.link_id + " to " + lk.link_id, distance + newLink.length);
						visitTable.add(lk.link_id + "-" + lk.link_dir);
						//						if (!lk.FName_Base.equals(nextLink.FName_Base)){
						ResultLink rlink = new ResultLink();
						rlink.link = lk;
						rlink.distance = distance + newLink.length;
						resultLinks.add(rlink);
						//						}
						//						else{
						//							linkQueue.add(lk);
						//						}
					}
				}
			}
		}
		return resultLinks;
	}

	/*********************
	 * Get the ramp link list for an off-ramp incident, cross_from and highway bound.
	 * @param link
	 * @param cross_from
	 * @param fromDir
	 * @return ArrayList<String> the return will be as the format: link_id-link_dir
	 * @throws Exception
	 */
	private ResultLinkList offRampTraverse(Link link,String cross_from,String fromDir,double lat,double lon, String country) throws Exception{
		//System.out.println("start offRampTraverse");
		ResultLinkList resultLinkList = new ResultLinkList();
		resultLinkList.resultLinks = new ArrayList<Link>();
		ArrayList<Link> prevLinks = null;
		Link prevLink = link;
		double preDistance = 0;
		double posDistance = 0;
		if (link.link_dir.equals("F")){
			preDistance = calDistanceInPoints(lat,lon,link.link_geom[1],link.link_geom[0]);
			posDistance = calDistanceInPoints(lat,lon,link.link_geom[link.link_geom.length - 1],link.link_geom[link.link_geom.length - 2]);
		}
		else{
			preDistance = calDistanceInPoints(lat,lon,link.link_geom[link.link_geom.length - 1],link.link_geom[link.link_geom.length - 2]);
			posDistance = calDistanceInPoints(lat,lon,link.link_geom[1],link.link_geom[0]);
		}
		ArrayList<ResultLink> connectors = new ArrayList<ResultLink>();
		ArrayList<Link> Links = new ArrayList<Link>();
		ArrayList<Link> connector = null;
		Links.add(prevLink);
		//System.out.println("start to look backward");
		connector = findOffRamp(Links,link.FName_Base, country);
		if (connector.size() != 0){
			for (Link cn : connector){
				//System.out.println("Yes we found connector: " + cn.link_id + "," + cn.link_dir );
				ResultLink rConnector = new ResultLink();
				rConnector.link = cn;
				rConnector.distance = preDistance;
				if (!connectors.contains(rConnector)){
					connectors.add(rConnector); 
				}
			}
		}
		// go back first
		while (true){
			prevLinks = getPreviousLinks(prevLink, false, country);
			if (prevLinks == null){
				break;
			}
			connector = findOffRamp(prevLinks,link.FName_Base, country);
			if (connector.size() != 0){
				for (Link cn : connector){
					//System.out.println("Yes we found connector: " + cn.link_id + "," + cn.link_dir );
					ResultLink rConnector = new ResultLink();
					rConnector.link = cn;
					rConnector.distance = preDistance;
					if (!connectors.contains(rConnector)){
						connectors.add(rConnector); 
					}
				}
			}
			if (preDistance > MAX_LENGTH){
				break;
			}
			preDistance += prevLink.length;
			boolean findPrev = false;
			for (Link lk : prevLinks){
				if (lk.FName_Base.equals(link.FName_Base) || !checkIfRamp(lk.FName_Base)){
					prevLink = lk;
					findPrev = true;
					break;
				}
			}
			if (findPrev == false){
				break;
			}
		}
		//System.out.println("start to look forward");
		// if go back cannot find the connector
		// traverse forward
		prevLink = link;
		connector = null;
		while (true){
			prevLinks = getNextLinks(prevLink, false, country);
			if (prevLinks == null){
				break;
			}
			connector = findOffRamp(prevLinks,link.FName_Base, country);
			if (connector.size() != 0){
				for (Link cn : connector){
					//System.out.println("Yes we found connector: " + cn.link_id + "," + cn.link_dir );
					ResultLink rConnector = new ResultLink();
					rConnector.link = cn;
					rConnector.distance = posDistance;
					if (!connectors.contains(rConnector)){
						connectors.add(rConnector); 
					}
				}
			}
			if (posDistance > MAX_LENGTH){
				break;
			}
			posDistance += prevLink.length;
			boolean findNext = false;
			for (Link lk : prevLinks){
				if (lk.FName_Base.equals(link.FName_Base) || !checkIfRamp(lk.FName_Base)){
					prevLink = lk;
					findNext = true;
					break;
				}
			}
			if (findNext == false){
				break;
			}
		}

		if (connectors.size() == 0){
			return resultLinkList;
		}
		Collections.sort(connectors);
		ArrayList<PriorityLink> pLinkList = new ArrayList<PriorityLink>();
		Hashtable<Link,Link> nextLinkTable = new Hashtable<Link,Link>();
		for (ResultLink lk : connectors){
			//System.out.println("start from " + lk.link.link_id + "," + lk.link.link_dir + "," + lk.link.FName_Base);
			ArrayList<ResultLink> linkList = offRampRouting(nextLinkTable,lk.link,cross_from,MAX_RAMP_LENGTH, country);
			if (linkList == null || linkList.isEmpty())
				continue;
			
			boolean p0 = false;
			boolean p1 = false;
			boolean p2 = false;
			Collections.sort(linkList);
			
			if (lk.link.FName_Base.equals(cross_from.replace("Exit ", ""))){
				ResultLink rlk = linkList.get(0);
				//System.out.println("The dest P0 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
				PriorityLink pLink = new PriorityLink();
				pLink.endLink = rlk.link;
				pLink.startLink = lk.link;
				pLink.type = 0;
				pLink.distance = rlk.distance;
				pLink.distance_from_link = lk.distance;
				pLinkList.add(pLink);
				p0 = true;
			}
				
			if (p0 == false){
				for (ResultLink rlk : linkList){
					//System.out.println(rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance);
					if (rlk.link.FName_Base.equals(cross_from) &&
							(linkList.size() > 1 || checkLinkBound(rlk.link.link_geom,rlk.link.link_dir,fromDir,rlk.link.dir_onsign))){
						//System.out.println("The dest P1 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
						PriorityLink pLink = new PriorityLink();
						pLink.endLink = rlk.link;
						pLink.startLink = lk.link;
						pLink.type = 1;
						pLink.distance = rlk.distance;
						pLink.distance_from_link = lk.distance;
						pLinkList.add(pLink);
						p1 = true;
						break;
					}
				}
			}
			if (p1 == false && p0 == false){
				for (ResultLink rlk : linkList){
					if (rlk.link.FName_Base.equals(cross_from) && !rlk.link.FName_Base.equals(link.FName_Base)){
						//System.out.println("The dest P2 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
						PriorityLink pLink = new PriorityLink();
						pLink.endLink = rlk.link;
						pLink.startLink = lk.link;
						pLink.type = 2;
						pLink.distance = rlk.distance;
						pLink.distance_from_link = lk.distance;
						pLinkList.add(pLink);
						p2= true;
						break;
					}
				}
			}
			if (p2 == false && p1 == false && p0 == false){
				for (ResultLink rlk : linkList){
					if (!rlk.link.FName_Base.equals(link.FName_Base)){
						//System.out.println("The dest P3 link is " + rlk.link.link_id + "," + rlk.link.link_dir + "," + rlk.link.FName_Base + "," + rlk.distance + "," + lk.distance);
						PriorityLink pLink = new PriorityLink();
						pLink.endLink = rlk.link;
						pLink.startLink = lk.link;
						pLink.type = 3;
						pLink.distance = rlk.distance;
						pLink.distance_from_link = lk.distance;
						pLinkList.add(pLink);
						break;
					}
				}
			}
		}
		
		if (pLinkList.size() == 0){
			return resultLinkList;
		}
		PriorityLink pLink = Collections.min(pLinkList);
		Link destLink = pLink.endLink;
		Link origLink = pLink.startLink;
		resultLinkList.type = pLink.type;
		Link curLink = destLink;
		Link orgLink = origLink;
		//System.out.println("start to do the traverse back from " + curLink + "," + pLink.type);
		//System.out.println("start to do the traverse end at " + orgLink);
		while(nextLinkTable.get(curLink) != null && 
				!nextLinkTable.get(curLink).equals(orgLink)){
			//System.out.println("pre link is " + nextLinkTable.get(curLink));
			curLink = nextLinkTable.get(curLink);
			if (resultLinkList.resultLinks.contains(curLink)){
				break;
			}
			if (curLink.FName_Base == null){
				resultLinkList.resultLinks.add(curLink);
			}
			else if (curLink.FName_Base.equals("")){
				resultLinkList.resultLinks.add(curLink);
			}
			else if (checkIfRamp(curLink.FName_Base)){
				resultLinkList.resultLinks.add(curLink);
			}
		}
		resultLinkList.connectorLink = destLink;
		resultLinkList.resultLinks.add(orgLink);
		return resultLinkList;
	}

	/**********************
	 * find the on-ramp link by matching the road name
	 * @param links
	 * @param FName_Base
	 * @return Link - return the ramp link
	 * @throws Exception
	 ***************/
	private ArrayList<Link> findOnRamp(ArrayList<Link> links,String FName_Base, String country) throws Exception{
		ResultSet rs = null;
		Statement stmt = null;
		ArrayList<Link> connectors = new ArrayList<Link>();
		try{
			stmt = connection1.createStatement();
			rs = null;
			String query = "";
			String fBase_name = null;
			for (Link lk : links){
				if (checkIfRamp(lk.FName_Base)){
					connectors.add(lk);
					continue;
				}
				if (lk.link_dir.equals("F")){
					query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
					" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE (t_node_id = " + lk.TNode +
					" AND linkdir = 'F') " + 
					" OR " + "(f_node_id = " + lk.TNode + " AND linkdir = 'T')";
				}
				else if (lk.link_dir.equals("T")){
					query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
					" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE (t_node_id = " + lk.FNode +
					" AND linkdir = 'F') " + 
					" OR " + "(f_node_id = " + lk.FNode + " AND linkdir = 'T')";
				}
				//System.out.println(query);
				rs = stmt.executeQuery(query);
				while(rs.next()){
					fBase_name = rs.getString("fname_base");
					if (checkIfRamp(fBase_name)){
						Link link = new Link();
						int link_id = rs.getInt("link_id");
						if (link_id == lk.link_id){
							continue;
						}
						link.link_id = link_id;			
						link.link_dir = rs.getString("linkdir");
						link.length = rs.getDouble("distance");
						link.FNode = rs.getInt("f_node_id");
						link.TNode = rs.getInt("t_node_id");
						link.FName_Base = rs.getString("fname_base");
						link.speedCat = rs.getInt("speedcat");
						if (link.FName_Base == null)
							link.FName_Base = "";
						connectors.add(link);
					}
				}
			}
			return connectors;
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

	/**********************
	 * find the off-ramp link by matching the road name
	 * @param links
	 * @param FName_Base
	 * @return Link - return the ramp link
	 * @throws Exception
	 **************/
	private ArrayList<Link> findOffRamp(ArrayList<Link> links,String FName_Base, String country) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<Link> connectors = new ArrayList<Link>();
		try{
			stmt = connection1.createStatement();
			rs = null;
			String query = "";
			String fBase_name = null;
			for (Link lk : links){
				if (checkIfRamp(lk.FName_Base)){
					connectors.add(lk);
					continue;
				}
				if (lk.link_dir.equals("F")){
					query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
					" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE (f_node_id = " + lk.FNode +
					" AND linkdir = 'F') " + 
					" OR " + "(t_node_id = " + lk.FNode + " AND linkdir = 'T')";
				}
				else if (lk.link_dir.equals("T")){
					query = "SELECT link_id,linkdir,distance,speedcat,f_node_id,t_node_id,fname_base,astext(link_geom) as geom" +
					" FROM " + Utils.getNTMapVersion() + "_ntlinks_" + country + " WHERE (f_node_id = " + lk.TNode +
					" AND linkdir = 'F') " + 
					" OR " + "(t_node_id = " + lk.TNode + " AND linkdir = 'T')";
				}
				//System.out.println(query);
				rs = stmt.executeQuery(query);
				while(rs.next()){
					fBase_name = rs.getString("fname_base");
					if (checkIfRamp(fBase_name)){
						Link link = new Link();
						int link_id = rs.getInt("link_id");
						if (link_id == lk.link_id){
							continue;
						}
						link.link_id = link_id;
						link.link_dir = rs.getString("linkdir");
						link.length = rs.getDouble("distance");
						link.FNode = rs.getInt("f_node_id");
						link.TNode = rs.getInt("t_node_id");
						link.FName_Base = rs.getString("fname_base");
						link.link_geom = getArrayFromLinestring(rs.getString("geom"));
						if (link.FName_Base == null)
							link.FName_Base = "";
						link.speedCat = rs.getInt("speedcat");
						connectors.add(link);
					}
				}
			}
			return connectors;
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
	 * Traverse forward to get next links
	 * @param gLink
	 * @return ArrayList<Link>
	 * @throws Exception
	 */
	private ArrayList<Link> getNextLinks(Link gLink, boolean includingRamps, String country) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = connection1.createStatement();
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
				String fname = rs.getString("fname_base");
				if (!includingRamps && checkIfRamp(fname)) {
					continue;
				}
				Link link = new Link();
				link.link_id = rs.getInt("link_id");		
				link.link_dir = rs.getString("linkdir");
				link.length = rs.getDouble("distance");
				link.FNode = rs.getInt("f_node_id");
				link.TNode = rs.getInt("t_node_id");
				link.FName_Base = fname;
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
	private ArrayList<Link> getPreviousLinks(Link gLink, boolean includingRamps, String country) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = connection1.createStatement();
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
				String fname = rs.getString("fname_base");
				if (!includingRamps && checkIfRamp(fname)) {
					continue;
				}
				Link link = new Link();
				link.link_id = rs.getInt("link_id");			
				link.link_dir = rs.getString("linkdir");
				link.length = rs.getDouble("distance");
				link.FNode = rs.getInt("f_node_id");
				link.TNode = rs.getInt("t_node_id");
				link.FName_Base = fname;
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


	/************
	 * Check the link bound
	 * @param linkGeom
	 * @param linkdir
	 * @param hwy_bound
	 * @param dir_onsign
	 * @return
	 * @throws Exception
	 */
	private boolean checkLinkBound(double[] linkGeom, String linkdir,
			String hwy_bound, String dir_onsign) throws Exception {

		if (hwy_bound == null || hwy_bound.equals(""))
			return false;

		if (dir_onsign != null && !dir_onsign.equals("") && hwy_bound.startsWith(dir_onsign))
			return true;

		if (calDiffLinkDirToBound(linkGeom, linkdir, hwy_bound) < 60)
			return true;
		
		return false;
	}

	/**************
	 * Calculate the differnce between the highway bound and link geometry
	 * @param linkGeom
	 * @param linkdir
	 * @param hwy_bound
	 * @return
	 * @throws Exception
	 */
	private double calDiffLinkDirToBound(double[] linkGeom, String linkdir,
			String hwy_bound) {
		double dmlat = 0.00;
		double dmlon = 0.00;
		double slat = 0.00;
		double slon = 0.00;
		double elat = 0.00;
		double elon = 0.00;
		double direction = 1000;
		int numOfPoint = linkGeom.length;

		if (linkdir.equals("T")) {
			slat = linkGeom[numOfPoint - 1];
			slon = linkGeom[numOfPoint - 2];
			elat = linkGeom[1];
			elon = linkGeom[0];
		} else if (linkdir.equals("F")) {
			elat = linkGeom[numOfPoint - 1];
			elon = linkGeom[numOfPoint - 2];
			slat = linkGeom[1];
			slon = linkGeom[0];
		} else {
			throw new IllegalArgumentException("Invalid linkdir value: " + linkdir);
		}

		dmlat = (elat - slat) * 69.0;
		dmlon = (elon - slon) * 69.0 * Math.cos(slat * PI180);

		double gamma = Math.atan2(dmlat, dmlon);

		gamma = -gamma + 0.5 * PI;

		if (gamma < 0)
			gamma = 2 * PI + gamma;

		gamma = PI180R * gamma;

		if (hwy_bound.equals("N") || hwy_bound.equals("NB"))
			direction = 0;
		else if (hwy_bound.equals("S") || hwy_bound.equals("SB"))
			direction = 180;
		else if (hwy_bound.equals("E") || hwy_bound.equals("EB"))
			direction = 90;
		else if (hwy_bound.equals("W") || hwy_bound.equals("WB"))
			direction = 270;
		else
			throw new IllegalArgumentException("Invalid highway bound value: " + hwy_bound);

		double diff = Math.abs(gamma - direction);

		if (diff < 180)
			return diff;
		else
			return 360 - diff;
	}

	private double[] getArrayFromLinestring(String lineStr)
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
}