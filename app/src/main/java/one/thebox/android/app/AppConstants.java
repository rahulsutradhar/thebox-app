package one.thebox.android.app;

/**
 * Created by nbansal2211 on 17/12/16.
 */

public interface AppConstants {
    public static interface ViewType{
        int BOX_HEADER_ITEM = 1;
        int USER_MY_BOX_ITEM = 2;
    }

    public static interface viewTypeInterface{
        public int getViewType();
    }
}
