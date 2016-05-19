package one.thebox.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.adapter.UserItemRecyclerAdapter;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;

public class CartFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private Order order;
    private RecyclerView recyclerView;
    private TextView proceedToPayment;
    private UserItemRecyclerAdapter userItemRecyclerAdapter;
    private View rootView;
    private TextView emptyCartText;

    public CartFragment() {
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
      /*  Bundle args = new Bundle();
        ArrayList<Integer> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        args.putString(EXTRA_USER_ITEMS_ARRAY_LIST, CoreGsonUtils.toJson(orderIds));
        fragment.setArguments(args);*/
        return fragment;
    }

    private void initVariables() {
        int cartId = PrefUtils.getUser(getActivity()).getCartId();
        Realm realm = MyApplication.getRealm();
        Order order = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0)
                .equalTo(Order.FIELD_ID, cartId).findFirst();
        this.order = realm.copyFromRealm(order);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews();
        setupRecyclerView();
        setupAppBarObserver();
        return rootView;
    }

    private void setupRecyclerView() {
        if (order.getUserItems() == null || order.getUserItems().isEmpty()) {
            emptyCartText.setVisibility(View.VISIBLE);
            return;
        } else {
            emptyCartText.setVisibility(View.GONE);
        }
        RealmList<UserItem> userItems = new RealmList<>();
        for (int i = 0; i < order.getUserItems().size(); i++) {
            userItems.add(order.getUserItems().get(i));
        }
        userItemRecyclerAdapter = new UserItemRecyclerAdapter(getActivity(), order.getUserItems(), false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userItemRecyclerAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        proceedToPayment = (TextView) rootView.findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmList<Order> orders = new RealmList<>();
                orders.add(order);
                startActivity(ConfirmAddressActivity.getInstance(getActivity(), orders));
            }
        });
        emptyCartText = (TextView) rootView.findViewById(R.id.empty_text);
    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
        proceedToPayment.setTranslationY(-offset);
    }

    private void setupAppBarObserver() {
        AppBarObserver appBarObserver;
        Activity activity = getActivity();
        AppBarLayout appBarLayout = (AppBarLayout) activity
                .findViewById(R.id.app_bar_layout);
        if (appBarLayout != null) {
            appBarObserver = AppBarObserver.observe(appBarLayout);
            appBarObserver.addOffsetChangeListener(this);
        }
    }
}
