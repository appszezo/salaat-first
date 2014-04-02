/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *  	
 *	@author Hicham BOUSHABA 2014 <hicham.boushaba@gmail.com>
 *	
 */

package org.hicham.salaat.db;

import static org.hicham.salaat.SalaatFirstApplication.TAG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.hicham.salaat.R;
import org.hicham.salaat.db.AhadithDatabaseHelper.Hadith;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import com.ahmedsoliman.devel.jislamic.astro.Location;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * this adapter is used to access the database, before use it, you must call
 * open(), and don't forget to call close() when job is done.
 */

public class DbAdapter {
	class DataBaseHelper extends SQLiteAssetHelper {
		public DataBaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			setForcedUpgrade();
		}
	}
	private static String DB_NAME = "database";
	private static final int DB_VERSION = 11;
	private DataBaseHelper mDbHelper;
	private SQLiteDatabase db;

	private Context context;
	public static final String CITIES_TABLE = "cities";
	public static final String COUNTRIES_TABLE = "countries";

	public static final String AHADITH_TABLE = "ahadith";

	/**
	 * 
	 * @param context
	 */
	public DbAdapter(Context context) {
		mDbHelper = new DataBaseHelper(context);
		this.context = context;
	}

	public void close() {
		db.close();
	}

	public String[] getCities(String country) {
		Cursor c = db.query(CITIES_TABLE, new String[] { "name" }, "country="
				+ DatabaseUtils.sqlEscapeString(country) + " AND name <> 'custom'", null, null, null, "name");
		String[] cities = new String[c.getCount()];
		int i = 0;
		while (c.moveToNext()) {
			cities[i] = c.getString(0);
			i++;
		}
		c.close();
		return cities;
	}

	public SimpleCursorAdapter getCitiesAsCursorAdapter(String country) {
		Cursor c = db.query(CITIES_TABLE, new String[] { "_id", "name" },
				"country=" + DatabaseUtils.sqlEscapeString(country) , null, null, null, "name");
		String[] from = new String[] { "name" };
		// create an array of the display item we want to bind our data to
		int[] to = new int[] { android.R.id.text1 };
		// create simple cursor adapter
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
				R.layout.simple_spinner_item, c, from, to);
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	public String[] getCountries() {
		Cursor c = db.query(COUNTRIES_TABLE, new String[] { "name" }, null,
				null, null, null, "name");
		String[] countries = new String[c.getCount()];
		int i = 0;
		while (c.moveToNext()) {
			countries[i] = c.getString(0);
			i++;
		}
		c.close();
		return countries;
	}

	public String getCountryName(String city) {
		Cursor c = db.query(CITIES_TABLE, new String[] { "country" }, "name="
				+ DatabaseUtils.sqlEscapeString(city) , null, null, null, null);
		String country = null;
		if (c.moveToFirst()) {
			country = c.getString(0);
		}
		return country;
	}

	public Hadith getHadith(int key) {
		Cursor c = db.query(AHADITH_TABLE, new String[] { "rowid", "hadith",
				"reference" }, "rowid='" + key + "'", null, null, null, null);
		c.moveToFirst();
		Hadith hadith = new Hadith(c.getInt(c.getColumnIndex("rowid")),
				c.getString(c.getColumnIndex("hadith")), c.getString(c
						.getColumnIndex("reference")));
		return hadith;
	}

	public long getHadithTableSize() {
		return DatabaseUtils.queryNumEntries(db, AHADITH_TABLE);
	}

	public Location getLocation(String city) {
		Cursor c = db.query(CITIES_TABLE, new String[] { "latitude",
				"longitude", "altitude" }, "lower(name)=lower(" + DatabaseUtils.sqlEscapeString(city) + ")",
				null, null, null, null);
		if (c.moveToFirst()) {
			double latitude = c.getDouble(c.getColumnIndex("latitude"));
			double longitude = c.getDouble(c.getColumnIndex("longitude"));
			double altitude = c.getDouble(c.getColumnIndex("altitude"));
			Log.i(TAG, "location " + latitude + "," + longitude + ","
					+ altitude);
			Location loc = new Location(city, latitude, longitude, 0, 0);
			loc.setSeaLevel(altitude);
			c.close();
			return loc;
		} else
			return null;
	}

	public Location getLocation(String city, double inLatitude,
			double inLongitude) {
		Cursor c = db.query(CITIES_TABLE, new String[] { "name", "latitude",
				"longitude", "altitude" }, "lower(name) like lower("+DatabaseUtils.sqlEscapeString("%" + city
				+ "%")+") and abs(latitude-(" + inLatitude
				+ "))<=0.5 and abs(longitude-(" + inLongitude + "))<=0.5", null,
				null, null, null);
		if (c.moveToFirst()) {
			double latitude = c.getDouble(c.getColumnIndex("latitude"));
			double longitude = c.getDouble(c.getColumnIndex("longitude"));
			double altitude = c.getDouble(c.getColumnIndex("altitude"));
			Log.i(TAG, "location " + latitude + "," + longitude + ","
					+ altitude);
			Location loc = new Location(c.getString(c.getColumnIndex("name")),
					latitude, longitude, 0, 0);
			loc.setSeaLevel(altitude);
			c.close();
			return loc;
		} else
			return null;
	}

	public Location getNearestLocation(final double longitude,
			final double latitude) {
		Cursor c = db.rawQuery("SELECT name,latitude,longitude,altitude FROM "
				+ CITIES_TABLE +" WHERE name <> 'custom' "
				+ " ORDER BY abs(latitude - ?) + abs( longitude - ?) LIMIT 10",
				new String[] { "" + latitude, "" + longitude });
		ArrayList<Location> list = new ArrayList<Location>();
		while (c.moveToNext()) {
			String name = c.getString(0);
			double degreeLat = c.getDouble(1);
			double degreeLong = c.getDouble(2);
			double altitude=c.getDouble(3);
			Location location = new Location(name, degreeLat, degreeLong);
			location.setSeaLevel(altitude);
			list.add(location);
		}

		c.close();
		if (list.isEmpty()) {
			return null;
		}
		Collections.sort(list, new Comparator<Location>() {
			public int compare(Location lhs, Location rhs) {
				float[] lhsDiff = new float[1];
				float[] rhsDiff = new float[1];
				android.location.Location.distanceBetween(latitude, longitude,
						lhs.getDegreeLat(), lhs.getDegreeLong(), lhsDiff);
				android.location.Location.distanceBetween(latitude, longitude,
						rhs.getDegreeLat(), rhs.getDegreeLong(), rhsDiff);
				return lhsDiff[0] - rhsDiff[0] < 0 ? -1 : 1;
			}
		});
		return list.get(0);
	}
	
	public Location getCustomNearestLocation() {
		Location loc=getLocation("custom");
		return getNearestLocation(loc.getDegreeLong(), loc.getDegreeLat());
	}

	public DbAdapter open() {
		if (db == null)
			db = mDbHelper.getWritableDatabase();
		return this;
	}

	public void setCustomCity(double longitude, double latitude, double altitude, String country) {
		ContentValues content = new ContentValues();
		content.put("country", country);
		content.put("longitude", longitude);
		content.put("latitude", latitude);
		content.put("altitude", altitude);
		db.update(CITIES_TABLE, content, "name='custom'", null);
	}

}
