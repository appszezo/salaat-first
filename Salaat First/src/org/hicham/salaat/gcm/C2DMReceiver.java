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

package org.hicham.salaat.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class C2DMReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		String str = arg1.getStringExtra("registration_id");

	}

}
