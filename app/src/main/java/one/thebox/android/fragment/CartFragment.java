package one.thebox.android.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Events.UpdateCartEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.FillUserInfoActivity;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.adapter.cart.CartAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

public class CartFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private Order order;
    private RecyclerView recyclerView;
    private TextView proceedToPayment;
    private CartAdapter adapter;
    private View rootView;
    private RelativeLayout emptyCartLayout;
    private List<BoxItem> boxItems = new ArrayList<>();

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

                    //initVariables();
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

    private void initVariables(boolean isUpdateRecyclerview) {
        boxItems = CartHelper.getCartItems();
        if (boxItems != null) {
            if (boxItems.size() > 0) {
                if (isUpdateRecyclerview) {
                    setupRecyclerView();
                }
                setCartPrice(CartHelper.getCartPrice());

            } else {
                //cart is Empty
                setCartEmpty();
            }
        } else {
            //cart is Empty
            setCartEmpty();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews();
        initVariables(true);
        setupAppBarObserver();
        return rootView;
    }

    public void setCartEmpty() {
        emptyCartLayout.setVisibility(View.VISIBLE);
        proceedToPayment.setText("");
        proceedToPayment.setVisibility(View.GONE);
    }

    public void setCartPrice(float price) {
        proceedToPayment.setText("Total Cost: " + Constants.RUPEE_SYMBOL + " " + price + "\n" + "Proceed to Payment");
    }

    public boolean doCartHasItems() {

        if (order == null || order.getUserItems() == null || order.getUserItems().isEmpty()) {
            emptyCartLayout.setVisibility(View.VISIBLE);
            proceedToPayment.setVisibility(View.GONE);
            return false;
        } else {
            emptyCartLayout.setVisibility(View.GONE);
            proceedToPayment.setVisibility(View.VISIBLE);
            proceedToPayment.setText("Total Cost: " + Constants.RUPEE_SYMBOL + " " + order.getTotalPrice() + "\n" + "Proceed to Payment");
            return true;
        }
    }

    private void setupRecyclerView() {
        emptyCartLayout.setVisibility(View.GONE);
        proceedToPayment.setVisibility(View.VISIBLE);

        if (adapter == null) {
            adapter = new CartAdapter(getActivity(), glideRequestManager);
            adapter.setBoxItems(boxItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setBoxItems(boxItems);
            adapter.notifyDataSetChanged();
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

                // checkUserDetailsAndProceedPayment();

            }
        });
        emptyCartLayout = (RelativeLayout) rootView.findViewById(R.id.empty_cart);
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
                    initVariables(false);
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

       /* LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_EVENT_TAB));
*/
        // onUpdateCart(new UpdateCartEvent(3));

        /**
         * Save CleverTap Event; OpenCart
         */
        //setCleverTapEventOpenCart();
    }

    /**
     * Logic to navigate users from cart
     */
    public void checkUserDetailsAndProceedPayment() {
        try {

            /**
             * set Clevertap Event Proceed from cart
             */
            setCleverTapEventProocedFromCart(order);

            User user = PrefUtils.getUser(getActivity());
            Setting setting = PrefUtils.getSettings(TheBox.getInstance());
            RealmList<Order> orders = new RealmList<>();
            orders.add(order);

            if (user != null) {
                //check for user details
                if (setting.isUserDataAvailable()) {
                    checkForAddress(user, orders);
                } else {
                    if (user.getName() != null && user.getEmail() != null) {
                        checkForAddress(user, orders);
                    } else {
                        //proceed to user details Activity
                        openUserDetailsActivity(orders);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * User Data is Available now check Address
     */
    public void checkForAddress(User user, RealmList<Order> orders) {
        try {
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

    //User Details Activity
    public void openUserDetailsActivity(RealmList<Order> orders) {
        startActivity(FillUserInfoActivity.newInstance(getActivity(), orders));
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
