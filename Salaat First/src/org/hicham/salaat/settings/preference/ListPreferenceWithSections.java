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

package org.hicham.salaat.settings.preference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hicham.salaat.R;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog.Builder;
import org.holoeverywhere.preference.DialogPreference;
import org.holoeverywhere.util.CharSequences;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.CheckedTextView;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter.FilterListener;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;

public class ListPreferenceWithSections extends DialogPreference implements
		OnQueryTextListener {
	private static final class CharacterWrapper {
		private char c;

		private CharacterWrapper(char c) {
			this.c = c;
		}

		@Override
		public String toString() {
			return Character.toString(c);
		}
	}

	private class CustomAdapter extends ArrayAdapter<CharSequence> implements
			SectionIndexer {
		private final Character[] mAlphabet;
		private final CharSequence[] mData;

		public CustomAdapter(CharSequence[] data) {
			super(ListPreferenceWithSections.this.getContext(),
					R.layout.select_dialog_singlechoice_holo,
					android.R.id.text1);
			Arrays.sort(data, CHAR_SEQUENCE_COMPARATOR);
			addAll(mData = data);
			List<Character> alphabet = new ArrayList<Character>();
			for (CharSequence s : data) {
				if (s.length() == 0) {
					continue;
				}
				char c = s.charAt(0);
				if (!alphabet.contains(c)) {
					alphabet.add(c);
				}
			}
			Collections.sort(alphabet, CHARACTER_COMPARATOR);
			mAlphabet = alphabet.toArray(new Character[alphabet.size()]);
		}

		public int getPositionForSection(int section) {
			char alphabetChar;
			try /*fix bug reported on google play*/
			{
				alphabetChar = mAlphabet[section];
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				alphabetChar = mAlphabet[mAlphabet.length-1];
			}
			for (int i = 0; i < mData.length; i++) {
				CharSequence s = mData[i];
				if (s.length() == 0) {
					continue;
				}
				if (s.charAt(0) == alphabetChar) {
					return i;
				}
			}
			return -1;
		}

		public int getSectionForPosition(int position) {
			CharSequence s = mData[position];
			if (s.length() == 0) {
				return -1;
			}
			char c = s.charAt(0);
			for (int i = 0; i < mAlphabet.length; i++) {
				if (mAlphabet[i] == c) {
					return i;
				}
			}
			return -1;
		}

		public CharacterWrapper[] getSections() {
			CharacterWrapper[] array = new CharacterWrapper[mAlphabet.length];
			for (int i = 0; i < mAlphabet.length; i++) {
				array[i] = new CharacterWrapper(mAlphabet[i]);
			}
			return array;
		}
	}
	private static class SavedState extends BaseSavedState {
		String value;

		public SavedState(Parcel source) {
			super(source);
			value = source.readString();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(value);
		}
	}
	private int mClickedDialogEntryIndex;
	private CharSequence[] mEntries;
	private CharSequence[] mEntryValues;

	private CharSequence mSummary;

	private String mValue;

	private static final Comparator<CharSequence> CHAR_SEQUENCE_COMPARATOR = new Comparator<CharSequence>() {

		public int compare(CharSequence lhs, CharSequence rhs) {
			return CharSequences.compareToIgnoreCase(lhs, rhs);
		}
	};

	private static final Comparator<Character> CHARACTER_COMPARATOR = new Comparator<Character>() {

		public int compare(Character lhs, Character rhs) {
			return lhs.compareTo(rhs);
		}
	};

	public ListPreferenceWithSections(Context context) {
		this(context, null);
	}

	public ListPreferenceWithSections(Context context, AttributeSet attrs) {
		super(context, attrs);
		context = getContext();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ListPreference, 0, 0);
		mEntries = a.getTextArray(R.styleable.ListPreference_android_entries);
		mEntryValues = a
				.getTextArray(R.styleable.ListPreference_android_entryValues);
		a.recycle();
		mSummary = super.getSummary();
	}

	public int findIndexOfValue(String value) {
		if (value != null && mEntryValues != null) {
			for (int i = mEntryValues.length - 1; i >= 0; i--) {
				if (mEntryValues[i].equals(value)) {
					return i;
				}
			}
		}
		return -1;
	}

	public CharSequence[] getEntries() {
		return mEntries;
	}

	public CharSequence getEntry() {
		int index = getValueIndex();
		return index >= 0 && mEntries != null ? mEntries[index] : null;
	}

	public CharSequence[] getEntryValues() {
		return mEntryValues;
	}

	@Override
	public CharSequence getSummary() {
		final CharSequence entry = getEntry();
		if (mSummary == null || entry == null) {
			return super.getSummary();
		} else {
			return String.format(mSummary.toString(), entry);
		}
	}

	public String getValue() {
		return mValue;
	}

	private int getValueIndex() {
		return findIndexOfValue(mValue);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult && mClickedDialogEntryIndex >= 0
				&& mEntryValues != null) {
			String value = mEntryValues[mClickedDialogEntryIndex].toString();
			if (callChangeListener(value)) {
				setValue(value);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		if (mEntries == null || mEntryValues == null) {
			throw new IllegalStateException(
					"ListPreference requires an entries array and an entryValues array.");
		}
		mClickedDialogEntryIndex = getValueIndex();
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		SearchView searchView = new SearchView(getContext());
		searchView.setSubmitButtonEnabled(false);
		final ListView listView = (ListView) LayoutInflater.inflate(
				getContext(), R.layout.select_dialog_holo);
		final CustomAdapter adapter = new CustomAdapter(mEntries);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		listView.setItemChecked(mClickedDialogEntryIndex, true);
		listView.setSelection(mClickedDialogEntryIndex);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(android.widget.AdapterView<?> view,
					View item, int position, long id) {

				mClickedDialogEntryIndex = Arrays.binarySearch(mEntries,
						((CheckedTextView) item).getText());
				ListPreferenceWithSections.this.onClick(getDialog(),
						DialogInterface.BUTTON_POSITIVE);
				getDialog().dismiss();
			}
		});
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			public boolean onQueryTextChange(String newText) {
				adapter.getFilter().filter(newText, new FilterListener() {

					public void onFilterComplete(int count) {
						int position = adapter.getPosition(getValue());
						try {
							listView.setItemChecked(position, true);
						} catch (Exception e) {
							// something went wrong, continue
						}
					}
				});
				return true;
			}

			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		layout.addView(searchView);
		layout.addView(listView);
		builder.setView(layout);
		builder.setPositiveButton(null, null);
	}

	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setValue(myState.value);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}
		final SavedState myState = new SavedState(superState);
		myState.value = getValue();
		return myState;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setValue(restoreValue ? getPersistedString(mValue)
				: (String) defaultValue);
	}

	public void setEntries(CharSequence[] entries) {
		mEntries = entries;
	}

	public void setEntries(int entriesResId) {
		setEntries(getContext().getResources().getTextArray(entriesResId));
	}

	public void setEntryValues(CharSequence[] entryValues) {
		mEntryValues = entryValues;
	}

	public void setEntryValues(int entryValuesResId) {
		setEntryValues(getContext().getResources().getTextArray(
				entryValuesResId));
	}

	@Override
	public void setSummary(CharSequence summary) {
		super.setSummary(summary);
		if (summary == null && mSummary != null) {
			mSummary = null;
		} else if (summary != null && !summary.equals(mSummary)) {
			mSummary = summary;
		}
	}

	public void setValue(String value) {
		mValue = value;
		persistString(value);
	}

	public void setValueIndex(int index) {
		if (mEntryValues != null) {
			setValue(mEntryValues[index].toString());
		}
	}

}
