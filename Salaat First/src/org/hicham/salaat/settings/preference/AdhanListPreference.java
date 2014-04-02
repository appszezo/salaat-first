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

package org.hicham.salaat.settings.preference;

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.hicham.salaat.SalaatFirstApplication.TAG;

import java.io.File;
import java.util.Arrays;

import org.hicham.salaat.R;
import org.hicham.salaat.R.raw;
import org.hicham.salaat.media.MediaHandler;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.preference.ListPreference;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import ar.com.daidalos.afiledialog.FileChooserDialog.OnFileSelectedListener;

public class AdhanListPreference extends ListPreference implements OnFileSelectedListener{

	private int mClickedDialogEntryIndex;
	private MediaHandler mediaHandler;
	private Uri chosenUri;
	private String mSecondaryKey;
	private int originalEntriesLength;
	public static final String BROWSE_VALUE="browse";
	private final String DEFAULT_SECONDARY_VALUE="android.resource://" + getContext().getPackageName()
		+ "/raw/makkah";

	public AdhanListPreference(Context context) {
		super(context);
	}
	
	
	public String getSecodaryKey() {
		return mSecondaryKey;
	}

	private Uri getUri(String sound) {
		Class<raw> rawClass = R.raw.class;
		int res = 0;
		try {
			res = (Integer) (rawClass.getField(sound).get(null));
		} catch (IllegalArgumentException e1) {
			Log.e(TAG, e1.getClass().getName() + " " + e1.getMessage());
			return null;
		} catch (SecurityException e1) {
			Log.e(TAG, e1.getClass().getName() + " " + e1.getMessage());
			return null;
		} catch (IllegalAccessException e1) {
			Log.e(TAG, e1.getClass().getName() + " " + e1.getMessage());
			return null;
		} catch (NoSuchFieldException e1) {
			Log.e(TAG, e1.getClass().getName() + " " + e1.getMessage());
			return null;
		}
		return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ getContext().getResources().getResourcePackageName(res) + '/'
				+ getContext().getResources().getResourceTypeName(res) + '/'
				+ getContext().getResources().getResourceEntryName(res));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (mediaHandler != null) {
			mediaHandler.stopSound();
			mediaHandler.releasePlayerAndRestoreVolume();
		}

		super.onDialogClosed(positiveResult);

		if (positiveResult && mClickedDialogEntryIndex >= 0 
				&& getEntryValues() != null && chosenUri != null) {
			String value = getEntryValues()[mClickedDialogEntryIndex]
					.toString();
			if (callChangeListener(value)) {
				setValue(value);
				if(mClickedDialogEntryIndex<originalEntriesLength)
				getSharedPreferences().edit()
						.putString(getSecodaryKey(), chosenUri.toString())
						.commit();
				else
					/*the custom file is re-chosen, store it*/
					getSharedPreferences().edit()
					.putString(getSecodaryKey(), value)
					.commit();
			}
		}
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		mediaHandler = new MediaHandler(getContext());
		if (super.getEntries() == null || super.getEntryValues() == null) {
			throw new IllegalStateException(
					"ListPreference requires an entries array and an entryValues array.");
		}
		
		if(originalEntriesLength==0)
		{
			originalEntriesLength=getEntries().length;
		}
		
		mClickedDialogEntryIndex=findIndexOfValue(getValue());
		if(mClickedDialogEntryIndex==-1)
		{
			mClickedDialogEntryIndex=originalEntriesLength;
			setEntries(copyArray(getEntries(), originalEntriesLength+1));
			getEntries()[mClickedDialogEntryIndex]=Uri.parse(getSharedPreferences().getString(getSecodaryKey(), DEFAULT_SECONDARY_VALUE)).getLastPathSegment();
			setEntryValues(copyArray(getEntryValues(), originalEntriesLength+1));
			getEntryValues()[mClickedDialogEntryIndex]=getSharedPreferences().getString(getSecodaryKey(), DEFAULT_SECONDARY_VALUE);
		}
	
		builder.setSingleChoiceItems(getEntries(), mClickedDialogEntryIndex,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// setting the selected item
						mClickedDialogEntryIndex = which;

						// playing the adhan sound
						if (which <= originalEntriesLength - 1) {
							String value = getEntryValues()[which].toString();
							chosenUri = getUri(value);
							mediaHandler.playSound(chosenUri);
						} else {
							/*A custom file is chosen, play it*/
							mediaHandler.playSound(Uri.parse(getEntryValues()[which].toString()));
						}
					}
				});
		Button browseButton=new Button(getContext());
		browseButton.setText(reshapeSentence(R.string.browse));
		browseButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			    AdhanListPreference.this.getDialog().hide();
			    if(mediaHandler!=null)
			    {
			    	mediaHandler.stopSound();
			    }
				FileChooserDialog dialog = new FileChooserDialog(getContext());
			    dialog.loadFolder(Environment.getExternalStorageDirectory().toString());
			    dialog.addListener(AdhanListPreference.this);
			    dialog.setFilter(".*mp3|.*ogg|.*flac|.*wav|.*mid|.*3gp|.*aac|.*mp4|.*m4a|"
			     		+ ".*MP3|.*OGG|.*FLAC|.*WAV|.*MID|.*3GP|.*AAC|.*MP4|.*M4A");
			    dialog.setShowOnlySelectable(true);
			    dialog.setNavigationByBackButton(true);
			    dialog.setOnCancelListener(new OnCancelListener() {
					
					public void onCancel(DialogInterface arg0) {
						try{
						AdhanListPreference.this.getDialog().show();
						}
						catch(Exception e)
						{
							/*the dialog is may be null, so quit*/
						}
					}
				});
			    dialog.show();
			}
		});
		builder.setView(browseButton);
		
		builder.setPositiveButton("Ok", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				AdhanListPreference.this.onClick(dialog,
						DialogInterface.BUTTON_POSITIVE);
				dialog.dismiss();
			}
		});
	}

	public void setSecondaryKey(String secondaryKey) {
		mSecondaryKey = secondaryKey;
	}

	public void onFileSelected(Dialog source, File file) {
		Uri uri=Uri.fromFile(file);
		int i=file.getName().lastIndexOf('.');
		String extension;
		if(i>0)
			extension = file.getName().substring(i+1);
		else extension="";
		String mimetype = android.webkit.MimeTypeMap.getSingleton()
				.getMimeTypeFromExtension(extension);

		if (mimetype == null || !mimetype.contains("audio")) {
			Toast.makeText(getContext(),
					"The selected file isn't an audio file",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(getDialog()!=null)
				getDialog().dismiss();
		source.dismiss();
		String FileName = uri.getLastPathSegment();
		Log.i(TAG, FileName+" MIMETYPE: "+mimetype);
		
		setValue(uri.toString());
		getSharedPreferences().edit().putString(getSecodaryKey(), uri.toString()).commit();
		Toast.makeText(
				getContext(),
				FileName + " "
						+ reshapeSentence(R.string.file_selected),
				Toast.LENGTH_SHORT).show();
	}

	public void onFileSelected(Dialog source, File folder, String name) {
		// TODO Auto-generated method stub
		
	}
	
	private CharSequence[] copyArray(CharSequence[] original, int newLength)
	{
		if(Build.VERSION.SDK_INT>=9)
			return Arrays.copyOf(original, newLength);
		else
		{
			CharSequence[] dest=new CharSequence[newLength];
			System.arraycopy(original, 0, dest, 0, original.length);
			return dest;
		}
	}
}
