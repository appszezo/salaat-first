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

package org.hicham.salaat.settings;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class CustomPreferenceActivity extends PreferenceActivity{
	
	
    public static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
    public static final String EXTRA_PREFS_SHOW_SKIP = "extra_prefs_show_skip";
    public static final String EXTRA_PREFS_SHOW_HOME_AS_UP="extra_prefs_show_home_as_up";
    public static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
    public static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
    public static final String EXTRA_PREFS_SHOW_DESCRIPTION = "extra_prefs_show_description";
    public static final String EXTRA_PREFS_SET_DESCRIPTION_TEXT="extra_prefs_set_description";
    public static final String EXTRA_PREFS_DISABLE_NEXT_BUTTON="extra_prefs_disable_next";
    public static final String EXTRA_PREFS_DISABLE_BACK_BUTTON="extra_prefs_disable_back";
    public static final String EXTRA_PREFS_DISABLE_SKIP_BUTTON="extra_prefs_disable_skip";

    
    public static final int RESULT_SKIP=2;
    public static final int RESULT_NEXT=3;
    public static final int RESULT_BACK=4;
    
    private boolean isWizardModeEnabled=false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_preference_list_content_single);
        Intent intent=getIntent();
		if (intent.getBooleanExtra(
                EXTRA_PREFS_SHOW_BUTTON_BAR, false)) {
            isWizardModeEnabled=true;
			findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
            Button backButton = (Button) findViewById(R.id.back_button);
            backButton.setOnClickListener(new OnClickListener() {
                
                public void onClick(View v) {
                    setResult(RESULT_BACK);
                    finish();
                }
            });
            if(intent.getBooleanExtra(EXTRA_PREFS_DISABLE_BACK_BUTTON, false))
            	backButton.setEnabled(false);
            Button skipButton = (Button) findViewById(R.id.skip_button);
            skipButton.setOnClickListener(new OnClickListener() {
                
                public void onClick(View v) {
                    setResult(RESULT_SKIP);
                    finish();
                }
            });
            if(intent.getBooleanExtra(EXTRA_PREFS_DISABLE_SKIP_BUTTON, false))
            	skipButton.setEnabled(false);

            Button nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setOnClickListener(new OnClickListener() {
                
                public void onClick(View v) {
                    setResult(RESULT_NEXT);
                    finish();
                }
            });
            if(intent.getBooleanExtra(EXTRA_PREFS_DISABLE_NEXT_BUTTON, false))
            	nextButton.setEnabled(false);

            if (intent.hasExtra(EXTRA_PREFS_SET_NEXT_TEXT)) {
                String buttonText = intent
                        .getStringExtra(EXTRA_PREFS_SET_NEXT_TEXT);
                if (TextUtils.isEmpty(buttonText)) {
                    nextButton.setVisibility(View.GONE);
                } else {
                    nextButton.setText(reshapeSentence(buttonText));
                }
            }
            else
            	nextButton.setText(reshapeSentence(R.string.next_button_label));
            if (intent.hasExtra(EXTRA_PREFS_SET_BACK_TEXT)) {
                String buttonText = intent
                        .getStringExtra(EXTRA_PREFS_SET_BACK_TEXT);
                if (TextUtils.isEmpty(buttonText)) {
                    backButton.setVisibility(View.GONE);
                } else {
                    backButton.setText(reshapeSentence(buttonText));
                }
            }
            else
            	backButton.setText(reshapeSentence(R.string.back_button_label));
            
            if (intent.getBooleanExtra(
                    EXTRA_PREFS_SHOW_SKIP, false)) {
                skipButton.setVisibility(View.VISIBLE);
                skipButton.setText(reshapeSentence(R.string.skip_button_label));
            }
        }

		
		if(!intent.getBooleanExtra(EXTRA_PREFS_SHOW_HOME_AS_UP, true))
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}
		
		else
		{
			/*the up indicator is shown reversed in Jellybean or later versions when arabic is used,
			 * so deactivate this behavior until find a solution
			 */
			
			if(!(SalaatFirstApplication.prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE).contains("ar")
					&&
					Build.VERSION.SDK_INT>=16))
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		if(intent.getBooleanExtra(EXTRA_PREFS_SHOW_DESCRIPTION, false))
		{
			getSupportActionBar().hide();
			findViewById(R.id.list_header).setVisibility(View.VISIBLE);
			String description=intent.getStringExtra(EXTRA_PREFS_SET_DESCRIPTION_TEXT);
			TextView descriptionTextView=(TextView)findViewById(R.id.text_description);
			descriptionTextView.setText(reshapeSentence(description));
		}
	}
	
	@Override
	public void onBackPressed() {
		if(isWizardModeEnabled)/*don't close the actual activity, navigation is handled using button bar*/
			return;
		super.onBackPressed();
	}
}
