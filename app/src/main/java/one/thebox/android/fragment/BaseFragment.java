package one.thebox.android.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import one.thebox.android.util.OnFragmentInteractionListener;

/**
 * Created by Ajeet Kumar Meena on 11-05-2016.
 */
public class BaseFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.showDrawerToggle(false);
    }
}
