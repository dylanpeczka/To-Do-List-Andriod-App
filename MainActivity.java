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

public class MainActivity extends AppCompatActivity {
    private ArrayList<myList> mainScreenList; //list of list
    private ArrayList<myList> fileNameList; //list of file names
    private mainscreenadapter listItemAdapter; //item adapter
    private int mostRecentClick; //the item last clicked on
    private int counter = 0; //used when a new file is created to create unique file names
    private int clickCounter = 0; //counts how many clicks have happened
    private int previousClick; //stores the index of the previous item clicked
    private MainActivity context = this;

    /* The onCreate function creates new list for file name and list, starts the adapter, determines
     * the header, and has an onItemClickListener for the items*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainScreenList = new ArrayList<myList>();
        fileNameList = new ArrayList<myList>();
        listItemAdapter = new mainscreenadapter(this, mainScreenList);
        readFile("mainScreenList.txt", mainScreenList);
        readFile("fileName.txt", fileNameList);
        if (mainScreenList.size() == 0) {
            TextView emptyList_layout = findViewById(R.id.textView22);
            emptyList_layout.setText("No list");
        }
        ListView mainScreenActivity = findViewById(R.id.mainScreenActivity);
        mainScreenActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                previousClick = mostRecentClick;
                if (clickCounter != 0 && mainScreenList.size() > 1) {
                    mainScreenList.get(previousClick).setButtonVisibility(View.INVISIBLE);
                    listItemAdapter.notifyDataSetChanged();
                    clickCounter++;
                }
                mainScreenList.get(i).setButtonVisibility(View.VISIBLE);
                listItemAdapter.notifyDataSetChanged();
                clickCounter++;
                mostRecentClick = i;
            }
        });
        mainScreenActivity.setAdapter(listItemAdapter);
    }

    /* addButtons function makes the appropriate button and edit text appear to add a new list*/
    public void addButtonMS(View view) {
        EditText listEditText = findViewById(R.id.listItemEditText);
        Button addButton = findViewById(R.id.addButton);
        listEditText.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);
    }

    /* The removesListButton function the selected item from the list of list and the list of file
     * names, corrects the files, deletes the file associated with the list, and corrects the header*/
    public void removeListButton(View view) {
        mainScreenList.remove(mostRecentClick);
        fileNameList.remove(mostRecentClick);
        String fileName = fileNameList.get(mostRecentClick).getItemName();
        deleteFile(fileName);
        writeToFile("mainScreenList.txt", mainScreenList);
        writeToFile("fileName.txt", fileNameList);
        if (mainScreenList.size() == 0) {
            TextView emptyList_layout = findViewById(R.id.textView22);
            emptyList_layout.setText("No list");
        }
        listItemAdapter.notifyDataSetChanged();
    }

    /* The addListButton function checks to see if a list name has been entered, if it has then the list
     * name is added to the list of list, createFile is called, the header is changed, the edit text is
     * set back to empty, and the editText and button to add an entry visibility is set back to gone. If
     * a list that already exist is entered a toast is produced*/
    public void addListButton(View view) {
        EditText listEditText = findViewById(R.id.listItemEditText);
        String list = listEditText.getText().toString();
        if (checkRepeatList(list) == false) {
            Toast toast = Toast.makeText(this, "Please add a list", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
            toast.show();
            listEditText.setText("");
        } else {
            mainScreenList.add(new myList(list));
            createFile(list);
            writeToFile("mainScreenList.txt", mainScreenList);
            writeToFile("fileName.txt", fileNameList);
            TextView emptyList_layout = findViewById(R.id.textView22);
            emptyList_layout.setText("My Lists:");
            listItemAdapter.notifyDataSetChanged();
            listEditText.setText("");
            Button addButton = findViewById(R.id.addButton);
            listEditText.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
        }
    }

    /* writeToFile takes a string containing the file name and an ArrayList containing myList objects
     * and writes to the file */
    public void writeToFile(String fileName, ArrayList<myList> list) {
        try {
            FileOutputStream fis = this.openFileOutput(fileName, Context.MODE_PRIVATE);
            for (int i = 0; i < list.size(); i++) {
                String toWrite = list.get(i).getItemName().concat("\n");
                fis.write(toWrite.getBytes());
            }
            fis.close();
        } catch (Exception e) {
            Toast.makeText(this, "problem with file", Toast.LENGTH_SHORT).show();
        }
    }

    /* readFile takes a string containg the file name and an ArrayList containg myList objects. ReadFile
     * then creates a file using the file name provided or opens and reads it line by line. Each line is
     * added to the ArrayList*/
    public void readFile(String fileName, ArrayList<myList> list) {
        File file = getBaseContext().getFileStreamPath(fileName);
        if (file.exists()) {
            try {
                FileInputStream fis = this.openFileInput(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bf = new BufferedReader(inputStreamReader);
                String line = bf.readLine();
                while (line != null) {
                    list.add(new myList(line));
                    line = bf.readLine();
                }
            } catch (Exception e) {
                Toast.makeText(this, "We should not get here", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* ChangeListPositions switches the position of one item on the list with another by calling the
     * swap function on both list, writing to both files, sets edit text where the position is entered
     * back to an empty string, and sets the constraint view visibility, which contains the buttons
     * back to gone. If an invalid position is entered a toast appears on screen*/
    public void changeListPosition(View view) {

        try {
            EditText posET = findViewById(R.id.MS_position);
            String pos_Str = posET.getText().toString();
            int pos = Integer.parseInt(pos_Str);
            pos--;
            swap(mainScreenList, pos, mostRecentClick);
            swap(fileNameList, pos, mostRecentClick);
            writeToFile("mainScreenList.txt", mainScreenList);
            writeToFile("fileName.txt", fileNameList);
            listItemAdapter.notifyDataSetChanged();
            posET.setText("");
            View constraint_view = findViewById(R.id.MS_constraint);
            constraint_view.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid Position", Toast.LENGTH_SHORT).show();
        }
    }

    /* Swaps two items in a list*/
    public static void swap(List<?> list, int j, int i) {
        Collections.swap(list, j, i);
    }

    /* createFile increases a counter which is then turned into a string. This string is added to the
     * name of the list and used to create a txt file. the name of the file is added to the file name
     * list*/
    public void createFile(String list) {
        counter++;
        String Str_position = Integer.toString(counter);
        String fileName = Str_position + list.concat(".txt");
        File file = getBaseContext().getFileStreamPath(fileName);
        fileNameList.add(new myList(fileName));
    }

    /* selectButton function gets the file name of the last clicked item from the file name list and
     * opens an intent to go to the list items. The name of the file is sent with the intent and the
     * activity is started */
    public void selectionButton(View view) {
        String fileName = fileNameList.get(mostRecentClick).getItemName();
        String listName = mainScreenList.get(mostRecentClick).getItemName();
        Intent toDoIntent = new Intent(this, listItemActivity.class);
        toDoIntent.putExtra("fileName", fileName);
        toDoIntent.putExtra("listName", listName);
        startActivity(toDoIntent);
    }
    /*checkRepeateList takes a string of the list name to be added and compares it to the list of list
    names. If it has already been added checkRepeateList returns false. Else it returns true.*/
    public boolean checkRepeatList(String list) {
        for (int i = 0; i < mainScreenList.size(); i++) {
            String compare = mainScreenList.get(i).getItemName();
            if ( list.equals(compare)) {
                return false;
            }
        }
        return true;
    }
}