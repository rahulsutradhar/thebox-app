package one.thebox.android.activity;


import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import one.thebox.android.R;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Developers on 10/12/15.
 */
public class OnBoardingActivity extends BaseActivity {

    private final String AUTO_ENABLE_PERMISSION = "AUTO_ENABLE_PERMISSION";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        setStatusBarTranslucent(true);
        setStatusBarColor(getResources().getColor(R.color.white));

        //Ask user to enable Auto-Start permission in Xiomi device
       /* if (!PrefUtils.getBoolean(this, AUTO_ENABLE_PERMISSION)) {
            autoStartAppPermission();
        }*/
    }


    /**
     * For Xiomi Device
     */
    public void autoStartAppPermission() {
        try {

            String xiaomi = "Xiaomi";
            final String CALC_PACKAGE_NAME = "com.miui.securitycenter";
            final String CALC_PACKAGE_ACITIVITY = "com.miui.permcenter.autostart.AutoStartManagementActivity";
            String devieManufacturer = android.os.Build.MANUFACTURER;

            if (devieManufacturer.equalsIgnoreCase(xiaomi)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission");
                builder.setMessage("We need Auto Start Permission to enable Notification");
                builder.setCancelable(false);

                String positiveText = getString(android.R.string.ok);
                builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        PrefUtils.putBoolean(MyApplication.getAppContext(), AUTO_ENABLE_PERMISSION, true);

                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(CALC_PACKAGE_NAME, CALC_PACKAGE_ACITIVITY));
                        startActivity(intent);
                    }
                });

                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}