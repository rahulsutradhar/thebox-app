package one.thebox.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.adapter.SearchResultAllItemAdapter;


public class AllItemsFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private SearchResultAllItemAdapter searchResultAllItemAdapter;

    public AllItemsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_all_items, container, false);
        initViews();
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        searchResultAllItemAdapter = new SearchResultAllItemAdapter(getActivity());
        for (int i = 0; i < 10; i++) {
            searchResultAllItemAdapter.addSearchResult(new SearchResult());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchResultAllItemAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

    @Subscribe
    public void onSearchEvent(SearchEvent searchEvent){
        Toast.makeText(getActivity(),searchEvent.getQuery(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
