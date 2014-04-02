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

package org.hicham.salaat.ui.activities;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.hicham.salaat.R;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				reshapeSentence(R.string.about));

		setContentView(R.layout.about);
		TextView version = (TextView) findViewById(R.id.version);
		version.setText("");
		try {
			String versionName = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			version.setText("Version: " + versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openFacebookPage(View view)
	{
		Intent intent = null;
		try {
			getBaseContext().getPackageManager()
					.getPackageInfo("com.facebook.katana", 0);
			intent = new Intent(Intent.ACTION_VIEW, Uri
					.parse("fb://page/557214157719272"));
		} catch (Exception e) {
			intent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/SalaatFirst"));
		}

		startActivity(intent);

	}
}
