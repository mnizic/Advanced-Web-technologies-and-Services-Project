package org.foi.nwtis.mnizic.aplikacija_4.mvc;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;

@SessionScoped
public class Zeton implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3933366656640483703L;

	private int t;

	private int v;

	public Zeton() {

	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}
}
