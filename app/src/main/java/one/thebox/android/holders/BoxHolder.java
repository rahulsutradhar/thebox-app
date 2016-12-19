package one.thebox.android.holders;

import android.view.View;

import one.thebox.android.Models.user.BoxData;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.MontserratTextView;

/**
 * Created by nbansal2211 on 17/12/16.
 */

public class BoxHolder extends BaseHolder {

    private final MontserratTextView title;

    public BoxHolder(View itemView) {
        super(itemView);
        this.title = (MontserratTextView) itemView.findViewById(R.id.title);
    }

    @Override
    public void onBind(int position, Object obj) {
        super.onBind(position, obj);
        BoxData box = (BoxData) obj;
        this.title.setText(box.getTitle());
    }
}
