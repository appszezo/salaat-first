/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.hicham.salaat.settings.preference;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.holoeverywhere.preference.DialogPreference;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

public class SeekBarPreference extends DialogPreference implements
SeekBar.OnSeekBarChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";

	private SeekBar mSeekBar;
	private TextView mSplashText, mSuffixText;

	protected TextView mValueText;
	private Context mContext;

	private String mDialogMessage, mSuffix, mMinutes;
	private int mDefault, mMax, mMin=1, mValue = 0;

	public SeekBarPreference(Context context) {
		super(context, null);
		mContext = context;
	}

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
		mSuffix = attrs.getAttributeValue(androidns, "text");
		mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		mMax = attrs.getAttributeIntValue(androidns, "max", 100);

	}

	public int getMax() {
		return mMax;
	}

	public int getValue() {
		return mValue;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
	}

	@Override
	protected View onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		mSplashText = new TextView(mContext);
		mSplashText.setSingleLine();
		mSplashText.setGravity(Gravity.CENTER_HORIZONTAL);
		if (mDialogMessage != null)
			mSplashText.setText(mDialogMessage);
		layout.addView(mSplashText);

		LinearLayout horizLayout=new LinearLayout(mContext);
		horizLayout.setOrientation(LinearLayout.HORIZONTAL);
		horizLayout.setPadding(6, 6, 6, 6);
		horizLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		mSuffixText = new TextView(mContext);
		mSuffixText.setSingleLine();
		mSuffixText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		mMinutes="";
		mValueText = new TextView(mContext);
		mValueText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		if(SalaatFirstApplication.prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE).contains("ar"))
		{
			horizLayout.addView(mSuffixText);
			horizLayout.addView(mValueText);
		}
		else
		{
			horizLayout.addView(mValueText);
			horizLayout.addView(mSuffixText);
		}

		layout.addView(horizLayout, params);

		mSeekBar = new SeekBar(mContext);
		mSeekBar.setOnSeekBarChangeListener(this);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		if (shouldPersist())
			mValue = getPersistedInt(mDefault);

		//mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
		return layout;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult)
			if (shouldPersist())
				persistInt(mValue);
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		if(value<mMin)
		{
			mSeekBar.setProgress(mMin);
			return;
		}
		if(!callChangeListener(new Integer(value)))
		{
			mSeekBar.setProgress(mValue);
			return;
		}
		prepareUi(value);
		mValue = value;

	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
			else
				mValue = (Integer) defaultValue;
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		mDefault = (Integer) defaultValue;
		super.setDefaultValue(defaultValue);
	}

	public void setMax(int max) {
		mMax = max;
	}

	public void setMin(int min)
	{
		mMin=min;
	}

	public void setProgress(int progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(progress);
	}

	public void setSuffix(String suffix)
	{
		mSuffix=suffix;
	}

	public void setMessage(String message)
	{
		mDialogMessage=message;
	}

	protected void prepareUi(int n)
	{
		if(mSuffix==null)
		{
			mSuffix="";
		}
		if(SalaatFirstApplication.prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE).contains("ar"))
		{
			if(n==1)
			{
				mMinutes=reshapeSentence("دقيقة");
				mSuffixText.setText(" "+mMinutes+" "+mSuffix);
				mValueText.setText("");
			}
			else if(n==2)
			{
				mMinutes=reshapeSentence("دقيقتين");
				mSuffixText.setText(" "+mMinutes+" "+mSuffix);
				mValueText.setText("");
			}
			else if(n%100>=3&&n%100<=10)
			{
				mMinutes=reshapeSentence("دقائق");
				mSuffixText.setText(" "+mMinutes+" "+mSuffix);
				mValueText.setText(""+n);
			}
			else
			{
				mMinutes=reshapeSentence("دقيقة");
				mSuffixText.setText(" "+mMinutes+" "+mSuffix);
				mValueText.setText(""+n);
			}
		}
		else
		{
			if(n==1)
			{
				mMinutes="minute";
			}
			else
			{
				mMinutes="minutes";
			}
			mSuffixText.setText(" "+mMinutes+" "+mSuffix);
			mValueText.setText(""+n);
		}
	}
}
