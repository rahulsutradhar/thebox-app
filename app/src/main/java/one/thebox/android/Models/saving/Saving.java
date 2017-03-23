package one.thebox.android.Models.saving;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by developers on 22/03/17.
 */

public class Saving extends RealmObject implements Serializable {

    private String type;

    private String month;

    private boolean saving;

    @SerializedName("monthly_bill")
    private MonthlyBill monthlyBill;

    @SerializedName("monthly_saving")
    private MonthlySaving monthlySaving;

    @SerializedName("total_items")
    private TotalItem totalItem;

    public Saving() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public boolean isSaving() {
        return saving;
    }

    public void setSaving(boolean saving) {
        this.saving = saving;
    }

    public MonthlyBill getMonthlyBill() {
        return monthlyBill;
    }

    public void setMonthlyBill(MonthlyBill monthlyBill) {
        this.monthlyBill = monthlyBill;
    }

    public MonthlySaving getMonthlySaving() {
        return monthlySaving;
    }

    public void setMonthlySaving(MonthlySaving monthlySaving) {
        this.monthlySaving = monthlySaving;
    }

    public TotalItem getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(TotalItem totalItem) {
        this.totalItem = totalItem;
    }
}
