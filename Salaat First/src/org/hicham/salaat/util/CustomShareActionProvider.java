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

package org.hicham.salaat.util;

import org.holoeverywhere.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.internal.widget.ActivityChooserModel;
import android.support.v7.internal.widget.ActivityChooserView;
import android.support.v7.widget.ShareActionProvider;
import android.util.TypedValue;
import android.view.View;

public class CustomShareActionProvider extends ShareActionProvider{

	private Context mContext;
	private String mShareHistoryFileName;
	
	public CustomShareActionProvider(Context context) {
		super(context);
		mShareHistoryFileName=super.DEFAULT_SHARE_HISTORY_FILE_NAME;
		mContext=context;
	}

	
    @Override
    public View onCreateActionView() {
        // Create the view and set its data model.
        ActivityChooserModel dataModel = ActivityChooserModel.get(mContext, mShareHistoryFileName);
        ActivityChooserView activityChooserView = new ActionShareActivityChooserView(mContext);
        activityChooserView.setActivityChooserModel(dataModel);

        // Lookup and set the expand action icon.
        TypedValue outTypedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.actionModeShareDrawable, outTypedValue, true);
        Drawable drawable = mContext.getResources().getDrawable(outTypedValue.resourceId);
        activityChooserView.setExpandActivityOverflowButtonDrawable(drawable);
        activityChooserView.setProvider(this);

        // Set content description.
        activityChooserView.setDefaultActionButtonContentDescription(
                R.string.abc_shareactionprovider_share_with_application);
        activityChooserView.setExpandActivityOverflowButtonContentDescription(
                R.string.abc_shareactionprovider_share_with);

        return activityChooserView;
    }
	
}
