package one.thebox.android.app;

/**
 * Created by developers on 15/02/17.
 */

public class Constants {

    /**
     * GCM Resgistration
     */
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    /**
     * GSM TOKEN
     */
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    /**
     * Fragment Number
     */
    public static final String EXTRA_ATTACH_FRAGMENT_NO = "extra_tab_no";

    /**
     * Notification Parameter
     */
    public static final String EXTRA_NOTIFICATION_PARAMETER = "extra_notification_params";

    /**
     * Items in Cart
     */
    public static final String EXTRA_ITEMS_IN_CART = "extra_item_in_cart";

    /**
     * Address Type
     */
    public static final String EXTRA_ADDRESS_TYPE = "extra_address_type";

    /**
     * Savings
     */
    public static final String SAVINGS = "savings";

    /**
     * Carousel Banner
     */
    public static final String CAROUSEL_BANNER = "carousel_banner";

    /**
     * Rupe Symbol
     */
    public static final String RUPEE_SYMBOL = "₹";

    /**
     * Category UUID
     */
    public static final String CATEGORY_UUID = "extra_category_uuid";

    /**
     * Constants to Notify Boradcast
     */
    public static final int DELIVERIES = 2;
    public static final int SUBSCRIPTION = 1;
    public static final int CART = 3;

    public static final int BROADCAST_SUBSCRIPTION_AND_DELIVERIES = 4;

    /**
     * Extra Category; to display category tabs
     */
    public static final String EXTRA_BOX_CATEGORY = "extra_box_category";

    /**
     * Clicked Category
     */
    public static final String EXTRA_CLICKED_CATEGORY_UID = "extra_clicked_category_uid";
    public static final String EXTRA_CLICK_POSITION = "extra_click_position";

    /**
     * EXTRA BOX NAME
     */
    public static final String EXTRA_BOX_NAME = "extra_box_name";

    /**
     * Extra Box Uuid
     */
    public static final String EXTRA_BOX_UUID = "extra_box_uuid";

    /**
     * EXTRA CATEGORY
     */
    public static final String EXTRA_CATEGORY = "extra_category";


    /**
     * EXTRA CATEGORY LIST
     */
    public static final String EXTRA_CATEGORY_LIST = "extra_category_list";

    /**
     * Update Cart Polling Time
     */
    public static long UPDATE_CART_POLLING_TIME = 5000;

    /**
     * UnAuthroized COde
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * Extra Cart Is Merging
     */
    public static final String EXTRA_IS_CART_MERGING = "extra_is_cart_merging";

    /**
     * Extra Address Selected
     */
    public static final String EXTRA_SELECTED_ADDRESS = "extra_selected_address";

    /**
     * EXTRA TIMESLOT SELECTED
     */
    public static final String EXTRA_TIMESLOT_SELECTED = "extra_timeslot_selected";

    /**
     * Extra Amount to pay
     */
    public static final String EXTRA_AMOUNT_TO_PAY = "extra_amount_to_pay";

    /**
     * EXTRA SELECTED UUID
     */
    public static final String EXTRA_SELECTED_ORDER_UUID = "extra_selected_order_uuid";

    /**
     * EXTRA SUBSCRIBE ITEM
     */
    public static final String EXTRA_SUBSCRIBE_ITEM = "extra_subscribe_item";

    /**
     * EXTRA ORDER
     */
    public static final String EXTRA_ORDER = "extra_order";

    /**
     * Extra Is order
     */
    public static final String EXTRA_IS_FROM_ORDER = "extra_is_from_order";

    /**
     * Extra Is Reschedule
     */
    public static final String EXTRA_IS_RESCHEDULE = "extra_is_reschedule";

    /**
     * EXTRA BOX ITEM
     */
    public static final String EXTRA_BOX_ITEM = "extra_box_item";

    /**
     * EXTRA ITEM CONFIG LIST
     */
    public final static String EXTRA_ITEM_CONFIG_LIST = "extra_item_config_array_list";

    /**
     * Search Detail Adapter
     * <p>
     * COnstant Value for View Type
     */
    public static final int VIEW_TYPE_SEARCH_ITEM = 0;
    public static final int VIEW_TYPE_SUBSCRIBE_ITEM = 1;

    /**
     * SUBSCRIPTION UPDATED
     */
    public final static String SUBSCRIPTION_UPDATED = "subscription_updated";

    /**
     * Pass Search Result Data
     */
    public final static String EXTRA_SEARCH_RESULT_DATA = "extra_search_result_data";

    /**
     * Pass Search Result Data
     */
    public final static String EXTRA_SEARCH_RESULT = "extra_search_result";


    /**
     * Pass Search Query
     */
    public final static String EXTRA_SEARCH_QUERY = "extra_search_query";

    /**
     * Event for updating search product on updating cart
     */
    public final static int UPDATE_QUANTITY_EVENT = 1;
    public final static int REMOVE_ITEM_EVENT = 2;
    public final static int UPDATE_ITEMCONFIG_EVENT = 3;

    /**
     * Shall Navigate to FAQ
     */
    public final static String EXTRA_NAVIGATE_TO_HOTLINE_FAQ = "extra_navigate_to_hotline_faq";


}
