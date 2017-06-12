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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.user.User;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.FillUserInfoActivity;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.cart.CartAdapter;
import one.thebox.android.api.Responses.cart.CartItemResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.SettingService;
import one.thebox.android.services.cart.CartHelperService;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;

public class CartFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private RecyclerView recyclerView;
    private TextView proceedToPayment;
    private CartAdapter adapter;
    private View rootView;
    private RelativeLayout emptyCartLayout;
    private ArrayList<BoxItem> boxItems = new ArrayList<>();
    private int requestCounter = 0;
    private GifImageView progressBar;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    public CartFragment() {
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    public void initVariables(boolean isUpdateRecyclerview) {
        boxItems = CartHelper.getCart();
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
        progressBar.setVisibility(View.GONE);
    }

    public void setCartPrice(float price) {
        proceedToPayment.setText("Total Cost: " + Constants.RUPEE_SYMBOL + " " + price + "\n" + "Proceed to Payment");
    }

    private void setupRecyclerView() {
        emptyCartLayout.setVisibility(View.GONE);
        proceedToPayment.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        if (adapter == null) {
            adapter = new CartAdapter(getActivity(), glideRequestManager, this);
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
        this.progressBar = (GifImageView) rootView.findViewById(R.id.progress_bar);
        emptyCartLayout = (RelativeLayout) rootView.findViewById(R.id.empty_cart);
        proceedToPayment = (TextView) rootView.findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request server to set cart
                if (ProductQuantity.getCartSize() > 0) {
                    requestServerConfirmCart();
                } else {
                    Toast.makeText(getActivity(), "Ypur cart seems empty, please add items", Toast.LENGTH_SHORT).show();
                }
            }
        });

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


        /**
         * Save CleverTap Event; OpenCart
         */
        //setCleverTapEventOpenCart();
    }


    /**
     * Request Server for Network Call
     */
    public void requestServerConfirmCart() {
        CartHelperService.stopCartService(getActivity(), false);
        requestCounter++;
        progressBar.setVisibility(View.VISIBLE);
        CartHelperService.updateCartToServer(getActivity(), this);
    }

    public void setCartUpdateServerResponse(boolean isSuccess, CartItemResponse response) {

        if (isSuccess) {
            progressBar.setVisibility(View.GONE);
            //Proceed
            doesUserExist(response.isMerge());

        } else {
            if (requestCounter > 1) {
                requestServerConfirmCart();
            } else {
                //if the call fails start background service again; if cart size is greater then 0
                CartHelperService.checkServiceRunningWhenAdded(getActivity());
                progressBar.setVisibility(View.GONE);
                requestCounter = 0;
                //show a error message about failed called
                Toast.makeText(getActivity(), "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Does User Detials Exist
     */
    public void doesUserExist(boolean isMerge) {
        Setting setting = new SettingService().getSettings(getActivity());
        if (setting != null) {
            if (setting.isUserDataAvailable()) {
                //available check for Address
                if (setting.isAddressAvailable()) {
                    //check if it is first order or not
                    if (!isMerge) {
                        //first order; navigate to display Address
                        displayDeliveryAddress(isMerge);
                    } else {
                        //navigate to Time Slot
                        proceedToSlots(isMerge);
                    }
                } else {
                    //open Add address activity
                    openAddressActivty(isMerge);
                }
            } else {
                //open Fill User Info Activity
                openUserInfoActivity(isMerge);
            }
        }

    }

    /**
     * Fill User Info Activity
     */
    public void openUserInfoActivity(boolean isMerge) {
        startActivity(FillUserInfoActivity.newInstance(getActivity(), isMerge));
    }

    /**
     * Add Address Activity
     */
    public void openAddressActivty(boolean isMerge) {
        addDeliverAddress(isMerge);
    }

    /**
     * Proceed To Slot Activity
     * When you have User Details and Address
     */
    public void proceedToSlots(boolean isMerge) {
        startActivity(ConfirmTimeSlotActivity.newInstance(getActivity(), isMerge));
    }

    /**
     * Open Add Address Form
     */
    public void addDeliverAddress(boolean isMerge) {
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
        intent.putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
        startActivity(intent);
    }

    /**
     * Show Delivery Address
     */
    public void displayDeliveryAddress(boolean isMerge) {
        User user = PrefUtils.getUser(getActivity());
        Address address = null;
        if (user.getAddresses() != null) {
            if (user.getAddresses().size() > 0) {
                address = user.getAddresses().first();
            }
        }
        Intent intent = new Intent(getActivity(), AddressActivity.class);
        intent.putExtra("called_from", 2);
        intent.putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
        intent.putExtra("delivery_address", CoreGsonUtils.toJson(address));
        startActivity(intent);
    }

    public void setCleverTapEventProocedFromCart(Order order) {
        HashMap<String, Object> cartItems = new HashMap<>();

        TheBox.getCleverTap().event.push("proceed_from_cart", cartItems);
    }

    public void setCleverTapEventOpenCart() {
        try {
            HashMap<String, Object> cartItems = new HashMap<>();
            TheBox.getCleverTap().event.push("open_cart", cartItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
