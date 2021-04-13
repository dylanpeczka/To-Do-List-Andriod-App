package com.example.listview_and_arrayadapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

class mainscreenadapter extends ArrayAdapter<myList> {
    //public constructor
    public mainscreenadapter(Activity context, ArrayList<myList> theList) {
        super(context, 0, theList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // check if convertView is null, if so, I need to create my own view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.mainscreenitem, parent, false);
        }
        myList currentNumber = getItem(position);
        // Update content of index TextView
        TextView numberTextView = listItemView.findViewById(R.id.numberTextView);
        numberTextView.setText(String.valueOf(position + 1));

        // Update content of item name
        TextView toDoEntryTextView = listItemView.findViewById(R.id.ToDoTextView);
        toDoEntryTextView.setText(currentNumber.getItemName());

        Button hideButton = listItemView.findViewById(R.id.hideButton_MS);
        final View finalListItemView = listItemView;
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View constraint_view = finalListItemView.findViewById(R.id.MS_constraint);
                constraint_view.setVisibility(View.INVISIBLE);
            }
        });

        //allows the buttons to disappear when the view is recycled
        View constraint_view = listItemView.findViewById(R.id.MS_constraint);
        constraint_view.setVisibility(currentNumber.getButtonVisibility());
        return listItemView;
    }

}


