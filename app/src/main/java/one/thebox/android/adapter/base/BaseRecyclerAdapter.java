package one.thebox.android.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.app.TheBox;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter {
    public static final int RECYCLER_ADAPTER_ITEM = 200;
    public static final int RECYCLER_ADAPTER_HEADER = 201;
    public static final int RECYCLER_ADAPTER_FOOTER = 202;

    public static final int RECYCLER_VIEW_TYPE_NORMAL = 300;
    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;
    public static final int RECYCLER_VIEW_TYPE_FOOTER = 302;
    public static final int RECYCLER_VIEW_TYPE_HEADER_FOOTER = 303;
    public int adapterCurrentPosition;
    protected Context mContext;
    protected int mViewType = RECYCLER_VIEW_TYPE_NORMAL;
    private boolean isManyItemViewTypeAdapter;
    private OnItemClickListener itemClickListener;

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public boolean isManyItemViewTypeAdapter() {
        return isManyItemViewTypeAdapter;
    }

    public void setManyItemViewTypeAdapter(boolean manyItemViewTypeAdapter) {
        isManyItemViewTypeAdapter = manyItemViewTypeAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == RECYCLER_ADAPTER_ITEM) {

            View itemView = LayoutInflater.from(TheBox.getInstance()).inflate(getItemLayoutId(), parent, false);
            return getItemHolder(itemView);


        } else if (viewType == RECYCLER_ADAPTER_HEADER) {
            if (getHeaderLayoutId() != -1) {
                View headerView = LayoutInflater.from(TheBox.getInstance()).inflate(getHeaderLayoutId(), parent, false);

                return getHeaderHolder(headerView);
            }
        } else if (viewType == RECYCLER_ADAPTER_FOOTER) {
            if (getFooterLayoutId() != -1) {
                View footerView = LayoutInflater.from(TheBox.getInstance()).inflate(getFooterLayoutId(), parent, false);
                return getFooterHolder(footerView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == RECYCLER_ADAPTER_ITEM) {
            int itemPos = position;
            if (mViewType != RECYCLER_VIEW_TYPE_NORMAL && mViewType != RECYCLER_VIEW_TYPE_FOOTER) {
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
            if (position != -1 && getItemViewType(position) == RECYCLER_ADAPTER_FOOTER) {
                onBindViewFooterHolder((FooterHolder) holder, position);
            }
        }
    }

    public void setViewType(int viewType) {
        mViewType = viewType;
    }

    @Override
    public int getItemCount() {
        int itemCount;
        switch (mViewType) {
            case RECYCLER_VIEW_TYPE_NORMAL:
                itemCount = getItemsCount();
                break;
            case RECYCLER_VIEW_TYPE_FOOTER:
                itemCount = getItemsCount() + 1;
                break;
            case RECYCLER_VIEW_TYPE_HEADER:
                itemCount = getItemsCount() + 1;
                break;
            case RECYCLER_VIEW_TYPE_HEADER_FOOTER:
                itemCount = getItemsCount() + 2;
                break;
            default:
                itemCount = getItemsCount() + 2;
                break;
        }
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        adapterCurrentPosition = position - 1;
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
                    return RECYCLER_ADAPTER_FOOTER;
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

    protected abstract ItemHolder getItemHolder(View view, int position);


    protected abstract HeaderHolder getHeaderHolder(View view);

    protected abstract FooterHolder getFooterHolder(View view);

    public abstract void onBindViewItemHolder(ItemHolder holder, int position);

    public abstract void onBindViewHeaderHolder(HeaderHolder holder, int position);

    public abstract void onBindViewFooterHolder(FooterHolder holder, int position);

    public abstract int getItemsCount();

    protected abstract int getItemLayoutId();

    protected abstract int getItemLayoutId(int position);

    protected abstract int getHeaderLayoutId();

    protected abstract int getFooterLayoutId();

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

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
}
