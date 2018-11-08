package org.magneato.service;

public class Point {
	String latitude;
	String longitude;
	String height;

	public Point(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
	public float getLatAsFloat() {
		return Float.parseFloat(latitude);
	}
	
	public float getLonAsFloat() {
		return Float.parseFloat(longitude);
	}
}