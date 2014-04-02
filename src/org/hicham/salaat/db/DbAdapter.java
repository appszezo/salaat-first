/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *
*
*	@author Hicham BOUSHABA 2011 <hicham.boushaba@gmail.com>
*	
*/

package org.hicham.salaat.db;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.cities.City;
import org.hicham.salaat.cities.Country;
import org.hicham.salaat.helper.FileHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * this adapter is used to access the database,
 * before use it, you must call open(), and don't forget to call close()
 * when job is done.
 */

public class DbAdapter {
    private static String DB_DIR = "/data/data/org.hicham.salaat/databases/";
    private static String DB_NAME = "citiesDataBase";
    private static String DB_PATH = DB_DIR + "salaat";
    
	
	private DataBaseHelper mDbHelper;
	private SQLiteDatabase db;
	/**
	 * 
	 * @param context
	 */
	public DbAdapter(Context context) {
		mDbHelper=new DataBaseHelper(context);
	}
	
	class DataBaseHelper extends SQLiteOpenHelper
	{
	    private boolean createDatabase = false;
	    Context context;
		public DataBaseHelper(Context context) {
			super(context, "salaat", null, 1);
			this.context=context;
		}
		
				
		public void initializeDataBase() {
	        /*
	         * Creates or updates the database in internal storage
	         */
	        getWritableDatabase();

	        if (createDatabase) {
	            try {
	                //Copying the database from assets folder to internal storage
	                copyDataBase();
	            } catch (IOException e) {
	                throw new Error("Error copying database");
	            }
	        } 

	    }

	   
	    private void copyDataBase() throws IOException {
	       
	        close();

	        
	        InputStream input = context.getAssets().open(DB_NAME);

	        /*
	         * Open the empty db in interal storage as the output stream.
	         */
	        OutputStream output = new FileOutputStream(DB_PATH);

	        /*
	         * Copy over the empty db in internal storage with the database in the
	         * assets folder.
	         */
	        FileHelper.copyFile(input, output);

	        /*
	         * Access the copied database so SQLiteHelper will cache it and mark it
	         * as created.
	         */
	        getWritableDatabase().close();
	    }

	   
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        /*
	         * Signal that a new database needs to be copied. 
	         */
	        createDatabase = true;
	        
	        
	    }

	   
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	    }

	    /**
	     * Called everytime the database is opened by getReadableDatabase or
	     * getWritableDatabase. This is called after onCreate or onUpgrade is
	     * called.
	     */
	    @Override
	    public void onOpen(SQLiteDatabase db) {
	        super.onOpen(db);
	    }
	}

	
	public DbAdapter open()
	{
		mDbHelper.initializeDataBase();
		if(db==null)
			db=mDbHelper.getWritableDatabase();
		return this;
	}
	


	
	public void close()
	{
		db.close();
	}
	
	
	public void truncate()
	{
		db.execSQL("DELETE FROM driveTests");
		db.execSQL("DELETE FROM results");
	}
	
	public long insertCountry(Country country)
	{
		ContentValues values=new ContentValues();
		values.put("name", country.getName());
		return db.insert("countries",null,values);
	}
	
	
	
	public long insertCity(City city, Country country)
	{
		ContentValues values=new ContentValues();
		values.put("country", country.getName());
		values.put("name", city.getName());
		values.put("longitude", city.getLongitude());
		values.put("latitude", city.getLatitude());
		return db.insert("results",null,values);
	}
	
	public String[] getCountries()
	{
		Cursor c=db.query("countries", new String[]{"name"}, null, null, null, null, null);
		String[] countries=new String[c.getCount()];
		int i=0;
		while(c.moveToNext())
		{
			countries[i]=c.getString(0);
			i++;
		}
		c.close();
		return countries;
	}
	
	public String[] getCities(String country)
	{
		Cursor c=db.query("cities", new String[]{"name"}, "country='"+country+"'", null, null, null, null);
		String[] cities=new String[c.getCount()];
		int i=0;
		while(c.moveToNext())
		{
			cities[i]=c.getString(0);
			i++;
		}
		c.close();
		return cities;
	}
	
	/**
	 * 
	 * @param city
	 * @return {latitude, longitude, altitude}
	 */
	public double[] getLocation(String city)
	{
		//the name of column must be changed
		Cursor c=db.query("cities", new String[]{"latitude","longitude","altitude"}, "name='"+city+"'", null, null, null, null);
		double[] coordoones=new double[3];
		c.moveToFirst();
		coordoones[0]=c.getDouble(c.getColumnIndex("latitude"));
		coordoones[1]=c.getDouble(c.getColumnIndex("longitude"));
		coordoones[2]=c.getDouble(c.getColumnIndex("altitude"));
		Log.i(PrayerTimesActivity.TAG, "location "+coordoones[0]+","+coordoones[1]+","+coordoones[2]);
		c.close();
		return coordoones;
	}
	
	public void setCustomCity(double longitude, double latitude, double altitude)
	{
		ContentValues content=new ContentValues();
		content.put("longitude", longitude);
		content.put("latitude", latitude);
		content.put("altitude",altitude);
		db.update("cities", content, "name='custom'", null);
	}
}
