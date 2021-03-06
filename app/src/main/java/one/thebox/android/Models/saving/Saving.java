package one.thebox.android.Models.saving;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 22/03/17.
 */

public class Saving implements Serializable {

    private String type;

    private String month;

    private boolean saving;

    @SerializedName("suggestion_box_id")
    private int suggestionBoxId;

    @SerializedName("suggestion_box_uuid")
    private String suggestionBoxUuid;

    @SerializedName("suggestion_box_name")
    private String suggestionBoxName;

    @SerializedName("suggestion_box_title")
    private String suggestionBoxTitle;

    @SerializedName("suggestion_box_description")
    private String suggestionBoxDescription;

    @SerializedName("monthly_bill")
    private MonthlyBill monthlyBill;

    @SerializedName("monthly_saving")
    private MonthlySaving monthlySaving;

    @SerializedName("total_items")
    private TotalItem totalItem;

    public Saving() {

    }

    /************************************
     * Getter Setter
     ************************************/

    public String getSuggestionBoxUuid() {
        return suggestionBoxUuid;
    }

    public void setSuggestionBoxUuid(String suggestionBoxUuid) {
        this.suggestionBoxUuid = suggestionBoxUuid;
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

    public String getSuggestionBoxTitle() {
        return suggestionBoxTitle;
    }

    public void setSuggestionBoxTitle(String suggestionBoxTitle) {
        this.suggestionBoxTitle = suggestionBoxTitle;
    }

    public String getSuggestionBoxDescription() {
        return suggestionBoxDescription;
    }

    public void setSuggestionBoxDescription(String suggestionBoxDescription) {
        this.suggestionBoxDescription = suggestionBoxDescription;
    }

    public int getSuggestionBoxId() {
        return suggestionBoxId;
    }

    public void setSuggestionBoxId(int suggestionBoxId) {
        this.suggestionBoxId = suggestionBoxId;
    }

    public String getSuggestionBoxName() {
        return suggestionBoxName;
    }

    public void setSuggestionBoxName(String suggestionBoxName) {
        this.suggestionBoxName = suggestionBoxName;
    }
}
