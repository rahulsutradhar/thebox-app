package one.thebox.android.adapter.orders;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Vector;

import one.thebox.android.Models.order.CalenderMonth;
import one.thebox.android.R;
import one.thebox.android.util.DisplayUtil;

/**
 * Created by developers on 10/07/17.
 */

public class ViewPagerCalenderAdapter extends FragmentStatePagerAdapter {
    private Vector<Fragment> mFragmentList = new Vector<>();
    private Vector<CalenderMonth> mCalenderMonths = new Vector<>();

    private Context context;

    public ViewPagerCalenderAdapter(FragmentManager manager, Context context) {
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

    public void addFragment(Fragment fragment, CalenderMonth calenderMonth) {
        mFragmentList.add(fragment);
        mCalenderMonths.add(calenderMonth);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getItemPosition(Object object) {
        return mFragmentList.indexOf(object);
    }

    public View getTabView(int position, boolean isSelected) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.custom_tab_order_calender, null);
        return getTabView(view, position, isSelected);
    }

    public View getTabView(View view, int position, boolean isSelected) {
        TextView title = (TextView) view.findViewById(R.id.text_view_category_name);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.holder);

        if (mCalenderMonths.size() > 0) {
            title.setText(mCalenderMonths.get(position).getName());
        }

        if (isSelected) {
            title.setTextColor(context.getResources().getColor(R.color.black));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            layout.setBackgroundResource(R.drawable.tab_layout_selected);
            layout.setPadding(DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6));
            layout.requestLayout();
        } else {
            title.setTextColor(context.getResources().getColor(R.color.md_grey_800));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            layout.setBackgroundResource(R.drawable.tab_layout);
            layout.setPadding(DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2));
            layout.requestLayout();
        }
        return view;
    }

    /**
     * Remove ALL View
     */
    public void removeAll() {
        for (int i = 0; i < mFragmentList.size(); i++) {
            mFragmentList.set(i, null);
        }
        mFragmentList.clear();
        mCalenderMonths.clear();
        notifyDataSetChanged();
    }
}
