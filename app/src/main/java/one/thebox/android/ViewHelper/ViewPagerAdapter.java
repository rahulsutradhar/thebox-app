package one.thebox.android.ViewHelper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.R;
import one.thebox.android.util.DisplayUtil;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
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

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
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
        TextView title = (TextView) view.findViewById(R.id.text_view);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.holder);
        title.setText(mFragmentTitleList.get(position));
        if (isSelected) {
            title.setTextColor(context.getResources().getColor(R.color.black));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            title.setPadding(DisplayUtil.dpToPx(context, 24), DisplayUtil.dpToPx(context, 24), DisplayUtil.dpToPx(context, 24), DisplayUtil.dpToPx(context, 24));
            linearLayout.setBackgroundResource(R.drawable.tab_layout_selected);
        } else {
            title.setTextColor(context.getResources().getColor(R.color.primary_text_color));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            title.setPadding(DisplayUtil.dpToPx(context, 16), DisplayUtil.dpToPx(context, 16), DisplayUtil.dpToPx(context, 16), DisplayUtil.dpToPx(context, 16));
            linearLayout.setBackgroundResource(R.drawable.tab_layout);
        }
        return view;
    }
}
