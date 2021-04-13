package com.example.listview_and_arrayadapter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class listItemActivity extends AppCompatActivity {

    private ArrayList<myList> theList;//list of items within a list
    private myToDoListAdapter itemAdapter;//adapter
    private int mostRecentClick; //the last item clicked
    private String fileName;//the name of the file containing the items in a list
    private String listName;//name of the list
    private int clickCounter = 0;//counts the number of clicks
    private int previousClick;//the index of the last item clicked in theList

    /* onCreate gets the intent and sets fileName to the name of the file passed to it, a new ArrayList
    * of myList is created and set to the List, an item adapter is created, readFile is called, and an
    * onItemClickListener is added to the items in the list*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitem);
        Intent callerIntent = getIntent();
        fileName = callerIntent.getStringExtra("fileName");
        listName = callerIntent.getStringExtra("listName");
        theList = new ArrayList<myList>();
        itemAdapter = new myToDoListAdapter(this, theList);
        readFile();
        TextView header = findViewById(R.id.textView);
       if(theList.size()==0){
            header.setText(listName.concat(" is currently empty"));
        }
       else{
           header.setText(listName.concat(":"));
       }
        ListView activityList = findViewById(R.id.activityList);
        activityList.setAdapter(itemAdapter);
        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                previousClick = mostRecentClick;
                if(clickCounter != 0){
                    theList.get(previousClick).setButtonVisibility(View.INVISIBLE);
                    itemAdapter.notifyDataSetChanged();
                }
                theList.get(i).setButtonVisibility(View.VISIBLE);
                itemAdapter.notifyDataSetChanged();
                clickCounter++;
                mostRecentClick = i;
            }
        });
    }

    /* removeButton removes the selected item is removed from the list of items, the file is updated,
    * and updates the header*/
    public void removeButton(View view){
        theList.remove(mostRecentClick);
        writeToFile();
        TextView header = findViewById(R.id.textView);
        if(theList.size()==0){
            header.setText(listName.concat(" is currently empty"));
        }
        else{
            header.setText(listName.concat(":"));
        }
        itemAdapter.notifyDataSetChanged();
    }

    /* addButton produces a toast if an empty string is entered. Else the item is added to the ArrayList
    * of current items in the list, the list item is added to the file, the header is corrected, the
    * edit text used to enter the item is set to an empty string, and the button and edit text used to
    * enter a list have their visibility set back to gone */
    public void addButton(View view) {
        EditText entryEditText = findViewById(R.id.entryEditText);
        String entry = entryEditText.getText().toString();
        if (entry.equals("")) {
            Toast toast = Toast.makeText(this, "Please add an entry", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
            toast.show();
        } else {
            theList.add(new myList(entry));
            writeToFile();
            TextView header = findViewById(R.id.textView);
            header.setText(listName.concat(":"));
            itemAdapter.notifyDataSetChanged();
            entryEditText.setText("");
            Button addButton = findViewById(R.id.addButton);
            entryEditText.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
        }
    }

    /* AddEntryButton sets the visibility to visible for the button and editText to enter an item*/
    public void AddEntryButton(View view) {
        EditText entryEditText = findViewById(R.id.entryEditText);
        Button addButton = findViewById(R.id.addButton);
        entryEditText.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);
    }

    /* writeToFile writes to the file to update it with the information from the current list*/
    public void writeToFile() {
        try {
            FileOutputStream fis = this.openFileOutput(fileName, Context.MODE_PRIVATE);
            for (int i = 0; i < theList.size(); i++) {
                String toWrite = theList.get(i).getItemName().concat("\n");//change to string builder
                fis.write(toWrite.getBytes());
            }
            fis.close();
        } catch (Exception e) {
            Toast.makeText(this, "problem with file", Toast.LENGTH_SHORT).show();
        }
    }

    /* creates and/or reads the file line by line and adds it the list containing items*/
    public void readFile() {
        File file = getBaseContext().getFileStreamPath(fileName);
        if (file.exists()) {
            try {
                FileInputStream fis = this.openFileInput(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bf = new BufferedReader(inputStreamReader);
                String line = bf.readLine();
                while (line != null) {
                    theList.add(new myList(line));
                    line = bf.readLine();
                }
            } catch (Exception e) {
                Toast.makeText(this, "We should not get here", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* If a valid position is entered, switches the position of two items in the list using the swap
    * function, updates the file and sets the edit text to an empty string. Else if the position is invalid
    * a toast appears on screen.*/
    public void changePosition(View view){
        try{EditText posET = findViewById(R.id.move_pos_ET);
            String pos_Str = posET.getText().toString();
            int pos = Integer.parseInt(pos_Str);
            pos = pos-1;
            swap(theList,pos,mostRecentClick);
            writeToFile();
            itemAdapter.notifyDataSetChanged();
            posET.setText("");
            View constraint_view = findViewById(R.id.button_constraint);
            constraint_view.setVisibility(View.INVISIBLE);}
        catch(Exception e){
            Toast.makeText(this, "The input was invalid", Toast.LENGTH_SHORT).show();
        }

    }
    /* swaps two items in a list*/
    public static void swap(List<?> list, int j, int i ){
        Collections.swap(list,j,i);
    }

}