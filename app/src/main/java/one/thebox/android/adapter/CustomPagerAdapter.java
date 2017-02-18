package one.thebox.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class CustomPagerAdapter<T> extends FragmentStatePagerAdapter {
	List<T> list;
	int fragmentType;
	PagerAdapterInterface<T> listener;

	public CustomPagerAdapter(FragmentManager fm, List<T> list,
			PagerAdapterInterface<T> listener) {
		super(fm);
		this.list = list;
		this.listener = listener;
		// TODO Auto-generated constructor stub
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return listener.getPageTitle(position, list.get(position));
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub

		return listener.getFragmentItem(position, list.get(position));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public interface PagerAdapterInterface<T> {
		public Fragment getFragmentItem(int position, T listItem);

		public CharSequence getPageTitle(int position, T listItem);
	}
}