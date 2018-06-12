package one.thebox.android.adapter.viewpager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.R;

/**
 * Created by developers on 10/04/17.
 */

public class ViewPagerAdapterHome extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private ArrayList<String> mFragmentTitleList;
    private Context context;

    public ViewPagerAdapterHome(FragmentManager manager, Context context, ArrayList<String> mFragmentTitleList) {
        super(manager);
        this.context = context;
        this.mFragmentTitleList = mFragmentTitleList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentTitleList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    public View getTabView(int position) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.custom_tab_home, null);

        TextView textView = (TextView) view.findViewById(R.id.text_view_title);
        if (mFragmentTitleList != null) {
            if (mFragmentTitleList.size() > 0) {
                textView.setText(mFragmentTitleList.get(position));
            }
        }
        return view;
    }
}
