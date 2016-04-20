package one.thebox.android.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import one.thebox.android.R;

public class CustomToast {
    public static void show(Context context, String text) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custon_toast, null);
        TextView textV = (TextView) layout.findViewById(R.id.text_view);
        textV.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}