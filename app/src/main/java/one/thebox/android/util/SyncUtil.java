package one.thebox.android.util;

import android.content.Context;

/**
 * Created by harsh on 14/12/15.
 */
public class SyncUtil {

    private String TAG = SyncUtil.class.getSimpleName();


    private Context context;

    public SyncUtil(Context context) {
        super();
        this.context = context;
    }

    public void setSyncSettings() {
        //Account account = TheBox.getInstance().getAccountUtil().getAccount();
      //  ContentResolver.setSyncAutomatically(account, context.getString(R.string.authority), true);
    }
}
