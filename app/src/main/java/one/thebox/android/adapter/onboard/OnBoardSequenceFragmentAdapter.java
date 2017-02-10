package one.thebox.android.adapter.onboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;

import one.thebox.android.fragment.onboard.OnBoardSequenceFragment;

/**
 * Created by developers on 09/02/2017 AD.
 */

public class OnBoardSequenceFragmentAdapter extends FragmentPagerAdapter {

    private int mCount;

    private HashMap<Integer, OnBoardSequenceFragment> fragmentHashMap;

    /**
     * Constructor
     */
    public OnBoardSequenceFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        fragmentHashMap = new HashMap<>();
        setmCount(3);

    }

    @Override
    public Fragment getItem(int position) {
        OnBoardSequenceFragment onBoardSequenceFragment;
        if (fragmentHashMap.containsKey(position)) {
            onBoardSequenceFragment = fragmentHashMap.get(position);
        } else {
            onBoardSequenceFragment = new OnBoardSequenceFragment(position);
            fragmentHashMap.put(position, onBoardSequenceFragment);
        }
        return onBoardSequenceFragment;
    }

    @Override
    public int getCount() {
        return getmCount();
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }
}
