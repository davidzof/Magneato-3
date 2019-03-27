/*
 * Copyright 2010-2013, David George, Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.magneato.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Parse the popular Garmin GPX format
 *
 * Can contain multiple tracks
 * 
 * @author dgeorge
 * 
 */
public class GpxParser extends org.xml.sax.helpers.DefaultHandler {
	private Stack<String> elementNames;
	private ArrayList<Point> points = new ArrayList<Point>();
	private float minLat, maxLat, minLon, maxLon;
	private float maxHeight, minHeight;
	private int totalPoints;
	private long totalSeconds;
	private double totalDistance;
	private String name;

	private double lastHeight;
	private double totalAscent;
	private double runningAscent;

	private double totalDescent;
	private int vam;

	private StringBuilder contentBuffer;
	private double currentDistance;

	private static final SimpleDateFormat sdfNoMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final SimpleDateFormat sdfMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private final Log _logger = LogFactory.getLog(GpxParser.class);


	public String getName() {
		return name;
	}

	public long getTotalSeconds() {
		return totalSeconds;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public double getTotalAscent() {
		return totalAscent;
	}

	public double getTotalDescent() {
		return totalAscent;
	}

	public int getVam() {
		return vam;
	}


	private void clear() {
		totalPoints = 0;
		totalDistance = 0.0;

		lastHeight = -1.0;
		System.out.println("lastHeight " + lastHeight);
		totalAscent = 0;

		runningAscent = 0;
		contentBuffer = new StringBuilder();
		elementNames = new Stack<String>();
	}

	/*
	 * READ GPX DATA FILE
	 * 
	 * Profile Profile data should be of the form: [0, 200.60, 'null'], [1,
	 * 230.60, '4.5%']... where the first variable is the distance, the second
	 * if the height and the third a percentage gradient
	 */
	public int read(InputStream geoFile) {
		clear();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(geoFile, this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				geoFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return 0;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// the <bounds> element has attributes which specify min & max latitude
		// and longitude
		/**
		 * GPX Format:-
		 * 
		 * <gpx><metadata></metadata><trk> <name>My Track</name><trkseg><trkpt lat="45.205372"
		 * lon="5.738155"><ele>248.64</ele><time>2012-09-01T11:14:12Z</time></trkpt></gpx>
		 */
		if (qName.compareToIgnoreCase("bounds") == 0) {
			minLat = new Float(attributes.getValue("minlat")).floatValue();
			maxLat = new Float(attributes.getValue("maxlat")).floatValue();
			minLon = new Float(attributes.getValue("minlon")).floatValue();
			maxLon = new Float(attributes.getValue("maxlon")).floatValue();
		} else if (qName.compareToIgnoreCase("trkpt") == 0) {
				// the <trkpt> element has attributes which specify latitude and
				// longitude (it has child elements that specify the time and
				// elevation)
				totalPoints++;
				Point point = new Point(attributes.getValue("lat"),
						attributes.getValue("lon"));
				points.add(point);
		}

		// Clear content buffer
		contentBuffer.delete(0, contentBuffer.length());

		// Store name of current element in stack
		elementNames.push(qName);
	}

	/*
	 * the DefaultHandler::characters() function fires 1 or more times for each
	 * text node encountered
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		contentBuffer.append(String.copyValueOf(ch, start, length));
	}

	/*
	 * the DefaultHandler::endElement() function fires for each end tag
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String currentElement = elementNames.pop();
		if (currentElement != null) {
		    switch(currentElement) {
		    case "name":
		    	name = contentBuffer.toString();
		    	break;
		    }

			if (totalPoints > 0) {
				if (currentElement.compareToIgnoreCase("ele") == 0) {
					double elevation = Double.parseDouble(contentBuffer.toString()
							.trim());

					// this is bad code, need another way of determining first time
					// through, why not just subtract first data point at end?
					if (lastHeight < 0.0) {
						// first time thru'
						System.out.println("first time through " + elevation);
						lastHeight = elevation;
					}

					if (elevation > lastHeight) {
						// we are climbing
						if (runningAscent >= 0.0) {
							// climbing trend
							runningAscent += elevation - lastHeight;
						}
					} else {
						// we are descending
						if (runningAscent > 10.0) {
							totalAscent += runningAscent;

						}
						runningAscent = 0.0;
					}

					lastHeight = elevation;

					System.out.println("total Ascent " + totalAscent + " ascent "
							+ runningAscent + " distance " + totalDistance);
				} else {
					if (currentElement.compareToIgnoreCase("time") == 0) {
						Calendar c = this.readDate(contentBuffer.toString().trim());
					} else if (currentElement.compareToIgnoreCase("trkpt") == 0) {
						if (totalPoints > 1) {

							Point oldPoint = points.get(totalPoints - 2);
							Point newPoint = points.get(totalPoints - 1);
							double distance = gps2m(oldPoint.getLatAsFloat(),
									oldPoint.getLonAsFloat(),
									newPoint.getLatAsFloat(),
									newPoint.getLonAsFloat());
							totalDistance += distance;

							System.out
									.println("distance from last point " + distance
											+ " total distance " + totalDistance);
						}
					}
				}
			}
		}
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	// TODO: do we need to copy this?, I think so because we could have someone
	// updating the points
	// while we read them.
	public ArrayList<Point> getPoints() {
		return points;
	}

	/*
	 * Distance in meters between two points, what about vertical?
	 */
	private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
		float pk = (float) (180 / 3.14169);

		float a1 = lat_a / pk;
		float a2 = lng_a / pk;
		float b1 = lat_b / pk;
		float b2 = lng_b / pk;

		double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
		double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
		double t3 = Math.sin(a1) * Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 6366000 * tt;
	}

	private Calendar readDate(String date) {
		Calendar c = Calendar.getInstance();
		Date d;
		
		try {
			d = sdfNoMillis.parse(date);
		} catch (ParseException e) {
			try {
				d = sdfMillis.parse(date);
			} catch (ParseException e1) {
				_logger.error("Error parsing the date: " + date);
				return null;
			}
		}
		
		c.setTime(d);
		return c;
	}

}
