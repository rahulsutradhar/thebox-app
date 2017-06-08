package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.realm.RealmObject;
import one.thebox.android.R;
import one.thebox.android.app.AppConstants;
import one.thebox.android.holders.BaseHolder;
import one.thebox.android.holders.BoxHolder;
import one.thebox.android.holders.UserItemHolder;


/**
 * Created by nbansal2211 on 25/08/16.
 */
public class BaseCustomRecycleAdapter<T extends RealmObject> extends RecyclerView.Adapter<BaseHolder> {
    private List<T> list;
    private Context context;
    private LayoutInflater inflater;
    private RecyclerClickListener clickListener;

    public BaseCustomRecycleAdapter(Context context, List<T> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setClickListener(RecyclerClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder = null;
        switch (viewType) {
            case AppConstants.ViewType.BOX_HEADER_ITEM:
                holder = new BoxHolder(inflater.inflate(R.layout.item_subscriptions, null));
                break;
            case AppConstants.ViewType.USER_MY_BOX_ITEM:
                holder = new UserItemHolder(inflater.inflate(R.layout.item_subscribe_item, null));
                break;
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        holder.onBind(position, this.list.get(position));
        if (clickListener != null)
            holder.setClickListener(clickListener);
    }

    @Override
    public int getItemViewType(int position) {
        RealmObject model = list.get(position);
        if (model instanceof AppConstants.viewTypeInterface) {
            return ((AppConstants.viewTypeInterface) model).getViewType();
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public static interface RecyclerClickListener {
        // actionType
        int ACTION_ADD_TAG = 1;
        int ACTION_DISABLE_SCROLL = 2;

        public void onItemClicked(View itemView, int position, Object obj);
    }

}
