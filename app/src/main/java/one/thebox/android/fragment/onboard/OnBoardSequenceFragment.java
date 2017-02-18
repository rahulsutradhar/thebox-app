package one.thebox.android.fragment.onboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.R;
import one.thebox.android.fragment.base.FragmentBase;

/**
 * Created by developers on 09/02/2017 AD.
 */

public class OnBoardSequenceFragment extends FragmentBase {

    private int position;

    /**
     * Constructor
     */
    public OnBoardSequenceFragment() {

    }

    @SuppressLint("ValidFragment")
    public OnBoardSequenceFragment(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;

        switch (getPosition()) {
            case 0:
                view = inflater.inflate(R.layout.intro_slide1, container, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.intro_slide2, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.intro_slide3, container, false);
                break;
            default:
                view = inflater.inflate(R.layout.intro_slide1, container, false);
        }

        return view;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
