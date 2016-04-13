package one.thebox.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.Models.Box;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.adapter.MyBoxItemAdapter;
import one.thebox.android.adapter.SearchResultAllItemAdapter;


public class MyItemsFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private MyBoxItemAdapter myBoxItemAdapter;

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
        myBoxItemAdapter = new MyBoxItemAdapter(getActivity());
        for (int i = 0; i < 10; i++) {
            myBoxItemAdapter.addBoxItem(new Box.BoxItem());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myBoxItemAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

}
