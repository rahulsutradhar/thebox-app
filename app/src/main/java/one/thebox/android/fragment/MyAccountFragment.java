package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import one.thebox.android.R;
import one.thebox.android.activity.AddressesActivity;
import one.thebox.android.activity.OrderDetailActivity;


public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView showAllAddressesButton;
    private TextView showAllOrdersButton;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        showAllAddressesButton = (TextView) rootView.findViewById(R.id.button_show_all_address);
        showAllOrdersButton = (TextView) rootView.findViewById(R.id.button_show_all_orders);
        showAllAddressesButton.setOnClickListener(this);
        showAllOrdersButton.setOnClickListener(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_show_all_address: {
                startActivity(new Intent(getActivity(), AddressesActivity.class));
                break;
            }
            case R.id.button_show_all_orders: {
                startActivity(new Intent(getActivity(), OrderDetailActivity.class));
                break;
            }
        }
    }
}
