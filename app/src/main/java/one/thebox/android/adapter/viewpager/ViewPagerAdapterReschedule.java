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

import one.thebox.android.Models.reschedule.Reschedule;
import one.thebox.android.R;

/**
 * Created by developers on 01/04/17.
 */

public class ViewPagerAdapterReschedule extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private Context context;
    private ArrayList<Reschedule> rescheduleArrayList;

    /**
     * Constructor
     */
    public ViewPagerAdapterReschedule(FragmentManager manager, Context context, ArrayList<Reschedule> reschedules) {
        super(manager);
        this.context = context;
        this.rescheduleArrayList = reschedules;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public int getCount() {
        return rescheduleArrayList.size();
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(context).inflate(R.layout.item_tab_reschedule, null);
        TextView textViewHeader = (TextView) tab.findViewById(R.id.header_tab_text);
        TextView textViewSubHeader = (TextView) tab.findViewById(R.id.sub_header_tab_text);
        Reschedule reschedule = rescheduleArrayList.get(position);

        if (reschedule != null) {
            if (reschedule.getTitle() != null) {
                textViewHeader.setText(reschedule.getTitle());
            }

            if (reschedule.getDescription() != null) {
                textViewSubHeader.setText(reschedule.getDescription());
            }
        }

        return tab;
    }


}
