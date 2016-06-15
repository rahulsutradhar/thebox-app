package one.thebox.android.ViewHelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 05-06-2016.
 */

public class ConnectionErrorViewHelper {
    private TextView connectionErrorTextView, retryButtonTextView;
    private LinearLayout holder;
    private View.OnClickListener onClickListener;

    public ConnectionErrorViewHelper(Activity activity, View.OnClickListener onClickListener) {
        this.holder = (LinearLayout) activity.findViewById(R.id.holder_connection_error);
        this.connectionErrorTextView = (TextView) activity.findViewById(R.id.connection_error_text_view);
        this.connectionErrorTextView = (TextView) activity.findViewById(R.id.connection_error_button);
        this.connectionErrorTextView.setOnClickListener(onClickListener);
    }

    public ConnectionErrorViewHelper(View rootView, View.OnClickListener onClickListener) {
        this.holder = (LinearLayout) rootView.findViewById(R.id.holder_connection_error);
        this.connectionErrorTextView = (TextView) rootView.findViewById(R.id.connection_error_text_view);
        this.retryButtonTextView = (TextView) rootView.findViewById(R.id.connection_error_button);
        this.retryButtonTextView.setOnClickListener(onClickListener);
    }

    public void isVisible(boolean isVisible) {
        if (isVisible) {
            holder.setVisibility(View.VISIBLE);
        } else {
            holder.setVisibility(View.GONE);
        }
    }
}
