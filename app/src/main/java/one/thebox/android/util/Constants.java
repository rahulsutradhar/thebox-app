package one.thebox.android.util;

import one.thebox.android.Models.Locality;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class Constants {
    public static final String[] DATE_RANGE = {"12:00 P.M - 03:00 P.M",
            "03:00 P.M - 06:00 P.M", "06:00 P.M - 09:00 P.M"};
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PREF_SHOULD_OPEN_EXPLORE_BOXES = "should_open_explore_boxes";
    public static final Locality POWAI_LOCALITY = new Locality(400072,"Powai");
    public static final String PREF_IS_ORDER_IS_LOADING = "pref_is_order_is_loading";

}
