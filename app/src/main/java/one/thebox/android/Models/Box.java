package one.thebox.android.Models;

import java.util.ArrayList;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class Box {

    private ArrayList<SmartItem> smartItems;
    private ArrayList<ExpandedListItem> expandedListItems;
    private boolean isExpandedListVisible;

    public Box(ArrayList<SmartItem> smartItems, ArrayList<ExpandedListItem> expandedListItems) {
        this.smartItems = smartItems;
        this.expandedListItems = expandedListItems;
    }

    public ArrayList<SmartItem> getSmartItems() {
        return smartItems;
    }

    public void setSmartItems(ArrayList<SmartItem> smartItems) {
        this.smartItems = smartItems;
    }

    public static class SmartItem{

    }

    public static class ExpandedListItem {

    }

    public boolean isExpandedListVisible() {
        return isExpandedListVisible;
    }

    public void setExpandedListVisible(boolean expandedListVisible) {
        isExpandedListVisible = expandedListVisible;
    }

    public ArrayList<ExpandedListItem> getExpandedListItems() {
        return expandedListItems;
    }

    public void setExpandedListItems(ArrayList<ExpandedListItem> expandedListItems) {
        this.expandedListItems = expandedListItems;
    }
}
