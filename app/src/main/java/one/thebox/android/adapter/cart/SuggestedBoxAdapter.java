package one.thebox.android.adapter.cart;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import one.thebox.android.Models.items.Box;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.fragment.CartFragment;
import one.thebox.android.util.PrefUtils;

/**
 * Created by developers on 22/07/17.
 */

public class SuggestedBoxAdapter extends BaseRecyclerAdapter {

    private Context context;
    private RequestManager glideRequestManager;
    private ArrayList<Box> suggestedBoxes;
    private CartFragment cartFragment;

    public SuggestedBoxAdapter(Context context, RequestManager glideRequestManager, CartFragment cartFragment) {
        super(context);
        this.context = context;
        this.glideRequestManager = glideRequestManager;
        this.cartFragment = cartFragment;
    }

    public ArrayList<Box> getSuggestedBoxes() {
        return suggestedBoxes;
    }

    public void setSuggestedBoxes(ArrayList<Box> suggestedBoxes) {
        this.suggestedBoxes = suggestedBoxes;
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
        itemViewHolder.setViews(suggestedBoxes.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return suggestedBoxes.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.card_item_suggested_box;
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

        private ImageView iconBox;
        private TextView title;
        private TextView savings;
        private View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            iconBox = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            savings = (TextView) itemView.findViewById(R.id.text_view_savings);
        }

        public void setViews(final Box box) {
            try {

                title.setText(box.getTitle());
                if (box.getSavingTitle() != null) {
                    if (!box.getSavingTitle().isEmpty()) {
                        savings.setText(box.getSavingTitle());
                    } else {
                        savings.setVisibility(View.GONE);
                    }
                } else {
                    savings.setVisibility(View.GONE);
                }
                glideRequestManager.load(box.getBoxImage())
                        .centerCrop()
                        .crossFade()
                        .into(iconBox);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * clicked close this fragment and navigate to Search Details Adapter
                         *
                         * Set data for Search Detail Item fragment
                         */
                        if (cartFragment != null) {
                            //open search Detail Fragment for the box
                            Intent intent = new Intent(cartFragment.getActivity(), MainActivity.class);
                            intent.putExtra(Constants.EXTRA_BOX_UUID, box.getUuid());
                            intent.putExtra(Constants.EXTRA_BOX_NAME, box.getTitle());
                            intent.putExtra(Constants.EXTRA_ATTACH_FRAGMENT_NO, 5);
                            cartFragment.getActivity().startActivity(intent);
                            //close this fragment
                            cartFragment.getActivity().onBackPressed();

                        }
                    }
                });
            } catch (Exception e) {

            }
        }
    }
}
