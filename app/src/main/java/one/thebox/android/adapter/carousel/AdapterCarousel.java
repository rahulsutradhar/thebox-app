package one.thebox.android.adapter.carousel;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Events.DisplayProductForCarouselEvent;
import one.thebox.android.Models.carousel.Offer;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;

/**
 * Created by developers on 28/03/17.
 */

public class AdapterCarousel extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<Offer> carousel;
    private RequestManager glideRequestManager;

    /**
     * Constructor
     */
    public AdapterCarousel(Context context, RequestManager glideRequestManager) {
        super(context);
        this.context = context;
        this.glideRequestManager = glideRequestManager;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Offer> getCarousel() {
        return carousel;
    }

    public void setCarousel(ArrayList<Offer> carousel) {
        this.carousel = carousel;
        notifyDataSetChanged();
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
        return null;
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        position = position % carousel.size();
        itemViewHolder.setView(carousel.get(position), position);
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_card_carousel;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.carousel_image);
        }

        public void setView(final Offer offer, int position) {

            if (offer.getImageUrl() != null) {
                if (!offer.getImageUrl().isEmpty()) {
                    glideRequestManager.load(offer.getImageUrl())
                            .centerCrop()
                            .crossFade()
                            .into(imageView);
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (offer.isOpenLink() && !offer.getCategoryUuid().isEmpty() && !offer.getBoxUuid().isEmpty()) {
                            //send request to StoreFragment to show products
                            EventBus.getDefault().post(new DisplayProductForCarouselEvent(offer));
                        }
                    }
                });

            }

        }

        /**
         * Clever tap Event
         */
        public void setCleverTapEventForCarousel(Offer offer) {
            try {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("category_uuid", offer.getCategoryUuid());

                TheBox.getCleverTap().event.push("carousel_click", hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
