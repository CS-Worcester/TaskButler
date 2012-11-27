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

import java.util.ArrayList;

import edu.worcester.cs499summer2012.R;
import edu.worcester.cs499summer2012.task.Category;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends ArrayAdapter<Category> {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/
	
	static class ViewHolder {
		public View color;
		public TextView name;
	}
	
	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/
	
	private final Activity activity;
	private final ArrayList<Category> categories;
	
	/**************************************************************************
	 * Constructors                                                           *
	 **************************************************************************/
	
	/**
	 * Default constructor.
	 * @param
	 * @param
	 */
	public CategoryListAdapter(Activity activity, ArrayList<Category> categories) {
		super(activity, R.layout.row_comparator, categories);
		this.activity = activity;
		this.categories = categories;
		this.categories.remove(0);
		setNotifyOnChange(true);
	}
	
	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/
	
	/**
	 * This method is called automatically when the user scrolls the ListView.
	 * Updates the View of a single visible row, reflecting the list being 
	 * scrolled by the user.
	 * @param position the index of the TaskList
	 * @param convert_view the View to be updated
	 * @param parent the parent ViewGroup of convert_view
	 * @return the updated View
	 */
	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		Category category = categories.get(position);
		
		if (view == null) {		
			LayoutInflater inflater = activity.getLayoutInflater();
			view = inflater.inflate(R.layout.row_category, null);
			
			final ViewHolder view_holder = new ViewHolder();
			view_holder.color = (View) view.findViewById(R.id.view_row_category_color);
			view_holder.name = (TextView) view.findViewById(R.id.text_row_category_name);
			
			view.setTag(view_holder);
			view_holder.name.setTag(category);
		} else
			((ViewHolder) view.getTag()).name.setTag(category); 

		ViewHolder holder = (ViewHolder) view.getTag();
		
		// Set color
		holder.color.setBackgroundColor(category.getColor());
		
		// Set name
		holder.name.setText(category.getName());
		
		return view;
	}
}
