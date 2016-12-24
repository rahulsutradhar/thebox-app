package one.thebox.android.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import one.thebox.android.adapter.BaseCustomRecycleAdapter;


/**
 * Created by nbansal2211 on 25/08/16.
 */
public class BaseHolder extends RecyclerView.ViewHolder {

    protected Context context;
    protected BaseCustomRecycleAdapter.RecyclerClickListener clickListener;

    public BaseHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
    }

    public void setClickListener(BaseCustomRecycleAdapter.RecyclerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    protected View findView(int id) {
        return itemView.findViewById(id);
    }

    protected TextView findTv(int id) {
        return (TextView) findView(id);
    }

    public void onBind(int position, Object obj) {

    }
}
