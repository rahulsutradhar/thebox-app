package one.thebox.android.util;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nbansal2211 on 27/01/17.
 */

public class AppUtil {

    static List<String> getEmails(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        return getAccount(accountManager, context);
    }

    private static List<String> getAccount(AccountManager accountManager, Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        List<String> list = new ArrayList<>();
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts != null && accounts.length > 0) {
            for (Account a : accounts) {
                if (!TextUtils.isEmpty(a.name) && isValidEmail(a.name)) {
                    list.add(a.name);
                }
            }
        } else {
            return null;
        }
        return list;
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    public static boolean checkPermission(Context context, String permission) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
