package one.thebox.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.adapter.OrdersItemAdapter;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;


public class UpComingOrderFragment extends Fragment implements View.OnClickListener {

    private static final String EXTRA_ORDER_ARRAY = "extra_order_array";
    ArrayList<Integer> orderIds = new ArrayList<>();
    private View rootView;
    private RecyclerView recyclerView;
    private OrdersItemAdapter ordersItemAdapter;
    private TextView buttonSelectAndPay;
    private RealmList<Order> orders = new RealmList<>();
    private TextView emptyOrderText;

    public UpComingOrderFragment() {
    }

    public static UpComingOrderFragment newInstance(ArrayList<Order> orders) {
        UpComingOrderFragment fragment = new UpComingOrderFragment();
        Bundle args = new Bundle();
        ArrayList<Integer> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        args.putString(EXTRA_ORDER_ARRAY, CoreGsonUtils.toJson(orderIds));
        fragment.setArguments(args);
        return fragment;
    }

    private void initVariables() {
        orderIds = CoreGsonUtils.fromJsontoArrayList(
                getArguments().getString(EXTRA_ORDER_ARRAY), Integer.class);
        if (orderIds.isEmpty()) {
            return;
        }
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0);
        for (int i = 0; i < orderIds.size(); i++) {
            if (orderIds.size() - 1 == i) {
                query.equalTo(Order.FIELD_ID, orderIds.get(i));
            } else {
                query.equalTo(Order.FIELD_ID, orderIds.get(i)).or();
            }
        }
        RealmResults<Order> realmResults = query.findAll();
        for (Order order : realmResults) {
            orders.add(order);
        }
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
        initVariables();
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        if (orders == null || orders.isEmpty()) {
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
        buttonSelectAndPay = (TextView) rootView.findViewById(R.id.button_select_and_pay);
        emptyOrderText = (TextView) rootView.findViewById(R.id.empty_order_text_view);
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
}
