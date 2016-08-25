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
import one.thebox.android.R;

/**
 * Created by vaibhav on 24/08/16.
 */

public class EditItemFragment extends BottomSheetDialogFragment {
    public static final String TAG = "Edit Item";
    private Button reschedule_delivery;
    private Button change_size;
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
        change_size.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onEditItemoptionSelected.onEditItemoptionSelected(true);
            }
        });
        reschedule_delivery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onEditItemoptionSelected.onEditItemoptionSelected(false);
            }
        });
    }

    private void initViews() {
        change_size = (Button) rootView.findViewById(R.id.change_size);
        reschedule_delivery = (Button) rootView.findViewById(R.id.reschedule_delivery);
    }

    private void initVariables() {

    }

    public interface OnEditItemoptionSelected {
        // true if change_size was clicked
        // false otherwise
        void onEditItemoptionSelected(
                Boolean change_size_or_reschedule);
    }
}

