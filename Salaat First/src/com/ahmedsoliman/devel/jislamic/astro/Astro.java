/**
 *  Astro value container.
 *  
 */
package com.ahmedsoliman.devel.jislamic.astro;

public class Astro {
	private double jd;

	private double dec[];

	private double ra[];

	private double sid[];

	private double dra[];

	private double rsum[];

	public Astro() {
		dec = new double[3];
		ra = new double[3];
		sid = new double[3];
		dra = new double[3];
		rsum = new double[3];
	}

	public double[] getDec() {
		return dec;
	}

	public double[] getDra() {
		return dra;
	}

	public double getJd() {
		return jd;
	}

	public double[] getRa() {
		return ra;
	}

	public double[] getRsum() {
		return rsum;
	}

	public double[] getSid() {
		return sid;
	}

	public void setDec(double[] dec) {
		this.dec = dec;
	}

	public void setDra(double[] dra) {
		this.dra = dra;
	}

	public void setJd(double jd) {
		this.jd = jd;
	}

	public void setRa(double[] ra) {
		this.ra = ra;
	}

	public void setRsum(double[] rsum) {
		this.rsum = rsum;
	}

	public void setSid(double[] sid) {
		this.sid = sid;
	}

}
