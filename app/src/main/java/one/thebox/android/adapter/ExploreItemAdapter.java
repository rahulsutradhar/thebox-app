package one.thebox.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import one.thebox.android.Models.ExploreItem;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.util.CoreGsonUtils;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 */
public class ExploreItemAdapter extends BaseRecyclerAdapter {

    /**
     * Glide Request Manager
     */
    private RequestManager glideRequestManager;

    private ArrayList<ExploreItem> exploreItems = new ArrayList<>();

    public ExploreItemAdapter(Context context, RequestManager glideRequestManager) {
        super(context);
        this.glideRequestManager = glideRequestManager;
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
    protected ItemHolder getItemHolder(View view, int position) {
        return null;
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
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    class ItemViewHolder extends ItemHolder {

        private View itemView;
        private ImageView imageView;
        private TextView title, subTitle, noOfItemsTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            title = (TextView) itemView.findViewById(R.id.title_text_view);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title_text_view);
            noOfItemsTextView = (TextView) itemView.findViewById(R.id.number_of_item);

        }

        public void setViewHolder(final ExploreItem exploreItem) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String exploreItemString = CoreGsonUtils.toJson(exploreItem);
                    mContext.startActivity(new Intent(mContext, MainActivity.class)
                            .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_DATA, exploreItemString)
                            .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 5));
                }
            });
            title.setText(exploreItem.getTitle());
            subTitle.setText(exploreItem.getSubTitle());
            noOfItemsTextView.setText(exploreItem.getTotalItems() + " items");
            int PICASSO_DISK_CACHE_SIZE = 1024 * 1024 * 10;

        /*    Downloader downloader = new OkHttpDownloader(TheBox.getInstance(),
                    PICASSO_DISK_CACHE_SIZE);
            Cache memoryCache = new LruCache(24000);
            Picasso mPicasso = new Picasso.Builder(TheBox.getInstance())
                    .downloader(downloader).memoryCache(memoryCache).build();*/
//            Picasso.with(mContext).load(exploreItem.getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);

            glideRequestManager.load(exploreItem.getImageUrl())
                    .centerCrop()
                    .crossFade()
                    .into(imageView);

        }
    }

    class HeaderViewHolder extends HeaderHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }


    }
}
