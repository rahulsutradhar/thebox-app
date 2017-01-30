package one.thebox.android.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.SearchAutoCompleteAdapter;


public class AutoCompleteFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private SearchAutoCompleteAdapter searchAutoCompleteAdapter;
    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private TextView noItemFoundTextView;
    private int tempScroll;

    public AutoCompleteFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ((MainActivity) getActivity()).getToolbar().setTitle("Search");
        rootView = inflater.inflate(R.layout.fragment_all_items, container, false);
        initViews();
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        searchAutoCompleteAdapter = new SearchAutoCompleteAdapter(getActivity());
        searchAutoCompleteAdapter.setSearchResults(searchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchAutoCompleteAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy!=0) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(((BaseActivity)getActivity()).getContentView().getWindowToken(), 0);
                }
            }
        });
        noItemFoundTextView = (TextView) rootView.findViewById(R.id.no_item_found_text_view);
        ((MainActivity) getActivity()).getSearchView().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchAutoCompleteAdapter.getSearchResults().size() != 0)
                        ((MainActivity) getActivity()).attachSearchDetailFragment(searchAutoCompleteAdapter.getSearchResults().get(0));
                    return true;
                }
                return false;
            }
        });
    }

    @Subscribe
    public void onSearchEvent(SearchEvent searchEvent) {
        searchResults.clear();
        for (int i = 0; i < searchEvent.getSearchAutoCompleteResponse().getCategories().size(); i++) {
            String categoryName = searchEvent.getSearchAutoCompleteResponse().getCategories().get(i).getTitle();
            int categoryId = searchEvent.getSearchAutoCompleteResponse().getCategories().get(i).getId();
            SearchResult searchResult = new SearchResult(categoryId, categoryName);
            searchResults.add(searchResult);
        }
        for (int i = 0; i < searchEvent.getSearchAutoCompleteResponse().getItems().size(); i++) {
            String itemName = searchEvent.getSearchAutoCompleteResponse().getItems().get(i);
            SearchResult searchResult = new SearchResult(itemName);
            searchResults.add(searchResult);
        }
        if (searchResults.size() == 0) {
            noItemFoundTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noItemFoundTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        searchAutoCompleteAdapter.setSearchResults(searchResults);
        recyclerView.setAdapter(searchAutoCompleteAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.isSearchFragmentIsAttached = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getChatbutton().setVisibility(View.GONE);

        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setImageDrawable(getResources().getDrawable(R.drawable.ic_thebox_identity_mono));
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 7));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.isSearchFragmentIsAttached = false;
    }
}
