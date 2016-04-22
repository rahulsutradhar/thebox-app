package one.thebox.android.Models;

import java.util.ArrayList;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class Box {

    private ArrayList<SmartItem> smartItems;
    private ArrayList<BoxItem> boxItems;
    private boolean isExpandedListVisible;

    public Box(ArrayList<SmartItem> smartItems, ArrayList<BoxItem> boxItems) {
        this.smartItems = smartItems;
        this.boxItems = boxItems;
    }

    public ArrayList<SmartItem> getSmartItems() {
        return smartItems;
    }

    public void setSmartItems(ArrayList<SmartItem> smartItems) {
        this.smartItems = smartItems;
    }

    public static class SmartItem{

    }

    public static class BoxItem {
        int size;
        int frequency;
        int noOfItemsSelected;
        int id;
        String title;
        String brand;


        public int getNoOfItemsSelected() {
            return noOfItemsSelected;
        }

        public void setNoOfItemsSelected(int noOfItemsSelected) {
            this.noOfItemsSelected = noOfItemsSelected;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public boolean isExpandedListVisible() {
        return isExpandedListVisible;
    }

    public void setExpandedListVisible(boolean expandedListVisible) {
        isExpandedListVisible = expandedListVisible;
    }

    public ArrayList<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(ArrayList<BoxItem> boxItems) {
        this.boxItems = boxItems;
    }
}
