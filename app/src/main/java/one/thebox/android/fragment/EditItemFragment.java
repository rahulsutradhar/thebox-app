package one.thebox.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import one.thebox.android.R;

/**
 * Created by vaibhav on 24/08/16.
 */

public class EditItemFragment extends BottomSheetDialogFragment {
    public static final String TAG = "Edit_User_Item";
    private LinearLayout layoutReschedule, layoutChangeSize, layoutCancelSubscription;
    private View rootView;
    private OnEditItemoptionSelected onEditItemoptionSelected;

    public static EditItemFragment newInstance() {
        EditItemFragment fragment = new EditItemFragment();
        return fragment;
    }

    public void attachListener(OnEditItemoptionSelected onEditItemoptionSelected) {
        this.onEditItemoptionSelected = onEditItemoptionSelected;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_edit_item_options, container, false);
        initVariables();
        initViews();
        setupViews();
        return rootView;
    }

    private void setupViews() {
        layoutReschedule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onEditItemoptionSelected.onEditItemoptionSelected(2);
            }
        });
        layoutChangeSize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onEditItemoptionSelected.onEditItemoptionSelected(1);
            }
        });

        layoutCancelSubscription.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onEditItemoptionSelected.onEditItemoptionSelected(3);
            }
        });
    }

    private void initViews() {
        layoutReschedule = (LinearLayout) rootView.findViewById(R.id.item_reschedule);
        layoutChangeSize = (LinearLayout) rootView.findViewById(R.id.item_change_size);
        layoutCancelSubscription = (LinearLayout) rootView.findViewById(R.id.item_cancel_subscription);
    }

    private void initVariables() {

    }

    public interface OnEditItemoptionSelected {
        // true if change_size was clicked
        // false otherwise
        void onEditItemoptionSelected(
                int actionUserItemSubscription);
    }


}

