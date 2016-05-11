package one.thebox.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.adapter.UserItemRecyclerAdapter;
import one.thebox.android.util.CoreGsonUtils;

public class CartFragment extends Fragment {

    private static final String EXTRA_USER_ITEMS_ARRAY_LIST = "user_items_array_list";
    private ArrayList<Order> orders;
    private RecyclerView recyclerView;
    private TextView proceedToPayment;
    private UserItemRecyclerAdapter userItemRecyclerAdapter;
    private View rootView;

    public CartFragment() {
    }


    public static CartFragment newInstance(ArrayList<Order> orders) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER_ITEMS_ARRAY_LIST, CoreGsonUtils.toJson(orders));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }

    private void initVariables() {
        orders = CoreGsonUtils.fromJsontoArrayList(
                getArguments().getString(EXTRA_USER_ITEMS_ARRAY_LIST), Order.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews();
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        ArrayList<UserItem> userItems = new ArrayList<>();
        for (Order order : orders) {
            userItems.addAll(order.getUserItems());
        }
        userItemRecyclerAdapter = new UserItemRecyclerAdapter(getActivity(), userItems, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userItemRecyclerAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        proceedToPayment = (TextView) rootView.findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ConfirmAddressActivity.getInstance(getActivity(), orders));
            }
        });
    }


}
