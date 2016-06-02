package one.thebox.android.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;

import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.util.DisplayUtil;

/**
 * Created by Ajeet kumar Meena on 24-05-2016.
 */

public class BoxLoader {
    private Context context;
    private MaterialDialog materialDialog;

    public BoxLoader(Context context) {
        this.context = context;
    }

    public BoxLoader show() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .customView(R.layout.layout_loader, false);
        materialDialog = builder.show();
        materialDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(materialDialog.getWindow().getAttributes());
        lp.width = DisplayUtil.dpToPx(context, 100);
        lp.height = DisplayUtil.dpToPx(context, 100);
        lp.gravity = Gravity.CENTER;
        materialDialog.getWindow().setAttributes(lp);
        return this;
    }

    public BoxLoader dismiss() {
        materialDialog.dismiss();
        return this;
    }
}
