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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	private State state = State.START;
	private String name;
	Point currentPoint;
	Point lastPoint;
	Point firstPoint;
	private double ascent;
	private double descent;
	private double maxHeight, minHeight;
	private Calendar c = null;
	private static final double SEUIL = 5.0;
	private final Rolling rollingAverage = new Rolling(10); // apply a bit of
															// smoothing to
															// glitchy altitude
															// data

	private float minLat, maxLat, minLon, maxLon;

	private double totalDistance;

	private StringBuilder contentBuffer;

	private final SimpleDateFormat sdfNoMillis = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	private final SimpleDateFormat sdfMillis = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy");

	public final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private int status;
	private long createDate;
	private String editTemplate;
	private String viewTemplate;
	private String ipAddr;
	private String canonicalUrl;

	/*
	 * public static ThreadLocal<SimpleDateFormat> sdf = new
	 * ThreadLocal<SimpleDateFormat>() {
	 * 
	 * @Override protected SimpleDateFormat initialValue() { return new
	 * SimpleDateFormat(DATETIME_FORMAT); } };
	 */

	private final Log _logger = LogFactory.getLog(GpxParser.class);

	public String getName() {
		return name;
	}

	public double getDistance() {
		return totalDistance;
	}

	public double getDistanceKM() {
		BigDecimal bd = new BigDecimal(totalDistance / 1000);
		return bd.setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue();
	}

	public double getClimb() {
		return ascent;
	}

	public double getDescent() {
		return descent;
	}

	public double getMaxElevation() {
		return maxHeight;
	}

	public double getMinElevation() {
		return minHeight;
	}

	public String getStartLat() {
		if (firstPoint != null) {
			return firstPoint.getLatitude();
		}

		return "";
	}

	public String getStartLon() {
		if (firstPoint != null) {
			return firstPoint.getLongitude();
		}

		return "";
	}

	public Date getStartTime() {
		return c.getTime();
	}

	public String getDate() {
		return dateFormat.format(c.getTime());
	}

	private void clear() {
		totalDistance = 0.0;
		ascent = 0;
		descent = 0;
		contentBuffer = new StringBuilder();
		firstPoint = null;
		lastPoint = null;
		currentPoint = null;

	}

	/*
	 * READ GPX DATA FILE
	 * 
	 * We merge all segments
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

		switch (state) {
		case START:
			if (qName.compareToIgnoreCase("gpx") == 0) {
				state = State.GPX;
			}
			break;
		case GPX:
			if (qName.compareToIgnoreCase("trk") == 0) {
				state = State.TRK;
			}
			break;
		case TRK:
			if (qName.compareToIgnoreCase("name") == 0) {
				state = State.NAME;
			}
			if (qName.compareToIgnoreCase("trkseg") == 0) {
				state = State.TRKSEG;
			}
			break;
		case TRKSEG:
			if (qName.compareToIgnoreCase("trkpt") == 0) {
				state = State.TRKPT;
				lastPoint = currentPoint;
				currentPoint = new Point(attributes.getValue("lat"),
						attributes.getValue("lon"));
			}
			break;
		case TRKPT:
			if (qName.compareToIgnoreCase("ele") == 0) {
				state = State.ELE;
			}
			if (qName.compareToIgnoreCase("time") == 0) {
				state = State.TIME;
			}
			break;
		}
		/**
		 * GPX Format:-
		 * 
		 * <gpx><metadata></metadata><trk><name>My Track</name><trkseg><trkpt
		 * lat="45.205372"
		 * lon="5.738155"><ele>248.64</ele><time>2012-09-01T11:14
		 * :12Z</time></trkpt><trkseg><trk></gpx>
		 */
		if (qName.compareToIgnoreCase("bounds") == 0) {
			minLat = new Float(attributes.getValue("minlat")).floatValue();
			maxLat = new Float(attributes.getValue("maxlat")).floatValue();
			minLon = new Float(attributes.getValue("minlon")).floatValue();
			maxLon = new Float(attributes.getValue("maxlon")).floatValue();
		}

		// Clear content buffer
		contentBuffer.delete(0, contentBuffer.length());
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

		switch (state) {
		case TRKPT:
			if (qName.compareToIgnoreCase("trkpt") == 0) {
				if (lastPoint != null) {
					double distance = distance(lastPoint.getLatAsDouble(),
							currentPoint.getLatAsDouble(),
							lastPoint.getLonAsDouble(),
							currentPoint.getLonAsDouble(),
							lastPoint.getElevation(),
							currentPoint.getElevation());

					totalDistance += distance;
				} else {
					// first time through
					firstPoint = currentPoint;
					minHeight = currentPoint.getElevation();
				}
				state = State.TRKSEG;
			}
			break;
		case NAME:
			name = contentBuffer.toString();
			state = State.TRK;
			break;
		case ELE:
			// http://www.gpsvisualizer.com/tutorials/elevation_gain.html

			currentPoint.setElevation(rollingAverage.add(Double
					.parseDouble(contentBuffer.toString().trim())));
			if (currentPoint.getElevation() > this.maxHeight) {
				maxHeight = currentPoint.getElevation();
			}
			if (currentPoint.getElevation() < minHeight) {
				minHeight = currentPoint.getElevation();
			}
			if (lastPoint != null) {
				double difference = currentPoint.getElevation()
						- lastPoint.getElevation();
				if (difference > 0) {
					// we are climbing
					ascent += difference;
				} else {
					descent -= difference;

				}
			}

			state = State.TRKPT;
			break;
		case TIME:
			c = this.readDate(contentBuffer.toString().trim());
			state = State.TRKPT;
			break;

		}
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

	/**
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * 
	 * @return distance in meters
	 */
	public static double distance(double lat1, double lat2, double lon1,
			double lon2, double el1, double el2) {

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2)
				* Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
	}
}

enum State {
	START, GPX, // from Start state
	METADATA, // from GPX state
	TRK, // from GPX state
	NAME, // from TRK state
	TRKSEG, // from TRK state
	TRKPT, // from TRKSEG state
	ELE, // from TRKSEG state
	TIME // from TRKSET stage
}

class Rolling {

	private int size;
	private double total = 0d;
	private int index = 0;
	private int count = 0;
	private double samples[];

	public Rolling(int size) {
		this.size = size;
		samples = new double[size];
		for (int i = 0; i < size; i++)
			samples[i] = 0d;
	}

	public double add(double x) {
		total -= samples[index];
		samples[index] = x;
		total += x;
		if (++index == size) {
			index = 0; // cheaper than modulus
		}
		count++;

		return getAverage();
	}

	public double getAverage() {
		if (count < size) {
			return total / count; // while it is filling up initially
		} else {
			return total / size;
		}
	}
}