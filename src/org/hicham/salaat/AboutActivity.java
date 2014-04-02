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

package org.hicham.salaat;

import org.hicham.salaat.R;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;
import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;

public class AboutActivity extends Activity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		LinearLayout layout=(LinearLayout)findViewById(R.id.aboutLayout);
        Display displayManager=getWindowManager().getDefaultDisplay();
        int width=displayManager.getWidth();
        int height=displayManager.getHeight();
        layout.setPadding(width/4+width/30, height/2-height/10, 0, 0);
		
		TextView version=(TextView)findViewById(R.id.version);
		TextView realizedBy=(TextView)findViewById(R.id.realizedBy);
		TextView author=(TextView)findViewById(R.id.author);
		version.setText("");
		realizedBy.setText(reshapeText(getString(R.string.realizedBy), PrayerTimesActivity.isReshapingNessecary));
		author.setText(reshapeText(getString(R.string.author),PrayerTimesActivity.isReshapingNessecary));
		try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0 ).versionName;
			version.setText("Version: "+versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
