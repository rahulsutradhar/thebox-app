package one.thebox.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.Models.mycart.CartProductDetail;
import one.thebox.android.Models.items.Box;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.promotion.PromotionalOffer;
import one.thebox.android.Models.user.User;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.activity.FillUserInfoActivity;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.adapter.cart.CartAdapter;
import one.thebox.android.api.Responses.cart.CartItemResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.SettingService;
import one.thebox.android.services.cart.CartHelperService;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;

public class CartFragment extends Fragment {

    public static final int RECYCLER_VIEW_TYPE_NORMAL = 300;
    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;
    private RecyclerView recyclerView;
    private LinearLayout proceedForward, promotionalMessageLayout;
    private RelativeLayout promotionalTutorialLayout, parentLayout;
    private ImageView removeTutorial;
    private GifImageView progressLoader;
    private CartAdapter adapter;
    private View rootView;
    private RelativeLayout emptyCartLayout;
    private ArrayList<BoxItem> boxItems = new ArrayList<>();

    private int requestCounter = 0;
    private Toolbar toolbar;
    private TextView totalPriceCart, totalSavingsCart, totalPriceBottomStrip, deliveryCharges,
            forwardMessage, progressStepToCheckoutText, cartQuantityText, promotionalMessage, tutorialMessage;
    private LinearLayout progressIndicatorLayout;
    private View progressStep1, progressStep2, progressStep3;
    private Setting setting;
    private CardView bottomCard;
    private boolean isCartEmpty = false;
    private PromotionalOffer displayedPromotionalTutorial;
    private boolean isTutorialAvailble = false;

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
        return rootView;
    }

    public void initVariables(boolean isUpdateRecyclerview) {
        try {
            boxItems = CartHelper.getCart();
            if (boxItems != null) {
                if (boxItems.size() > 0) {
                    isCartEmpty = false;
                    if (isUpdateRecyclerview) {
                        //group the boxItem accoording to group
                        groupBoxItem();
                    }
                    setCartPrices(CartHelper.getCartPrice(), CartHelper.getTotalSavings(), CartHelper.getCartSize());

                } else {
                    //cart is Empty
                    isCartEmpty = true;
                    setCartEmpty();
                }
            } else {
                //cart is Empty
                isCartEmpty = true;
                setCartEmpty();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Group BoxItem According to Box
     */
    public void groupBoxItem() {
        TreeMap<String, ArrayList<BoxItem>> cartHashMap = new TreeMap<>();
        ArrayList<CartProductDetail> cartProductDetails = new ArrayList<>();

        for (BoxItem boxItem : boxItems) {
            if (boxItem.getBoxUuid() != null) {
                //grouping box wise
                if (cartHashMap.containsKey(boxItem.getBoxUuid())) {
                    ArrayList<BoxItem> getBoxItems = cartHashMap.get(boxItem.getBoxUuid());
                    getBoxItems.add(boxItem);
                    cartHashMap.put(boxItem.getBoxUuid(), getBoxItems);
                } else {
                    ArrayList<BoxItem> newBoxItems = new ArrayList<>();
                    newBoxItems.add(boxItem);
                    cartHashMap.put(boxItem.getBoxUuid(), newBoxItems);
                }
            }
        }

        setting = new SettingService().getSettings(getActivity());

        Set<String> keys = cartHashMap.keySet();
        for (String uuid : keys) {
            for (Box box : setting.getBoxes()) {
                if (uuid.equalsIgnoreCase(box.getUuid())) {
                    cartProductDetails.add(new CartProductDetail(uuid, box.getTitle(), cartHashMap.get(uuid)));
                }
            }
        }

        //reverse the list
        Collections.reverse(cartProductDetails);

        cartHashMap.clear();
        //setup recyclerview
        setupRecyclerView(cartProductDetails, setting);

        //check for promotional popups or tutorial popups
        checkForPopup();
    }

    /**
     * Empty State
     */
    public void setCartEmpty() {
        emptyCartLayout.setVisibility(View.VISIBLE);
        bottomCard.setVisibility(View.GONE);
        progressLoader.setVisibility(View.GONE);
        progressIndicatorLayout.setVisibility(View.GONE);
        cartQuantityText.setVisibility(View.GONE);
    }

    /**
     * Prices Details
     *
     * @param totalPrice
     * @param totalSavings
     * @param cartQuantity
     */
    public void setCartPrices(float totalPrice, float totalSavings, int cartQuantity) {
        totalPriceCart.setText(Constants.RUPEE_SYMBOL + totalPrice);
        totalPriceBottomStrip.setText(Constants.RUPEE_SYMBOL + totalPrice);
        totalSavingsCart.setText(Constants.RUPEE_SYMBOL + totalSavings);
        cartQuantityText.setText("(" + cartQuantity + ")");
    }

    /**
     * Set List of items
     */
    private void setupRecyclerView(ArrayList<CartProductDetail> cartProductDetailses, Setting setting) {
        emptyCartLayout.setVisibility(View.GONE);
        progressLoader.setVisibility(View.GONE);
        bottomCard.setVisibility(View.VISIBLE);

        if (adapter == null) {
            adapter = new CartAdapter(getActivity(), glideRequestManager, this);
            adapter.setCartProductDetails(cartProductDetailses);
            //check if suggestion is available or not
            if (setting.getSuggestedBoxes() != null) {
                if (!setting.getSuggestedBoxes().isEmpty()) {
                    adapter.setSuggestedBoxes(setting.getSuggestedBoxes());
                    adapter.setViewType(RECYCLER_VIEW_TYPE_HEADER);
                } else {
                    adapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
                }
            } else {
                adapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setCartProductDetails(cartProductDetailses);
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * Initialize Views
     */
    private void initViews() {
        this.glideRequestManager = Glide.with(this);
        this.parentLayout = (RelativeLayout) rootView.findViewById(R.id.parent_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        this.progressLoader = (GifImageView) rootView.findViewById(R.id.progress_bar);
        emptyCartLayout = (RelativeLayout) rootView.findViewById(R.id.empty_cart);
        proceedForward = (LinearLayout) rootView.findViewById(R.id.button_proceed_forward);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //blank
            }
        });

        proceedForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request server to set cart
                if (ProductQuantity.getCartSize() > 0) {
                    requestServerConfirmCart();
                    // set celver tap even when proceed from cart
                    setCleverTapEventProocedFromCart();

                } else {
                    Toast.makeText(getActivity(), "Your cart seems empty, please add items", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Tootalbar
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        cartQuantityText = (TextView) rootView.findViewById(R.id.cart_quantity);
        progressIndicatorLayout = (LinearLayout) rootView.findViewById(R.id.progress_indicator);
        progressStepToCheckoutText = (TextView) rootView.findViewById(R.id.progress_step_text);
        progressStep1 = (View) rootView.findViewById(R.id.progress_step1);
        progressStep2 = (View) rootView.findViewById(R.id.progress_step2);
        progressStep3 = (View) rootView.findViewById(R.id.progress_step3);


        //Bottom Card
        totalPriceCart = (TextView) rootView.findViewById(R.id.total_price_cart);
        totalSavingsCart = (TextView) rootView.findViewById(R.id.total_savings_cart);
        deliveryCharges = (TextView) rootView.findViewById(R.id.delivery_charges);
        totalPriceBottomStrip = (TextView) rootView.findViewById(R.id.cart_price_bottom_strip);
        forwardMessage = (TextView) rootView.findViewById(R.id.forward_message);

        bottomCard = (CardView) rootView.findViewById(R.id.bottom_card);

        promotionalMessageLayout = (LinearLayout) rootView.findViewById(R.id.promotional_tool);
        promotionalMessage = (TextView) rootView.findViewById(R.id.promotion_message_line1);

        promotionalTutorialLayout = (RelativeLayout) rootView.findViewById(R.id.tutorial_tool);
        removeTutorial = (ImageView) rootView.findViewById(R.id.remove_tutorial);
        tutorialMessage = (TextView) rootView.findViewById(R.id.tutorial_message);

        //cancelable popup
        removeTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTutorialAvailble) {
                    if (displayedPromotionalTutorial != null) {
                        removeTutorialPopup();
                    }
                }
            }
        });
        /**
         * Save CleverTap Event; OpenCart
         */
        setCleverTapEventOpenCart();


    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            setting = new SettingService().getSettings(getActivity());
            setForwardMessageWithIndicator();

        } catch (Exception e) {

        }
    }


    /**
     * Request Server for Network Call
     */
    public void requestServerConfirmCart() {
        CartHelperService.stopCartService(getActivity(), false);
        requestCounter++;
        progressLoader.setVisibility(View.VISIBLE);
        CartHelperService.updateCartToServer(getActivity(), this);
    }

    public void setCartUpdateServerResponse(boolean isSuccess, CartItemResponse response) {

        if (isSuccess) {
            progressLoader.setVisibility(View.GONE);
            //Proceed
            doesUserExist(response.isMerge());

        } else {
            if (requestCounter > 1) {
                requestServerConfirmCart();
            } else {
                //if the call fails start background service again; if cart size is greater then 0
                CartHelperService.checkServiceRunningWhenAdded(getActivity());
                progressLoader.setVisibility(View.GONE);
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


    /**
     * Detemine the forward message and Toolbar Indicator
     */
    public void setForwardMessageWithIndicator() {
        if (setting != null) {
            //check if cart is empty
            if (!isCartEmpty) {
                //check if its first order then display progress
                if (setting.isFirstOrder()) {
                    if (setting.isUserDataAvailable()) {
                        if (setting.isAddressAvailable()) {

                            progressIndicatorLayout.setVisibility(View.VISIBLE);
                            forwardMessage.setText("Select Address to Checkout");
                            progressStepToCheckoutText.setText("3 steps remaining");
                            progressStep1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_500));
                            progressStep2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_500));
                            progressStep3.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_500));

                        } else {
                            //Proceed to Address
                            progressIndicatorLayout.setVisibility(View.VISIBLE);
                            forwardMessage.setText("Add Address to Checkout");
                            progressStepToCheckoutText.setText("4 steps remaining");
                            progressStep1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_500));
                            progressStep2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_500));
                        }

                    } else {
                        //Proceed to User Data
                        progressIndicatorLayout.setVisibility(View.VISIBLE);
                        forwardMessage.setText("Add User Details to Checkout");
                        progressStepToCheckoutText.setText("5 steps remaining");
                        progressStep1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.md_green_500));

                    }
                } else {
                    progressIndicatorLayout.setVisibility(View.GONE);
                    forwardMessage.setText("Select Timeslot to Checkout");
                }
            }
        } else {
            progressIndicatorLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Check and display promotional popup
     */
    public void checkForPopup() {
        try {
            if (setting.isFirstOrder()) {
                //get message from preferences
                ArrayList<PromotionalOffer> promtionaFirstTimeOffer = CoreGsonUtils.fromJsontoArrayList(
                        PrefUtils.getString(getActivity(), Constants.EXTRA_PROMOTIONAL_OFFER_FIRST_TIME), PromotionalOffer.class);

                if (promtionaFirstTimeOffer != null) {
                    if (!promtionaFirstTimeOffer.isEmpty()) {
                        //set the first time offer data
                        checkPromotionalOfferMessage(promtionaFirstTimeOffer.get(0));
                    } else {
                        promotionalMessageLayout.setVisibility(View.GONE);
                    }
                } else {
                    promotionalMessageLayout.setVisibility(View.GONE);
                }
            } else {
                //get message from preferences
                ArrayList<PromotionalOffer> promotionTutorials = CoreGsonUtils.fromJsontoArrayList(
                        PrefUtils.getString(getActivity(), Constants.EXTRA_PROMOTIONAL_TUTORIAL), PromotionalOffer.class);

                if (promotionTutorials != null) {
                    if (!promotionTutorials.isEmpty()) {
                        //set the tutorial message
                        checkPromotionalTutorial(promotionTutorials);
                    } else {
                        promotionalTutorialLayout.setVisibility(View.GONE);
                    }
                } else {
                    promotionalTutorialLayout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Check to show promotional Message
     */
    public void checkPromotionalOfferMessage(PromotionalOffer promotionalOffer) {
        try {
            if (promotionalOffer.isFirstTime()) {
                //check for null and empty state
                if (promotionalOffer.getTitle() != null) {
                    if (!promotionalOffer.getTitle().isEmpty()) {
                        promotionalMessageLayout.setVisibility(View.VISIBLE);
                        promotionalMessage.setText(Html.fromHtml(promotionalOffer.getTitle()));
                    } else {
                        promotionalMessageLayout.setVisibility(View.GONE);
                    }
                } else {
                    promotionalMessageLayout.setVisibility(View.GONE);
                }

            } else {
                promotionalMessageLayout.setVisibility(View.GONE);
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    /**
     * Check for promotional Tutorial
     */
    public void checkPromotionalTutorial(ArrayList<PromotionalOffer> promotionalTutorials) {
        try {
            for (PromotionalOffer tutorial : promotionalTutorials) {
                //should not be shown earlier
                if (!tutorial.isChecked()) {
                    //check for null
                    if (tutorial.getTitle() != null) {
                        if (!tutorial.getTitle().isEmpty()) {
                            isTutorialAvailble = true;
                            displayedPromotionalTutorial = tutorial;
                            promotionalTutorialLayout.setVisibility(View.VISIBLE);
                            tutorialMessage.setText(Html.fromHtml(tutorial.getTitle()));
                        } else {
                            isTutorialAvailble = false;
                        }
                    } else {
                        isTutorialAvailble = false;
                    }
                    break;
                } else {
                    isTutorialAvailble = false;
                    continue;
                }
            }

            if (!isTutorialAvailble) {
                promotionalTutorialLayout.setVisibility(View.GONE);
            }

        } catch (Exception e) {

        }

    }

    /**
     * Remove Tutorial Popup or Cancelable popup
     */
    public void removeTutorialPopup() {
        try {
            ArrayList<PromotionalOffer> promotionTutorials = CoreGsonUtils.fromJsontoArrayList(
                    PrefUtils.getString(getActivity(), Constants.EXTRA_PROMOTIONAL_TUTORIAL), PromotionalOffer.class);
            boolean isRemoved = false;
            if (promotionTutorials != null) {
                if (!promotionTutorials.isEmpty()) {
                    for (PromotionalOffer tutorial : promotionTutorials) {
                        //check displayed tutorial with all the tutorial
                        if (tutorial.getUuid().equalsIgnoreCase(displayedPromotionalTutorial.getUuid())) {
                            tutorial.setChecked(true);
                            isRemoved = true;
                            isTutorialAvailble = false;
                            promotionalTutorialLayout.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
            }

            if (isRemoved) {
                PrefUtils.putString(getActivity(), Constants.EXTRA_PROMOTIONAL_TUTORIAL, CoreGsonUtils.toJson(promotionTutorials));
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }

    /**
     * When proceed from Cart
     */
    public void setCleverTapEventProocedFromCart() {
        HashMap<String, Object> cartItems = new HashMap<>();
        cartItems.put("user_uuid", PrefUtils.getUser(getActivity()).getUuid());
        cartItems.put("item_quantity_cart", ProductQuantity.getCartSize());
        cartItems.put("total_price_cart", CartHelper.getCartPrice());

        TheBox.getCleverTap().event.push("proceed_from_cart", cartItems);
    }

    /**
     * Clevertap event for cart Items
     */
    public void setCleverTapEventOpenCart() {
        try {
            HashMap<String, Object> cartItems = new HashMap<>();
            cartItems.put("user_uuid", PrefUtils.getUser(getActivity()).getUuid());
            cartItems.put("item_quantity_cart", ProductQuantity.getCartSize());
            cartItems.put("total_price_cart", CartHelper.getCartPrice());

            TheBox.getCleverTap().event.push("open_cart", cartItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
