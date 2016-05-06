package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.activity.ConfirmPaymentDetailsActivity;
import one.thebox.android.adapter.OrdersItemAdapter;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyOrderFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView recyclerView;
    private OrdersItemAdapter ordersItemAdapter;
    private LinearLayout buttonAddressLinear;
    private TextView buttonSelectAndPay;
    private ArrayList<Order> orders = new ArrayList<>();
    private TextView emptyOrderText;
    private static final String EXTRA_ORDER_ARRAY = "extra_order_array";

    public MyOrderFragment() {
    }

    public static MyOrderFragment newInstance(ArrayList<Order> orders) {
        String orderString = CoreGsonUtils.toJson(orders);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ORDER_ARRAY, orderString);
        MyOrderFragment fragment = new MyOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private ArrayList<Order> gerOrders() {
        String orderString = getArguments().getString(EXTRA_ORDER_ARRAY);
        return CoreGsonUtils.fromJsontoArrayList(orderString, Order.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        initViews();
        orders = gerOrders();
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        if(orders == null || orders.isEmpty()) {
            emptyOrderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            buttonSelectAndPay.setVisibility(View.GONE);
        } else {
            emptyOrderText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            buttonSelectAndPay.setVisibility(View.VISIBLE);
            ordersItemAdapter = new OrdersItemAdapter(getActivity(), orders);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(ordersItemAdapter);
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        buttonAddressLinear = (LinearLayout) rootView.findViewById(R.id.button_address);
        buttonSelectAndPay = (TextView) rootView.findViewById(R.id.button_select_and_pay);
        emptyOrderText = (TextView) rootView.findViewById(R.id.empty_order_text_view);
        buttonAddressLinear.setOnClickListener(this);
        buttonSelectAndPay.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_address: {
                if (!ordersItemAdapter.isAnyItemSelected()) {
                    Toast.makeText(getActivity(), "Select At Least One Option", Toast.LENGTH_SHORT).show();
                    return;
                }
                openSelectAddressActivity();
                break;
            }
            case R.id.button_select_and_pay: {
                if (!ordersItemAdapter.isAnyItemSelected()) {
                    Toast.makeText(getActivity(), "Select At Least One Option", Toast.LENGTH_SHORT).show();
                    return;
                }
                openSelectAddressActivity();
                break;
            }
        }
    }

    private void openSelectAddressActivity() {
        startActivity(ConfirmAddressActivity.getInstance(getActivity(), ordersItemAdapter.getSelectedOrders()));
    }

    public void setButtonState(boolean isSelectAndPay) {
        if (isSelectAndPay) {
            buttonSelectAndPay.setVisibility(View.VISIBLE);
            buttonAddressLinear.setVisibility(View.GONE);
        } else {
            buttonSelectAndPay.setVisibility(View.GONE);
            buttonAddressLinear.setVisibility(View.VISIBLE);
        }
    }


}
