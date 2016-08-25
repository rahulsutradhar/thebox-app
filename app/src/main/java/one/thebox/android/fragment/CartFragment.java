package one.thebox.android.fragment;

import android.app.Activity;
import android.content.Intent;
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
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;

public class CartFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private Order order;
    private RecyclerView recyclerView;
    private TextView proceedToPayment;
    private SearchDetailAdapter userItemRecyclerAdapter;
    private View rootView;
    private TextView emptyCartText;

    public CartFragment() {
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    private void initVariables() {
        int cartId = PrefUtils.getUser(getActivity()).getCartId();
        Realm realm = MyApplication.getRealm();
        Order order = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0)
                .equalTo(Order.FIELD_ID, cartId).findFirst();
        if(order!=null) {
            this.order = realm.copyFromRealm(order);
        }
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
            proceedToPayment.setVisibility(View.GONE);
            return;
        } else {
            emptyCartText.setVisibility(View.GONE);
            proceedToPayment.setVisibility(View.VISIBLE);
            proceedToPayment.setText("Total Cost: Rs "+ order.getTotalPriceOfUserItems() + "\n"+"Proceed to Payment");
        }

        userItemRecyclerAdapter = new SearchDetailAdapter(getActivity());
        userItemRecyclerAdapter.setBoxItems(order.getBoxItemsObjectFromUserItem(), null);
        userItemRecyclerAdapter.setShouldRemoveBoxItemOnEmptyQuantity(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userItemRecyclerAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        proceedToPayment = (TextView) rootView.findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmList<Order> orders = new RealmList<>();
                orders.add(order);
                startActivity(ConfirmAddressActivity.getInstance(getActivity(), orders,false));
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

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle("Cart");
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
        ((MainActivity) getActivity()).getSearchViewHolder().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setImageDrawable(getResources().getDrawable(R.drawable.ic_thebox_identity_mono));
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 7));
            }
        });
    }
}
