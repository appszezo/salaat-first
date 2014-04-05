package org.hicham.salaat.ui.fragments;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.db.SimpleCursorLoader;
import org.holoeverywhere.app.ListFragment;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class HadithSearchResultsFragment extends ListFragment implements
LoaderManager.LoaderCallbacks<Cursor> {
	// TutListFragment class member variables
	private static final int AHADITH_LIST_LOADER = 0x01;

	public static final String QUERY_KEY = "ahadith_key";
	private String queryText;
	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    if(getArguments()!=null&&getArguments().containsKey(QUERY_KEY))
	    {
	    	queryText=getArguments().getString(QUERY_KEY);
	    }
		String[] uiBindFrom = { "hadith" };
	    int[] uiBindTo = { android.R.id.text1 };
	 
	    getLoaderManager().initLoader(AHADITH_LIST_LOADER, null, this);
	 
	    adapter = new SimpleCursorAdapter(
	            getActivity().getApplicationContext(), R.layout.hadith_results_list_item,
	            null, uiBindFrom, uiBindTo,
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	    setListAdapter(adapter);
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setBackgroundColor(getResources().getColor(R.color.background_holo_dark));
		getListView().setDivider(getResources().getDrawable(R.drawable.list_divider_holo_dark));
	}
	
	@Override
	public void onStart() {
		super.onStart();
	    getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long id) {
				HadithFragment fragment=new HadithFragment();
				Bundle args=new Bundle();
				args.putInt(HadithFragment.HADITH_ID, (int)id);
				fragment.setArguments(args);
				
				FragmentManager fm=getActivity().getSupportFragmentManager();
				FragmentTransaction ft=fm.beginTransaction();
		        ft.replace(R.id.contentView, fragment, null);
		        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	            ft.commit();
			}
		});
	}
	
	public static final class AhadithCursorLoader extends SimpleCursorLoader {

		private String query;
		
		public AhadithCursorLoader(Context context, String query) {
			super(context);
			this.query=query;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor = null;
			try {
				cursor=SalaatFirstApplication.dBAdapter.getAhadith(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return cursor;
		}

	}

	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new AhadithCursorLoader(getActivity(), queryText);
	}

	
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
