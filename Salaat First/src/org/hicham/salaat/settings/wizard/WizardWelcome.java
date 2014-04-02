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

package org.hicham.salaat.settings.wizard;

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.arabic.ArabicUtilities.reshapeText;
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.AdkarSettings;
import org.hicham.salaat.settings.CitySettings;
import org.hicham.salaat.settings.CustomPreferenceActivity;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.PrayerSettingsScreen;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;


public class WizardWelcome extends Activity  {
	
    
    private static final int REQUEST_CODE=1;
    
    private Spinner languageSpinner;
    @SuppressWarnings("rawtypes")
	private Class[] settings=new Class[]{CitySettings.class, PrayerCalculationSettings.class, PrayerSettingsScreen.class, AdkarSettings.class};
    private int[] descriptions=new int[]{R.string.city_settings_title, R.string.calculation_settings_title, R.string.prayer_settings_title, R.string.adkar_notifications_title};
    private int currentStep=0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null)
		{
			currentStep=savedInstanceState.getInt("current_step");
		}
		setContentView(R.layout.wizard_layout);
		
		final TextView message=(TextView)findViewById(R.id.welcome_message);
		message.setText(Html.fromHtml(reshapeSentence(R.string.wizard_welcome_text)));
		
		final Button start=(Button)findViewById(R.id.start);
		start.setText(reshapeSentence(R.string.wizard_start_button));
		start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				currentStep=0;
				showSelectedStep();
				findViewById(R.id.outer_layout).setVisibility(View.INVISIBLE);
			}
		});
		
		languageSpinner=(Spinner)findViewById(R.id.change_language_spinner);
		ArrayAdapter <CharSequence> adapter =
				  new ArrayAdapter <CharSequence> (this, R.layout.simple_spinner_item );
				adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		final String[] languages=reshapeText(getResources().getStringArray(R.array.languages));
		adapter.addAll(languages);
		languageSpinner.setAdapter(adapter);
		//selectedLanguage=
		languageSpinner.setSelection(getLanguagePosition(SalaatFirstApplication.prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE)));
		languageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(true)
				{
					SalaatFirstApplication.prefs.edit().putString(Keys.LANGUAGE_KEY, getSelectedLanguage(position)).commit();
					message.setText(Html.fromHtml(reshapeSentence(R.string.wizard_welcome_text)));
					message.invalidate();
					start.setText(reshapeSentence(R.string.wizard_start_button));
					findViewById(R.id.outer_layout).invalidate();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void showSelectedStep()
	{
		Intent intent=new Intent(WizardWelcome.this, settings[currentStep]);
		intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_SHOW_BUTTON_BAR, true);
		intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_SHOW_HOME_AS_UP, false);
		intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_SHOW_SKIP, true);
		intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_SHOW_DESCRIPTION, true);
		intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_SET_DESCRIPTION_TEXT, getString(descriptions[currentStep]));
		
		if(currentStep==settings.length-1)
			{
				intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_SET_NEXT_TEXT, getString(R.string.finish_wizard_label));
				intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_DISABLE_SKIP_BUTTON, true);
			}
		if(currentStep==0)
			intent.putExtra(CustomPreferenceActivity.EXTRA_PREFS_DISABLE_BACK_BUTTON, true);	
		
		startActivityForResult(intent, REQUEST_CODE);

	}
	
	
	public void recreate() {
		Intent intent=getIntent();
		finish();
		startActivity(intent);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("current_step", currentStep);
	}
	
	private String getSelectedLanguage(int selection) {
		return getResources().getStringArray(R.array.languages_values)[selection];
	}
	
	
	private int getLanguagePosition(String language)
	{
		String[] languageValues=getResources().getStringArray(R.array.languages_values);
		for(int i=0; i<languageValues.length; i++)
		{
			if(languageValues[i].equals(language))
				return i;
		}
		return -1;
	}


	private void prepareWizardCompletionInterface() {
		findViewById(R.id.imageView1).setVisibility(View.GONE);
		findViewById(R.id.change_language_spinner).setVisibility(View.GONE);
		((TextView)findViewById(R.id.welcome_message)).setText(Html.fromHtml(reshapeSentence(R.string.wizard_completion_message)));
		((Button)findViewById(R.id.start)).setText(reshapeSentence(R.string.wizard_close_button));
		((Button)findViewById(R.id.start)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
				prefs.edit().putBoolean("firstrun_version_2", false).commit();
			}
		});
		findViewById(R.id.outer_layout).setVisibility(View.VISIBLE);
		findViewById(R.id.outer_layout).invalidate();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case REQUEST_CODE:
			if(resultCode==CustomPreferenceActivity.RESULT_NEXT)
			{
				if(currentStep==settings.length-1)
				{
					prepareWizardCompletionInterface();
				}
				else
				{
					currentStep++;
					showSelectedStep();
				}
			}
			if(resultCode==CustomPreferenceActivity.RESULT_BACK)
			{
				currentStep--;
				showSelectedStep();
			}
			if(resultCode==CustomPreferenceActivity.RESULT_SKIP)
			{
				finish();
				prefs.edit().putBoolean("firstrun_version_2", false).commit();
			}
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		/*continue*/
	}
	
}