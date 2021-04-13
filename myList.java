package com.example.listview_and_arrayadapter;

import android.view.View;

public class myList {
    private String itemName;//name of the item
    private int buttonVisibility;

    public myList(String lN) {
        this.itemName = lN;
        this.buttonVisibility = View.GONE;
    }

    public String getItemName() { return this.itemName; }

    public int getButtonVisibility() {
        return buttonVisibility;
    }

    public void setButtonVisibility(int buttonVisibility) {
        this.buttonVisibility = buttonVisibility;
    }
}
