package one.thebox.android.fragment.order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

import one.thebox.android.Models.order.CalenderYear;
import one.thebox.android.R;
import one.thebox.android.adapter.orders.SelectYearAdapter;

/**
 * Created by developers on 12/07/17.
 */

public class SelectYearBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final int RECYCLER_VIEW_TYPE_NORMAL = 300;
    public static final String TAG = "Select Year Fragment";
    private OnYearSelected onYearSelected;
    private Vector<CalenderYear> calenderYears;
    private View rootView;
    private RecyclerView recyclerView;
    private SelectYearAdapter adapter;
    private String selectedYear;
    private int scrollPosition = 0;

    public SelectYearBottomSheetDialogFragment() {

    }

    @SuppressLint("ValidFragment")
    public SelectYearBottomSheetDialogFragment(Vector<CalenderYear> calenderYears, String selectedYear) {
        this.calenderYears = calenderYears;
        this.selectedYear = selectedYear;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sheet_select_year, container, false);
        initView();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecycelerView();
    }

    public void initView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_year);
        try {
            for (int i = 0; i < calenderYears.size(); i++) {
                if (calenderYears.get(i).getName().equalsIgnoreCase(selectedYear.toString().trim())) {
                    scrollPosition = i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void attachListener(OnYearSelected onYearSelected) {
        this.onYearSelected = onYearSelected;
    }

    public void setupRecycelerView() {
        adapter = new SelectYearAdapter(getActivity(), calenderYears, selectedYear);
        final LinearLayoutManager manager = new
                LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
        adapter.attachListener(new SelectYearAdapter.OnYearSelected() {
            @Override
            public void onYearSelected(CalenderYear calenderYear) {
                //pass data to fragment listener
                onYearSelected.onYearSelected(calenderYear);
            }
        });
        recyclerView.scrollToPosition(scrollPosition);

    }


    /**
     * Interface
     */
    public interface OnYearSelected {
        void onYearSelected(CalenderYear calenderYear);
    }

}
