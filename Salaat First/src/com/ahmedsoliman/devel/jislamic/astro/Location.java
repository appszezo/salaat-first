package com.ahmedsoliman.devel.jislamic.astro;

public class Location {

	static final double DEFAULT_SEA_LEVEL = 0;

	static final double DEFAULT_PRESSURE = 1010;

	static final double DEFAULT_TEMPERATURE = 10;

	private String name;

	private double degreeLong;

	private double degreeLat;

	private double gmtDiff;

	private int dst;

	private double seaLevel;

	private double pressure;

	private double temperature;

	private Location() {

	}

	public Location(String name, double degreeLat, double degreeLong) {
		this(name, degreeLat, degreeLong, 0, 0);
	}

	/**
	 * default constructor of location object. Latitude, Longitude, GMT
	 * difference and day saving time flag are required. Other settings (sea
	 * level, pressure, temperature) are given standard astronomical values and
	 * can be set later using setters.
	 * 
	 * 
	 * @param degreeLat
	 *            latitude in degrees
	 * @param degreeLong
	 *            longitude in degrees
	 * @param gmtDiff
	 *            difference with GMT
	 * @param dst
	 *            day saving time (1 to add one hour, 2 to add two, 0 if none,
	 *            etc..)
	 */
	public Location(String name, double degreeLat, double degreeLong,
			double gmtDiff, int dst) {
		this.name = name;
		this.degreeLong = degreeLong;
		this.degreeLat = degreeLat;
		this.gmtDiff = gmtDiff;
		this.dst = dst;

		this.seaLevel = DEFAULT_SEA_LEVEL;
		this.pressure = DEFAULT_PRESSURE;
		this.temperature = DEFAULT_TEMPERATURE;
	}

	public Location copy() {
		Location loc = new Location();

		// copy all fields
		loc.setDegreeLat(degreeLat);
		loc.setDegreeLong(degreeLong);
		loc.setGmtDiff(gmtDiff);
		loc.setDst(dst);
		loc.setSeaLevel(seaLevel);
		loc.setPressure(pressure);
		loc.setTemperature(temperature);

		return loc;
	}

	public double getDegreeLat() {
		return degreeLat;
	}

	public double getDegreeLong() {
		return degreeLong;
	}

	public int getDst() {
		return dst;
	}

	public double getGmtDiff() {
		return gmtDiff;
	}

	public String getName() {
		return name;
	}

	public double getPressure() {
		return pressure;
	}

	public double getSeaLevel() {
		return seaLevel;
	}

	public double getTemperature() {
		return temperature;
	}

	/**
	 * 
	 * @param degreeLat
	 *            Latitude in decimal degree.
	 */
	public void setDegreeLat(double degreeLat) {
		this.degreeLat = degreeLat;
	}

	/**
	 * 
	 * @param degreeLong
	 *            Longitude in decimal degree.
	 */
	public void setDegreeLong(double degreeLong) {
		this.degreeLong = degreeLong;
	}

	/**
	 * Daylight savings time switch (0 if not used). Set this to 1 should add 1
	 * hour to all the calculated prayer times
	 * 
	 * @param dst
	 */
	public void setDst(int dst) {
		this.dst = dst;
	}

	/**
	 * 
	 * @param gmtDiff
	 *            GMT difference at <b>regular time</b>.
	 */
	public void setGmtDiff(double gmtDiff) {
		this.gmtDiff = gmtDiff;
	}

	/**
	 * @param pressure
	 *            Atmospheric pressure in millibars (the astronomical standard
	 *            value is 1010 (<code>Location.DEFAULT_PRESSURE</code>))
	 */
	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	/**
	 * 
	 * @param seaLevel
	 *            Height above Sea level in meters
	 */
	public void setSeaLevel(double seaLevel) {
		this.seaLevel = seaLevel;
	}

	/**
	 * 
	 * @param temperature
	 *            Temperature in Celsius degree (the astronomical standard value
	 *            is 10)
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
}
