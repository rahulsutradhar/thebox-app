package one.thebox.android.util;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;

import one.thebox.android.R;
import one.thebox.android.app.MyApplication;

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
        Account account = MyApplication.getInstance().getAccountUtil().getAccount();
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.authority), true);
    }
}
