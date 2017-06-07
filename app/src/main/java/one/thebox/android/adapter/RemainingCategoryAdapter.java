package one.thebox.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import io.realm.RealmList;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Models.items.Box;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.UserCategory;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.util.CoreGsonUtils;

/**
 * Created by developers on 29/05/17.
 */

public class RemainingCategoryAdapter extends BaseRecyclerAdapter {

    private RealmList<Category> categories = new RealmList<>();
    private boolean isSearchDetailItemFragment;
    private RealmList<UserCategory> my_catIds;
    private Box box;
    private Context context;
    private RequestManager mRequestManager;

    public RemainingCategoryAdapter(Context context, RealmList<Category> categories, RealmList<UserCategory> my_catIds, RequestManager mRequestManager) {
        super(context);
        this.categories = categories;
        this.my_catIds = my_catIds;
        this.context = context;
        this.mRequestManager = mRequestManager;
    }

    public RemainingCategoryAdapter(Context context, RealmList<Category> categories, RequestManager mRequestManager) {
        super(context);
        this.categories = categories;
        this.context = context;
        this.mRequestManager = mRequestManager;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public boolean isSearchDetailItemFragment() {
        return isSearchDetailItemFragment;
    }

    public void setSearchDetailItemFragment(boolean searchDetailItemFragment) {
        isSearchDetailItemFragment = searchDetailItemFragment;
    }

    public RealmList<Category> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<Category> categories) {
        this.categories = categories;
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
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        if (position < categories.size()) {
            itemViewHolder.setViewHolder(categories.get(position), position);
        } else {
            itemViewHolder.setViewHolder(my_catIds.get(position - categories.size()), position);
        }

        // When used in Search Detail Fragment
        if (isSearchDetailItemFragment) {
            setAnimation(itemViewHolder.itemView);
        }
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {
    }

    // Animations
    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemsCount() {
        if (my_catIds == null) {
            return categories.size();
        } else {
            return (categories.size() + my_catIds.size());
        }
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.smart_box_item;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.empty_space_header;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public class ItemViewHolder extends ItemHolder {
        private TextView categoryNameTextView, noOfItems, savingTextView;
        private ImageView categoryIcon;
        private View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            categoryNameTextView = (TextView) itemView.findViewById(R.id.text_view_category_name);
            savingTextView = (TextView) itemView.findViewById(R.id.text_view_savings);
            noOfItems = (TextView) itemView.findViewById(R.id.number_of_item);
            categoryIcon = (ImageView) itemView.findViewById(R.id.icon);
        }

        public void setViewHolder(final Category category, final int position) {
            if (isSearchDetailItemFragment) {
                categoryNameTextView.setText(category.getTitle());
            } else {
                categoryNameTextView.setText(category.getTitle());
            }

            //Saving text display if not empty
               /* if (category.getAverageSavings() != null) {
                    if (!category.getAverageSavings().isEmpty()) {
                        savingTextView.setVisibility(View.VISIBLE);
                        savingTextView.setText(category.getAverageSavings());
                    } else {
                        savingTextView.setVisibility(View.GONE);
                        savingTextView.setText("");
                    }
                } else {
                    savingTextView.setVisibility(View.GONE);
                    savingTextView.setText("");
                }
*/
            //clickevent
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCLickEvent(category, position);
                }
            });


            mRequestManager.load(category.getCategoryImage())
                    .centerCrop()
                    .crossFade()
                    .into(categoryIcon);

        }

        public void setViewHolder(final UserCategory userCategory, final int position) {
            categoryNameTextView.setText(userCategory.getCategory().getMinititle());

            noOfItems.setVisibility(View.GONE);

                /*if (userCategory.getNo_of_items() == 1) {
                    noOfItems.setText("1 item subscribed");
                } else {
                    noOfItems.setText(userCategory.getNo_of_items() + " items subscribed");
                }

                categoryIcon.setMaxWidth(noOfItems.getWidth());
*/
            //Saving text display if not empty
                /*if (userCategory.getCategory().getAverageSavings() != null) {
                    if (!userCategory.getCategory().getAverageSavings().isEmpty()) {
                        savingTextView.setVisibility(View.VISIBLE);
                        savingTextView.setText(userCategory.getCategory().getAverageSavings());
                    } else {
                        savingTextView.setVisibility(View.GONE);
                        savingTextView.setText("");
                    }
                } else {
                    savingTextView.setVisibility(View.GONE);
                    savingTextView.setText("");
                }
*/
            //clickevent
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCLickEvent(userCategory.getCategory(), position);
                }
            });


            mRequestManager.load(userCategory.getCategory().getCategoryImage())
                    .centerCrop()
                    .crossFade()
                    .into(categoryIcon);

        }

        public void handleCLickEvent(Category category, int position) {
            if (isSearchDetailItemFragment) {
                EventBus.getDefault().post(new OnCategorySelectEvent(categories.get(position)));
            } else {
                
                Intent intent = new Intent(mContext, MainActivity.class)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 6)
                        .putExtra(Constants.EXTRA_BOX_CATEGORY, CoreGsonUtils.toJson(categories))
                        .putExtra(Constants.EXTRA_CLICKED_CATEGORY_UID, category.getUuid())
                        .putExtra(Constants.EXTRA_CLICK_POSITION, position)
                        .putExtra(Constants.EXTRA_BOX_NAME, box.getTitle());

                mContext.startActivity(intent);
            }
        }
    }
}
