package one.thebox.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.api.RequestBodies.SearchDetailResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchDetailFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private LinearLayout linearLayoutHolder;
    private ProgressBar progressBar;
    private String query;
    private int catId;
    private static final String EXTRA_QUERY = "extra_query";
    private static final String EXTRA_CAT_ID = "extra_cat_id";
    private ArrayList<Category> categories = new ArrayList<>();
    private FloatingActionButton floatingActionButton;

    public SearchDetailFragment() {
    }

    public static SearchDetailFragment getInstance(SearchResult searchResult) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_QUERY, searchResult.getResult());
        bundle.putInt(EXTRA_CAT_ID, searchResult.getId());
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search_detail, container, false);
            initViews();
            initVariables();
            setupRecyclerView();
            if (catId == 0) {
                getSearchDetails();
            } else {
                getCategoryDetail();
            }
        }
        return rootView;

    }

    private void initVariables() {
        query = getArguments().getString(EXTRA_QUERY);
        catId = getArguments().getInt(EXTRA_CAT_ID);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        linearLayoutHolder = (LinearLayout) rootView.findViewById(R.id.holder_linear_layout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_TAB_NO, 1));
            }
        });
    }

    private void setupRecyclerView() {
        searchDetailAdapter = new SearchDetailAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchDetailAdapter);
    }

    public void getSearchDetails() {
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        MyApplication.getAPIService().getSearchResults(PrefUtils.getToken(getActivity()), query)
                .enqueue(new Callback<SearchDetailResponse>() {
                    @Override
                    public void onResponse(Call<SearchDetailResponse> call, Response<SearchDetailResponse> response) {
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null) {
                            if (response.body().getSearchedItems() != null && !response.body().getSearchedItems().isEmpty()) {
                                ArrayList<UserItem> userItems = new ArrayList<UserItem>();
                                userItems.addAll(response.body().getMySearchItems());
                                userItems.addAll(response.body().getMyNonSearchedItems());
                                categories.add(response.body().getSearchedCategory());
                                categories.addAll(response.body().getRestCategories());
                                searchDetailAdapter.setBoxItems(new ArrayList<BoxItem>(response.body().getSearchedItems()), userItems, categories);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchDetailResponse> call, Throwable t) {
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void getCategoryDetail() {
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        MyApplication.getAPIService().getCategoryBoxItems(PrefUtils.getToken(getActivity()), catId)
                .enqueue(new Callback<CategoryBoxItemsResponse>() {
                    @Override
                    public void onResponse(Call<CategoryBoxItemsResponse> call, Response<CategoryBoxItemsResponse> response) {
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null) {
                            if (response.body().getNormalBoxItems() != null && !response.body().getNormalBoxItems().isEmpty()) {
                                ArrayList<UserItem> userItems = new ArrayList<UserItem>();
                                categories.add(response.body().getSelectedCategory());
                                categories.addAll(response.body().getRestCategories());
                                searchDetailAdapter.setBoxItems(new ArrayList<BoxItem>(response.body().getNormalBoxItems()), new ArrayList<UserItem>(response.body().getMyBoxItems()), categories);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryBoxItemsResponse> call, Throwable t) {
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
