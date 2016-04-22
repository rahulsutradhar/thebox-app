package one.thebox.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import one.thebox.android.Models.Box;
import one.thebox.android.R;
import one.thebox.android.adapter.MyBoxRecyclerAdapter;
import one.thebox.android.adapter.SearchResultMyItemAdapter;


public class MyItemsFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private MyBoxRecyclerAdapter myBoxRecyclerAdapter;

    public MyItemsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_items, container, false);
        initViews();
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        ArrayList<Box> boxes = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        for (int i = 0; i < 5; i++) {
            ArrayList<Box.SmartItem> smartItems = new ArrayList<>();
            ArrayList<Box.BoxItem> boxItems = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                smartItems.add(new Box.SmartItem());
                boxItems.add(new Box.BoxItem());
            }
            boxes.add(new Box(smartItems, boxItems));
        }
        myBoxRecyclerAdapter = new MyBoxRecyclerAdapter(getActivity());
        myBoxRecyclerAdapter.setBoxes(boxes);
        recyclerView.setAdapter(myBoxRecyclerAdapter);
    }
    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

}
