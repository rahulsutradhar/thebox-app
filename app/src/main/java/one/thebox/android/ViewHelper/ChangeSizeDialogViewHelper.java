package one.thebox.android.ViewHelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.R;
import one.thebox.android.adapter.SizeAndFrequencyAdapter;

/**
 * Created by Ajeet Kumar Meena on 24-04-2016.
 */
public class ChangeSizeDialogViewHelper {

    private Context mContext;
    private OnSizeAndFrequencySelected onSizeAndFrequencySelected;
    private String frequencySelected;
    private SizeAndFrequencyAdapter sizeAndFrequencyAdapter;

    public ChangeSizeDialogViewHelper(Context mContext, OnSizeAndFrequencySelected onSizeAndFrequencySelected) {
        this.mContext = mContext;
        this.onSizeAndFrequencySelected = onSizeAndFrequencySelected;

    }

    public void show(final BoxItem boxItem) {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title("Select Size and Frequency").positiveText("Select")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onSizeAndFrequencySelected.onSizeAndFrequencySelected(frequencySelected, sizeAndFrequencyAdapter.getPriceAndSizes().get(sizeAndFrequencyAdapter.getCurrentItemSelected()));
                    }
                })
                .customView(R.layout.layout_change_size_and_frequency, false)
                .build();
        View customView = dialog.getCustomView();

        final RecyclerView recyclerView = (RecyclerView) customView.findViewById(R.id.recycler_view);
        sizeAndFrequencyAdapter = new SizeAndFrequencyAdapter(mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(sizeAndFrequencyAdapter);
        final HashMap<String, ArrayList<BoxItem.PriceAndSize>> hashMap = boxItem.getPriceAndSizeHashMap();
        Set<String> keySet = hashMap.keySet();
        int radioGroupSize = hashMap.size();
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    frequencySelected = buttonView.getText().toString();
                    sizeAndFrequencyAdapter.setPriceAndSizes(hashMap.get(frequencySelected));
                    ArrayList<BoxItem.PriceAndSize> priceAndSizes = new ArrayList<>();
                    priceAndSizes = hashMap.get(frequencySelected);
                    int adapterCurrentItemPosition = 0;
                    for(int i=0; i<priceAndSizes.size(); i++) {
                        if(priceAndSizes.get(i) == boxItem.getSelectedPriceAndSize()) {
                            adapterCurrentItemPosition = i;
                        }
                    }
                    sizeAndFrequencyAdapter.setCurrentItemSelected(adapterCurrentItemPosition);
                    recyclerView.setAdapter(sizeAndFrequencyAdapter);
                }

            }
        };
        final RadioGroup radioGroup = (RadioGroup) customView.findViewById(R.id.radio_group);
        for (int row = 0; row < 1; row++) {
            RadioGroup ll = new RadioGroup(mContext);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            int i = 1;
            for (String key : keySet) {
                RadioButton rdbtn = new RadioButton(mContext);
                rdbtn.setId((row * 2) + i);
                rdbtn.setText(key);
                rdbtn.setTextColor(mContext.getResources().getColor(R.color.dim_gray));
                rdbtn.setOnCheckedChangeListener(onCheckedChangeListener);
                if (boxItem.getSelectedFrequency().equals(key)) {
                    rdbtn.setChecked(true);
                }

                ll.addView(rdbtn);
                i++;
            }
            radioGroup.addView(ll);
        }
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
        dialog.show();

    }

    public interface OnSizeAndFrequencySelected {
        void onSizeAndFrequencySelected(String frequency, BoxItem.PriceAndSize priceAndSize);
    }


}