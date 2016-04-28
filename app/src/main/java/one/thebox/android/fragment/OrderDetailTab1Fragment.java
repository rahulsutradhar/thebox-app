package one.thebox.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.Models.Box;
import one.thebox.android.R;



public class OrderDetailTab1Fragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
/*
    private OrderDetailAdapter orderDetailAdapter;
*/


    public OrderDetailTab1Fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        initViews();
        setupRecyclerAdapter();
        return rootView;
    }

    private void setupRecyclerAdapter() {
      /*  orderDetailAdapter = new OrderDetailAdapter(getActivity());
        for (int i = 0; i < 10; i++) {
            orderDetailAdapter.addBoxItem(new Box.BoxItem());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(orderDetailAdapter);*/
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

}
