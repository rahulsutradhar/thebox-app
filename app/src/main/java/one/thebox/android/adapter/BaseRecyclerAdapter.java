package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter {
    protected Context mContext;


    public static final int RECYCLER_ADAPTER_ITEM = 200;
    public static final int RECYCLER_ADAPTER_HEADER = 201;
    public static final int RECYCLER_ADAPTER_FOOTER = 202;
    public static final int RECYCLER_VIEW_TYPE_NORMAL = 300;
    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;
    public static final int RECYCLER_VIEW_TYPE_FOOTER = 302;
    public static final int RECYCLER_VIEW_TYPE_HEADER_FOOTER = 303;

    protected int mViewType = RECYCLER_VIEW_TYPE_NORMAL;
    private OnItemClickListener itemClickListener;

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RECYCLER_ADAPTER_ITEM) {
            View itemView = LayoutInflater.from(mContext.getApplicationContext()).inflate(getItemLayoutId(), parent, false);
            return getItemHolder(itemView);
        } else if (viewType == RECYCLER_ADAPTER_HEADER) {
            if (getHeaderLayoutId() != -1) {
                View headerView = LayoutInflater.from(mContext.getApplicationContext()).inflate(getHeaderLayoutId(), parent, false);

                return getHeaderHolder(headerView);
            }
        } else if (viewType == RECYCLER_ADAPTER_FOOTER) {
            if (getFooterLayoutId() != -1) {
                View footerView = LayoutInflater.from(mContext.getApplicationContext()).inflate(getFooterLayoutId(), parent, false);
                return getFooterHolder(footerView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == RECYCLER_ADAPTER_ITEM) {
            int itemPos = position;
            if (mViewType != RECYCLER_VIEW_TYPE_NORMAL) {
                itemPos = position - 1;
            }
            onBindViewItemHolder((ItemHolder) holder, itemPos);
            final int pos = itemPos;
            if (itemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClicked(pos);
                    }
                });
            }
        } else if (getItemViewType(position) == RECYCLER_ADAPTER_HEADER) {
            onBindViewHeaderHolder((HeaderHolder) holder, position);
        } else {
            onBindViewFooterHolder((FooterHolder) holder, position);
        }
    }

    public void setViewType(int viewType) {
        mViewType = viewType;
    }

    @Override
    public int getItemCount() {
        switch (mViewType) {
            case RECYCLER_VIEW_TYPE_NORMAL:
                return getItemsCount();
            case RECYCLER_VIEW_TYPE_FOOTER:
            case RECYCLER_VIEW_TYPE_HEADER:
                return getItemsCount() + 1;
            case RECYCLER_VIEW_TYPE_HEADER_FOOTER:
                return getItemsCount() + 2;
        }
        return getItemsCount();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mViewType) {
            case RECYCLER_VIEW_TYPE_NORMAL:
                return RECYCLER_ADAPTER_ITEM;
            case RECYCLER_VIEW_TYPE_HEADER:
                if (position == 0) {
                    return RECYCLER_ADAPTER_HEADER;
                } else {
                    return RECYCLER_ADAPTER_ITEM;
                }
            case RECYCLER_VIEW_TYPE_FOOTER:
                if (position == getItemCount() - 1) {
                    return RECYCLER_ADAPTER_HEADER;
                } else {
                    return RECYCLER_ADAPTER_ITEM;
                }
            case RECYCLER_VIEW_TYPE_HEADER_FOOTER:
                if (position == 0) {
                    return RECYCLER_ADAPTER_HEADER;
                } else if (position == getItemCount() - 1) {
                    return RECYCLER_ADAPTER_FOOTER;
                } else {
                    return RECYCLER_ADAPTER_ITEM;
                }
        }
        return super.getItemViewType(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    protected abstract ItemHolder getItemHolder(View view);

    protected abstract HeaderHolder getHeaderHolder(View view);

    protected abstract FooterHolder getFooterHolder(View view);

    public abstract void onBindViewItemHolder(ItemHolder holder, int position);

    public abstract void onBindViewHeaderHolder(HeaderHolder holder, int position);

    public abstract void onBindViewFooterHolder(FooterHolder holder, int position);

    public abstract int getItemsCount();

    protected abstract int getItemLayoutId();

    protected abstract int getHeaderLayoutId();

    protected abstract int getFooterLayoutId();

    protected abstract class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }


    }

    protected abstract class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    protected abstract class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}
