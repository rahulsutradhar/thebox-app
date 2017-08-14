package one.thebox.android.Events;

/**
 * Created by developers on 14/08/17.
 */

public class FABVisibilityOrderFactorEvent {

    private boolean orderAvailable;

    private int currentYear;
    private int currentMonth;

    public FABVisibilityOrderFactorEvent(boolean orderAvailable) {
        this.orderAvailable = orderAvailable;
    }

    public boolean isOrderAvailable() {
        return orderAvailable;
    }

    public void setOrderAvailable(boolean orderAvailable) {
        this.orderAvailable = orderAvailable;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }
}
