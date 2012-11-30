/*
 * CategoryListAdapter.java
 *
 * Copyright 2012 Jonathan Hasenzahl, James Celona, Dhimitraq Jorgji
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.worcester.cs499summer2012.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.Task;

public class PriorityListAdapter extends ArrayAdapter<String> {
	
	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/
	
	private final Activity activity;
	private final String[] priorities;
	private final int textViewResourceId;
	
	/**************************************************************************
	 * Constructors                                                           *
	 **************************************************************************/
	
	/**
	 * Default constructor.
	 * @param
	 * @param
	 */
	public PriorityListAdapter(Activity activity, int textViewResourceId, String[] priorities) {
		super(activity, textViewResourceId, priorities);
		this.activity = activity;
		this.priorities = priorities;
		this.textViewResourceId = textViewResourceId;
		setNotifyOnChange(true);
	}
	
	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/
	
	public View getCustomView(int position, View convert_view, ViewGroup parent) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(textViewResourceId, null);

		TextView text = (TextView) view.findViewById(R.id.text_row_priority);
		text.setText(priorities[position]);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			text.setTextColor(Color.BLACK);
		
		ImageView icon = (ImageView) view.findViewById(R.id.image_row_priority);
		
		switch (position) {
		case Task.TRIVIAL:
			icon.setImageResource(R.drawable.ic_trivial);
			break;
		case Task.NORMAL:
			icon.setImageResource(R.drawable.ic_normal);
			break;
		case Task.URGENT:
			icon.setImageResource(R.drawable.ic_urgent);
			break;
		}
		
		return view;
	}
	
	
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
	
	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		return getCustomView(position, convert_view, parent);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
	return getCustomView(position, convertView, parent);
	}
}
