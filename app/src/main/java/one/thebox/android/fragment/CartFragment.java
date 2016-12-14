package one.thebox.android.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Events.UpdateCartEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;

import static one.thebox.android.fragment.SearchDetailFragment.BROADCAST_EVENT_TAB;
import static one.thebox.android.fragment.SearchDetailFragment.EXTRA_NUMBER_OF_TABS;

public class CartFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private Order order;
    private RecyclerView recyclerView;
    private TextView proceedToPayment;
    private SearchDetailAdapter userItemRecyclerAdapter;
    private View rootView;
    private TextView emptyCartText;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initVariables();
                    setupRecyclerView();
                }
            });

        }
    };

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
        if (order != null) {
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
            proceedToPayment.setText("Total Cost: Rs " + order.getTotalPrice() + "\n" + "Proceed to Payment");
        }

        if (userItemRecyclerAdapter == null) {
            userItemRecyclerAdapter = new SearchDetailAdapter(getActivity());
            userItemRecyclerAdapter.setBoxItems(order.getBoxItemsObjectFromUserItem(), null);
            userItemRecyclerAdapter.setShouldRemoveBoxItemOnEmptyQuantity(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(userItemRecyclerAdapter);
        } else {
            userItemRecyclerAdapter.setBoxItems(order.getBoxItemsObjectFromUserItem(), null);
            userItemRecyclerAdapter.notifyDataSetChanged();
        }

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
                startActivity(ConfirmAddressActivity.getInstance(getActivity(), orders, false));
            }
        });
        emptyCartText = (TextView) rootView.findViewById(R.id.empty_text);
    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
        proceedToPayment.setTranslationY(-offset);
    }


    @Subscribe
    public void onUpdateCart(UpdateCartEvent UpdateCartEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initVariables();
                    setupRecyclerView();
                }
            });
        }
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
        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setImageDrawable(getResources().getDrawable(R.drawable.ic_thebox_identity_mono));
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 7));
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_EVENT_TAB));

        onUpdateCart(new UpdateCartEvent(3));
    }
}
