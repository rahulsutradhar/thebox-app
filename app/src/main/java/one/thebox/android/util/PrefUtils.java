package one.thebox.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Set;

import one.thebox.android.Models.User;


/**
 * Created by Ajeet Kumar Meena on 06.09.15.
 */
public class PrefUtils {
    // Param keys
    public final static String KEY_EMAIL = "KEY_EMAIL";
    public final static String KEY_SCROLL_POSITION_CHAT = "KEY_SCROLL_POSITION_CHAT";
    public final static String KEY_NAME = "KEY_NAME";
    public final static String KEY_OBJECT = "KEY_OBJECT";
    public final static String KEY_IMAGE = "KEY_IMAGE";
    public final static String KEY_ID = "KEY_ID";
    final static String KEY_TOKEN = "token";
    final static String KEY_USERID = "userid";
    public static final String PREF_KEY_USER_ID = "pref_key_user_id";
    public static final String PREF_KEY_USER_DATA = "pref_data_user_data";
    public static final String PREF_KEY_SIGN_IN_STATUS = "pref_data_sign_in_status";
    // Location
    public static final String PREF_KEY_LOCATION = "PREF_KEY_LOCATION";
    public static final String PREF_KEY_LOCATION_STATUS = "PREF_KEY_LOCATION_STATUS";
    public static final String PREF_KEY_ADDRESS = "PREF_KEY_ADDRESS";
    public static final String PREF_KEY_ADDRESS_STATUS = "PREF_KEY_ADDRESS_STATUS";
    public static final String PREF_KEY_REQUIRED_DELETE = "DELETE_SHARED_PREFERNCES";


    private static final String FILE_NAME = "TROVO_PREFERENCES";
    public static final String PREF_KEY_CAMPAIGN = "PREF_KEY_CAMPAIGN";
    public static final String PREF_USER = "PREF_USER";
    private static SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    /**
     * Get an instance of the current site settings
     *
     * @param context
     */
    public PrefUtils(Context context) {
        appSharedPrefs = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    /**
     * convenience method to add preference listener
     *
     * @param listener preference listener
     * @param context  context
     */
    public static void addOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener, Context context) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * convenience method to remove preference listener
     *
     * @param listener preference listener
     * @param context  context
     */
    public static void removeOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener, Context context) {
        getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * convenience method to save location
     *
     * @param location location to be saved
     * @param context  context
     * @return true if success
     */
    public static boolean saveLocation(Location location, Context context) {
        return saveObjectToPrefs(PREF_KEY_LOCATION, location, context);
    }

    /**
     * convenience method to get location
     *
     * @param context context
     * @return location if exists else null
     */
    public static Location getLocation(Context context) {
        return getObjectFromPrefs(PREF_KEY_LOCATION, Location.class, context);
    }

    public static void saveUserId(Context context, int userId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_KEY_USER_ID, userId);
        editor.apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getInt(PREF_KEY_USER_ID, 0);
    }

    public static Boolean deleteRequired(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(PREF_KEY_REQUIRED_DELETE, true);
    }

    public static void setDeleteRequired(Context context, boolean b) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_REQUIRED_DELETE, b);
        editor.apply();
    }

    public static int getScrollViewPosition(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getInt(KEY_SCROLL_POSITION_CHAT, 0);
    }

    public static void setScrollViewPosition(Context context, int mScrollY) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(KEY_SCROLL_POSITION_CHAT, mScrollY);
        editor.apply();
    }

    /**
     * convenience method to save address
     *
     * @param address address to be saved
     * @param context context
     */
    public static void saveAddress(String address, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_KEY_ADDRESS, address);
        editor.apply();
    }

    /**
     * convenience method to get the address
     *
     * @param context context
     * @return address if exists else null
     */
    public static String getAddress(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREF_KEY_ADDRESS, null);
    }

    /**
     * convenience method set the location status
     *
     * @param locationStatus location status to be set
     * @param context        context
     */
    public static void saveLocationStatus(int locationStatus, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_KEY_LOCATION_STATUS, locationStatus);
        editor.apply();
    }


    /**
     * convenience method set the address status
     *
     * @param addressStatus address status to be set
     * @param context       context
     */
    public static void saveAddressStatus(int addressStatus, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_KEY_ADDRESS_STATUS, addressStatus);
        editor.apply();
    }

    /**
     * returns same SharedPrefs
     * through out the application
     *
     * @param context context
     * @return SharedPreference object
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        if (appSharedPrefs == null) {
            appSharedPrefs = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        return appSharedPrefs;
    }

    public static void saveToken(Context context, String token) {
        putString(context, KEY_TOKEN, token);
    }

    public static String getToken(Context context) {
        return getString(context, KEY_TOKEN, "");
    }

    public void setUserid(int userid) {
        prefsEditor.putInt(KEY_USERID, userid);
        prefsEditor.commit();
    }

    public int getUserid() {
        return appSharedPrefs.getInt(KEY_USERID, 0);
    }

    /**
     * converts any given object to json and
     * saves to shared prefs with given key
     *
     * @param prefKey key to be saved to
     * @param object  to be saved
     * @param context context
     * @return true if success
     */
    public static boolean saveObjectToPrefs(String prefKey, Object object, Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(object);
            editor.putString(prefKey, json);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * returns an object stored in preferences
     * if the key is not present or the type of
     * object is wrong returns null
     *
     * @param prefKey key of the required object
     * @param context context
     * @param <T>     type of object to be returned
     * @return object if present else null
     */
    public static <T> T getObjectFromPrefs(String prefKey, Class<T> type, Context context) {
        String json = getSharedPreferences(context).getString(prefKey, null);
        if (json != null) {
            try {
                Gson gson = new Gson();
                T result = gson.fromJson(json, type);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> T getObjectFromPrefsByType(String prefKey, Type type, Context context) {
        String json = getSharedPreferences(context).getString(prefKey, null);
        if (json != null) {
            try {
                Gson gson = new Gson();
                T result = gson.fromJson(json, type);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void putStringSet(Context context, String key, Set<String> stringSet) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putStringSet(key, stringSet);
        editor.apply();
    }

    public static Set<String> getStringSet(Context context, String key) {
        return getSharedPreferences(context).getStringSet(key, null);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPreferences(context).getBoolean(key, defValue);
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key, int defValue) {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defValue) {
        return getSharedPreferences(context).getString(key, defValue);
    }

    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    public static void removeSharedPref(Context context, String key) {
        getSharedPreferences(context).edit().remove(key).apply();
    }

    public static void saveUser(Context context, User user) {
        saveObjectToPrefs(PREF_USER, user, context);
    }

    public static User getUser(Context context) {
        return getObjectFromPrefs(PREF_USER, User.class, context);
    }

    public static boolean removeAll(Context context) {
        return getSharedPreferences(context).edit().clear().commit();
    }
}
