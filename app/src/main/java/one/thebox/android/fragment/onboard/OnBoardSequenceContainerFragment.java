package one.thebox.android.fragment.onboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;*/

import one.thebox.android.R;
import one.thebox.android.activity.RequestOtpActivity;
import one.thebox.android.adapter.onboard.OnBoardSequenceFragmentAdapter;
import one.thebox.android.fragment.base.FragmentBase;

/**
 * Created by developers on 09/02/2017 AD.
 */

public class OnBoardSequenceContainerFragment extends FragmentBase {

    /**
     * Adapter
     */
    private OnBoardSequenceFragmentAdapter onBoardSequenceFragmentAdapter;

    private ViewPager viewPager;
    /*private PageIndicator pageIndicator;*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboard_sequence_container, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        onBoardSequenceFragmentAdapter = new OnBoardSequenceFragmentAdapter(getChildFragmentManager());

        viewPager = (ViewPager) getView().findViewById(R.id.pager);
        viewPager.setAdapter(onBoardSequenceFragmentAdapter);

       /* pageIndicator = (CirclePageIndicator) getView().findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);*/

        //move to signin Activity
        TextView getStartedButton = (TextView) getView().findViewById(R.id.button_get_started);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), RequestOtpActivity.class);
                startActivity(intent);

            }
        });
    }

}
