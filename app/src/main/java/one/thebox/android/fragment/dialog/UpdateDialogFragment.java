package one.thebox.android.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import one.thebox.android.Models.update.UpdatePopupDetails;
import one.thebox.android.R;

import static android.content.Intent.ACTION_VIEW;

/**
 * Created by nbansal2211 on 03/01/17.
 */

public class UpdateDialogFragment extends DialogFragment {
    private UpdatePopupDetails details;
    private boolean isForceUpdate;

    public static UpdateDialogFragment getInstance(UpdatePopupDetails details, boolean isForceUpdate) {
        UpdateDialogFragment f = new UpdateDialogFragment();
        f.isForceUpdate = isForceUpdate;
        f.details = details;
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_app_update, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setCancelable(!isForceUpdate);
        initViews(v);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(!isForceUpdate);
        dialog.setCancelable(!isForceUpdate);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        ) {
                    // TODO do the "back pressed" work here
                    if (isForceUpdate) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    private void initViews(View v) {
        setTextOrGone(R.id.tv_title, v, details.getTitle());
        setTextOrGone(R.id.tv_subtext1, v, details.getSubtext1());
        setTextOrGone(R.id.tv_subtext2, v, details.getSubtext2());
        setTextOrGone(R.id.tv_subtext3, v, details.getSubtext3());
        ImageView iconImage = (ImageView) v.findViewById(R.id.iv_update_icon);
        if (!TextUtils.isEmpty(details.getIcon_url())) {
            Picasso.with(getActivity()).load(details.getIcon_url()).placeholder(R.drawable.announcement).into(iconImage);
        }
        v.findViewById(R.id.tv_update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(details.getRedirectUrl())) {
                    openPlayStore(details.getRedirectUrl());
                }
            }
        });
    }

    private void openPlayStore(String url) {
        Intent i = new Intent();
        i.setAction(ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        if (!isForceUpdate) {
            dismiss();
        }
    }

    private void setTextOrGone(int textViewId, View v, String text) {
        TextView tv = (TextView) v.findViewById(textViewId);
        if (TextUtils.isEmpty(text)) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setText(text);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
