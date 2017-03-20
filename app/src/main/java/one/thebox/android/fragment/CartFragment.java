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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Events.UpdateCartEvent;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

import static one.thebox.android.fragment.SearchDetailFragment.BROADCAST_EVENT_TAB;

public class CartFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private Order order;
    private RecyclerView recyclerView;
    private TextView proceedToPayment;
    private SearchDetailAdapter userItemRecyclerAdapter;
    private View rootView;
    private TextView emptyCartText;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

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
        Realm realm = TheBox.getRealm();
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
        if (order == null || order.getUserItems() == null || order.getUserItems().isEmpty()) {
            emptyCartText.setVisibility(View.VISIBLE);
            proceedToPayment.setVisibility(View.GONE);
            return;
        } else {
            emptyCartText.setVisibility(View.GONE);
            proceedToPayment.setVisibility(View.VISIBLE);
            proceedToPayment.setText("Total Cost: Rs " + order.getTotalPrice() + "\n" + "Proceed to Payment");
        }

        if (userItemRecyclerAdapter == null) {
            userItemRecyclerAdapter = new SearchDetailAdapter(getActivity(), glideRequestManager);
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
        this.glideRequestManager = Glide.with(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        proceedToPayment = (TextView) rootView.findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAddressAndProceedPayment();

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
        ((MainActivity) getActivity()).getChatbutton().setVisibility(View.GONE);

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

        /**
         * Save CleverTap Event; OpenCart
         */
        setCleverTapEventOpenCart();
    }

    /**
     * Logic to navigate users from cart
     */
    public void checkAddressAndProceedPayment() {
        try {

            /**
             * set Clevertap Event Proceed from cart
             */
            setCleverTapEventProocedFromCart(order);

            User user = PrefUtils.getUser(getActivity());
            RealmList<Order> orders = new RealmList<>();
            orders.add(order);

            if (user.getAddresses() != null) {
                if (user.getAddresses().size() > 0) {

                    //if order exist move to slots else take to delivery address
                    if (OrderHelper.isOrderExist()) {
                        //move to slots
                        startActivity(ConfirmTimeSlotActivity.newInstance(getActivity(),
                                OrderHelper.getAddressAndOrder(orders), false));
                    } else {
                        //open Delivery Address Fragment
                        displayDeliveryAddress(user, orders);
                    }

                } else {
                    //open Add Address Activity
                    addDeliverAddress(orders);
                }
            } else {
                //open Add Address Activity
                addDeliverAddress(orders);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open Add Address Form
     */
    public void addDeliverAddress(RealmList<Order> orders) {
        //open add address fragment blank
        Intent intent = new Intent(getActivity(), AddressActivity.class);
        /**
         * 1- My Account Fragment
         * 2- Cart Fargment
         */
        intent.putExtra("called_from", 2);
        /**
         * 1- add address
         * 2- edit address
         */
        intent.putExtra(Constants.EXTRA_ADDRESS_TYPE, 1);
        intent.putExtra(Constants.EXTRA_LIST_ORDER, CoreGsonUtils.toJson(orders));
        startActivity(intent);
    }

    /**
     * Show Delivery Address
     */
    public void displayDeliveryAddress(User user, RealmList<Order> orders) {
        Address address = user.getAddresses().first();
        Intent intent = new Intent(getActivity(), AddressActivity.class);
        intent.putExtra("called_from", 2);
        intent.putExtra(Constants.EXTRA_LIST_ORDER, CoreGsonUtils.toJson(orders));
        intent.putExtra("delivery_address", CoreGsonUtils.toJson(address));
        startActivity(intent);
    }

    public void setCleverTapEventProocedFromCart(Order order) {
        HashMap<String, Object> cartItems = new HashMap<>();
        cartItems.put("cart_id", order.getId());
        cartItems.put("user_id", order.getUserId());
        cartItems.put("total_price_cart", order.getTotalPrice());
        cartItems.put("item_quantity_cart", order.getUserItems().size());

        TheBox.getCleverTap().event.push("proceed_from_cart", cartItems);
    }

    public void setCleverTapEventOpenCart() {
        try {
            HashMap<String, Object> cartItems = new HashMap<>();
            cartItems.put("cart_id", order.getId());
            cartItems.put("user_id", order.getUserId());
            cartItems.put("total_price_cart", order.getTotalPrice());
            cartItems.put("item_quantity_cart", order.getUserItems().size());

            TheBox.getCleverTap().event.push("open_cart", cartItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
