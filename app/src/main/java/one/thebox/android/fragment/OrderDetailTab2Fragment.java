package one.thebox.android.fragment;


import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.R;


public class OrderDetailTab2Fragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;

    public OrderDetailTab2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_detail_item, container, false);
        return rootView;
    }

}
