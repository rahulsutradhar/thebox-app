package one.thebox.android.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.Models.ExploreItem;
import one.thebox.android.R;
import one.thebox.android.adapter.ExploreItemAdapter;

public class ExploreBoxesFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private ExploreItemAdapter exploreItemAdapter;

    public ExploreBoxesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_explore_boxes, container, false);
        initViews();
        setupRecyclerView();
        return rootView;
    }



    private void setupRecyclerView() {
        exploreItemAdapter = new ExploreItemAdapter(getActivity());
        for (int i = 0; i < 10; i++) {
            exploreItemAdapter.addExploreItems(new ExploreItem());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(exploreItemAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
