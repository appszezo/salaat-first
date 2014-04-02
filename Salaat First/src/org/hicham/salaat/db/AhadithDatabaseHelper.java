/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *   
 *
 *	@author Hicham BOUSHABA 2014 <hicham.boushaba@gmail.com>
 *	
 */

package org.hicham.salaat.db;

import static org.hicham.salaat.SalaatFirstApplication.dBAdapter;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class AhadithDatabaseHelper {

	public static class Hadith {
		private int id;
		private String text;
		private String reference;

		public Hadith(int id, String text, String reference) {
			super();
			this.id = id;
			// this.text = text;
			try {
				byte[] bytes = text.getBytes("utf-8");
				this.text = new String(bytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				byte[] bytes = reference.getBytes("utf-8");
				this.reference = new String(bytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formatText();
		}

		private void formatText() {
			// TODO
		}

		public int getId() {
			return id;
		}

		public String getReference() {
			return reference;
		}

		public String getText() {
			return text;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	public static Hadith getRandomHadith() {
		Random random = new Random();
		int key = random.nextInt((int) (dBAdapter.getHadithTableSize())) + 1;
		Hadith hadith = dBAdapter.getHadith(key);
		return hadith;
	}
}
