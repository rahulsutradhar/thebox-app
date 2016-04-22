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

import java.util.ArrayList;

import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.adapter.SearchResultAllItemAdapter;


public class AllItemsFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private SearchResultAllItemAdapter searchResultAllItemAdapter;
    private ArrayList<SearchResult> searchResults = new ArrayList<>();

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
        searchResultAllItemAdapter.setSearchResults(searchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchResultAllItemAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

    @Subscribe
    public void onSearchEvent(SearchEvent searchEvent) {
        searchResults.clear();
        for (int i = 0; i < searchEvent.getSearchAutoCompleteResponse().getCategories().size(); i++) {
            String categoryName = searchEvent.getSearchAutoCompleteResponse().getCategories().get(i);
            SearchResult searchResult = new SearchResult(categoryName);
            searchResults.add(searchResult);
        }
        for (int i = 0; i < searchEvent.getSearchAutoCompleteResponse().getItems().size(); i++) {
            String itemName = searchEvent.getSearchAutoCompleteResponse().getItems().get(i);
            SearchResult searchResult = new SearchResult(itemName);
            searchResults.add(searchResult);
        }
        searchResultAllItemAdapter.setSearchResults(searchResults);
        recyclerView.setAdapter(searchResultAllItemAdapter);
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
