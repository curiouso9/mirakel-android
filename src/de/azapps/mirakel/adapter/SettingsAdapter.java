/*******************************************************************************
 * Mirakel is an Android App for managing your ToDo-Lists
 * 
 * Copyright (c) 2013 Anatolij Zelenin, Georg Semmler.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.azapps.mirakel.adapter;

import java.util.List;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity.Header;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import de.azapps.mirakel.Mirakel;
import de.azapps.mirakel.helper.Log;
import de.azapps.mirakel.helper.PreferencesHelper;
import de.azapps.mirakelandroid.R;

public class SettingsAdapter extends ArrayAdapter<Header> {

	private static final String TAG = "SettingsAdapter";
	private LayoutInflater mInflater;
	private Context ctx;

	public SettingsAdapter(Context context, List<Header> objects) {
		super(context, 0, objects);
		ctx = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		Header header = getItem(position);
		View view = null;
		if (header.id == R.id.sync_header) {
			view = mInflater
					.inflate(R.layout.preferences_switch, parent, false);

			((ImageView) view.findViewById(android.R.id.icon))
					.setImageResource(header.iconRes);
			((TextView) view.findViewById(android.R.id.title)).setText(header
					.getTitle(getContext().getResources()));
			((TextView) view.findViewById(android.R.id.summary)).setText(header
					.getSummary(getContext().getResources()));
			final Switch s = ((Switch) view.findViewById(R.id.switchWidget));
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			Log.d(TAG,
					"account: "
							+ AccountManager.get(ctx).getAccountsByType(
									Mirakel.ACCOUNT_TYPE).length);
			Editor e = settings.edit();
			e.putBoolean(
					"syncUse",
					AccountManager.get(ctx).getAccountsByType(
							Mirakel.ACCOUNT_TYPE).length > 0);
			e.commit();
			s.setChecked(settings.getBoolean("syncUse", false));
			s.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					PreferencesHelper.createAuthActivity(isChecked,
							(Activity) ctx, s, false);
				}
			});
		} else {
			view = mInflater.inflate(R.layout.preferences_header_item, parent,
					false);
			((ImageView) view.findViewById(android.R.id.icon))
					.setImageResource(header.iconRes);
			((TextView) view.findViewById(android.R.id.title)).setText(header
					.getTitle(getContext().getResources()));
			((TextView) view.findViewById(android.R.id.summary)).setText(header
					.getSummary(getContext().getResources()));
		}
		return view;
	}

}
