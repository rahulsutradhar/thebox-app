package one.thebox.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import one.thebox.android.Models.ExploreItem;
import one.thebox.android.R;
import one.thebox.android.activity.ExploreItemDetailActivity;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 */
public class ExploreItemAdapter extends BaseRecyclerAdapter {

    private ArrayList<ExploreItem> exploreItems = new ArrayList<>();

    public ExploreItemAdapter(Context context) {
        super(context);
    }

    public void addExploreItems(ExploreItem exploreItem) {
        exploreItems.add(exploreItem);
    }

    public ArrayList<ExploreItem> getExploreItems() {
        return exploreItems;
    }

    public void setExploreItems(ArrayList<ExploreItem> exploreItems) {
        this.exploreItems = exploreItems;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    protected HeaderHolder getHeaderHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(exploreItems.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return exploreItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_explore;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_explore;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    class ItemViewHolder extends ItemHolder {

        private View itemView;
        private ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }

        public void setViewHolder(ExploreItem exploreItem) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, ExploreItemDetailActivity.class));
                }
            });
            Picasso.with(mContext).load("http://media.thedenverchannel.com/photo/2015/11/17/products%20image%20for%20grocery%20delivery_1447824686293_27027613_ver1.0.png").into(imageView);
        }
    }

    class HeaderViewHolder extends HeaderHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}