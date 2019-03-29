package org.magneato.service;

public class Point {
	String latitude;
	String longitude;
	String elevation;

	public Point(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setElevation(String elevation) {
		this.elevation = elevation;
	}
	
	public double getElevation() {
		return Double.parseDouble(elevation);
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
	public double getLatAsDouble() {
		return Double.parseDouble(latitude);
	}
	
	public double getLonAsDouble() {
		return Double.parseDouble(longitude);
	}
}