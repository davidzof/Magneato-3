package org.magneato.service;

public class Point {
	private String latitude;
	private String longitude;
	private Double elevation;

	public Point(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
	
	public double getElevation() {
		return elevation;
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

