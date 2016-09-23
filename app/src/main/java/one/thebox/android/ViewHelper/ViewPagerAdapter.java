package one.thebox.android.ViewHelper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.Category;
import one.thebox.android.R;
import one.thebox.android.util.DisplayUtil;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<Category> mFragmentCategoryList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Context context;

    public ViewPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, Category category) {
        mFragmentList.add(fragment);
        mFragmentCategoryList.add(category);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mFragmentCategoryList.isEmpty()) {
            return mFragmentTitleList.get(position);
        } else {
            return mFragmentCategoryList.get(position).getTitle();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return mFragmentList.indexOf(object);
    }

    public View getTabView(int position, boolean isSelected) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        return getTabView(view, position, isSelected);
    }

    public View getTabView(View view, int position, boolean isSelected) {
        TextView title = (TextView) view.findViewById(R.id.text_view_category_name);
        TextView numberOfItems = (TextView) view.findViewById(R.id.number_of_item);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.holder);
        title.setText(mFragmentCategoryList.get(position).getTitle());
        if (mFragmentCategoryList.get(position).getNoOfItems() == 1) {
            numberOfItems.setText(mFragmentCategoryList.get(position).getNoOfItems() + " item");
        } else {
            numberOfItems.setText(mFragmentCategoryList.get(position).getNoOfItems() + " items");
        }

        Glide.with(context)
                .load(mFragmentCategoryList.get(position).getIconUrl())
                .centerCrop()
                .crossFade()
                .into(icon);

        //Picasso.with(context).load(mFragmentCategoryList.get(position).getIconUrl()).fit().into(icon);
        //Picasso.with(context).load(mFragmentCategoryList.get(position).getIconUrl()).resize(42,42).into(icon);
        //Integer image_size = DisplayUtil.dpToPx(context, 42);
        //Picasso.with(context).load(mFragmentCategoryList.get(position).getIconUrl()).resize(image_size,image_size).into(icon);

        if (isSelected) {
            icon.getLayoutParams().height = DisplayUtil.dpToPx(context, 48);
            icon.getLayoutParams().width = DisplayUtil.dpToPx(context, 48);
            icon.requestLayout();
            title.setTextColor(context.getResources().getColor(R.color.black));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            numberOfItems.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            layout.setBackgroundResource(R.drawable.tab_layout_selected);
            layout.setPadding(DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6));
            layout.requestLayout();
        } else {
            icon.getLayoutParams().height = DisplayUtil.dpToPx(context, 42);
            icon.getLayoutParams().width = DisplayUtil.dpToPx(context, 42);
            icon.requestLayout();
            title.setTextColor(context.getResources().getColor(R.color.primary_text_color));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            numberOfItems.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            layout.setBackgroundResource(R.drawable.tab_layout);
            layout.setPadding(DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2));
            layout.requestLayout();
        }
        return view;
    }
}
