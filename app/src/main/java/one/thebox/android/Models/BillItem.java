package one.thebox.android.Models;

import java.util.ArrayList;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class BillItem {

    private ArrayList<SubBillItem> subBillItems;

    public BillItem(ArrayList<SubBillItem> subBillItems) {
        this.subBillItems = subBillItems;
    }

    public ArrayList<SubBillItem> getSubBillItems() {
        return subBillItems;
    }

    public void setSubBillItems(ArrayList<SubBillItem> subBillItems) {
        this.subBillItems = subBillItems;
    }

    public static class SubBillItem {
        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

}
