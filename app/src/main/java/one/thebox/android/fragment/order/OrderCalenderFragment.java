package one.thebox.android.fragment.order;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Vector;

import one.thebox.android.Events.SetResultForActivityEvent;
import one.thebox.android.Models.order.CalenderMonth;
import one.thebox.android.Models.order.CalenderYear;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.activity.order.OrderCalenderActivity;
import one.thebox.android.adapter.orders.ViewPagerCalenderAdapter;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 10/07/17.
 */

public class OrderCalenderFragment extends Fragment {

    private Toolbar toolbar;
    private View rootView;
    private TextView selectedYearText, noInternet, noOrders;
    private int currentYear, currentMonth;
    private Vector<CalenderYear> calenderYears;
    private FrameLayout frameLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_calender, container, false);
        initView();
        initVariable();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchDataFromServer();
        setYear(String.valueOf(currentYear));

    }

    public void initVariable() {
        currentYear = getArguments().getInt(Constants.EXTRA_CALENDER_SELECTED_YEAR);
        currentMonth = getArguments().getInt(Constants.EXTRA_CALENDER_SELECTED_MONTH);
    }

    public void initView() {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Calender");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        frameLayout = (FrameLayout) rootView.findViewById(R.id.container_pager);

        selectedYearText = (TextView) rootView.findViewById(R.id.selected_year);

        //click event on year
        selectedYearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open bottom sheet and show year list
                openYearBottomSheet();
            }
        });
        noInternet = (TextView) rootView.findViewById(R.id.no_internet);
        noInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                fetchDataFromServer();
            }
        });

        noOrders = (TextView) rootView.findViewById(R.id.no_data_available);
    }

    /**
     * Open Year Selection Bottom Sheet
     */
    public void openYearBottomSheet() {

        final SelectYearBottomSheetDialogFragment fragmentSheet = new
                SelectYearBottomSheetDialogFragment(calenderYears, String.valueOf(currentYear));
        fragmentSheet.show(((AppCompatActivity) getActivity()).getSupportFragmentManager()
                , SelectYearBottomSheetDialogFragment.TAG);
        fragmentSheet.attachListener(new SelectYearBottomSheetDialogFragment.OnYearSelected() {
            @Override
            public void onYearSelected(CalenderYear calenderYear) {
                //TODO get the selected Year
                if (!calenderYear.getName().equalsIgnoreCase(String.valueOf(currentYear))) {
                    removeFragment();
                    setYear(calenderYear.getName());
                    currentYear = Integer.parseInt(calenderYear.getName());
                    //search Month for year
                    searchMonthForYear(calenderYear.getName());
                }
                fragmentSheet.dismiss();
            }
        });

    }

    /**
     * Fetch CalenderMonth from Server
     */
    public void fetchDataFromServer() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("month", currentMonth);
        params.put("year", currentYear);

        final BoxLoader dialog = new BoxLoader(getActivity()).show();
        TheBox.getAPIService()
                .getOrders(PrefUtils.getToken(getActivity()), params)
                .enqueue(new Callback<OrdersResponse>() {
                    @Override
                    public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                        try {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    if (response.body().getCalenderYears() != null) {
                                        calenderYears = response.body().getCalenderYears();
                                        searchMonthForYear(String.valueOf(currentYear));
                                    } else {
                                        noOrders.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersResponse> call, Throwable t) {
                        dialog.dismiss();
                        noInternet.setVisibility(View.VISIBLE);
                    }
                });
    }


    /**
     * Search For Month List with the coresponding year
     *
     * @param selectedYear
     */
    public void searchMonthForYear(String selectedYear) {
        boolean flag = false;
        if (calenderYears.size() > 0) {
            for (CalenderYear calenderYear : calenderYears) {
                if (calenderYear.getName().equalsIgnoreCase(selectedYear)) {
                    if (calenderYear.getCalenderMonths() != null) {
                        if (calenderYear.getCalenderMonths().size() > 0) {
                            flag = true;
                            transactToContainerFragment(calenderYear.getCalenderMonths());
                        }
                    }
                    break;
                }
            }

            if (!flag) {
                //month doesnot exist for this year
                noOrders.setVisibility(View.VISIBLE);
            } else {
                noOrders.setVisibility(View.GONE);
            }
        } else {
            noOrders.setVisibility(View.VISIBLE);
        }
    }

    public void setYear(String year) {
        selectedYearText.setText(year);
        selectedYearText.setPaintFlags(selectedYearText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    public void transactToContainerFragment(Vector<CalenderMonth> calenderMonths) {
        Fragment fragment = new CalenderContainerFragment(calenderMonths, currentMonth, currentYear);
        FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.container_pager, fragment, "container_order_calender_fragment").commit();
    }

    public void removeFragment() {
        Fragment fragment = (CalenderContainerFragment) getChildFragmentManager().findFragmentByTag("container_order_calender_fragment");
        if (fragment != null) {
            FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
            transaction.remove(fragment).commit();
        }
        frameLayout.removeAllViewsInLayout();

    }
}
