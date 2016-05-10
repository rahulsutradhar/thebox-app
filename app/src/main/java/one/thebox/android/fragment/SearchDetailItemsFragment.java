package one.thebox.android.fragment;


import android.content.Context;
import android.os.Bundle;
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
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.api.RequestBodies.SearchDetailResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchDetailItemsFragment extends Fragment {

    private static final String EXTRA_QUERY = "extra_query";
    private static final String EXTRA_CAT_ID = "extra_cat_id";
    private static final String EXTRA_USER_ITEM_ARRAY_LIST = "extra_user_item_array_list";
    private static final String EXTRA_BOX_ITEM_ARRAY_LIST = "extra_box_item_array_list";
    private View rootView;
    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private LinearLayout linearLayoutHolder;
    private ProgressBar progressBar;
    private String query;
    private int catId;
    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<UserItem> userItems = new ArrayList<>();
    private ArrayList<BoxItem> boxItems = new ArrayList<>();

    public SearchDetailItemsFragment() {
    }

    public static SearchDetailItemsFragment getInstance(SearchResult searchResult) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_QUERY, searchResult.getResult());
        bundle.putInt(EXTRA_CAT_ID, searchResult.getId());
        SearchDetailItemsFragment searchDetailItemsFragment = new SearchDetailItemsFragment();
        searchDetailItemsFragment.setArguments(bundle);
        return searchDetailItemsFragment;
    }

    public static SearchDetailItemsFragment getInstance(Context activity, ArrayList<UserItem> userItems, ArrayList<BoxItem> boxItems) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USER_ITEM_ARRAY_LIST, CoreGsonUtils.toJson(userItems));
        bundle.putString(EXTRA_BOX_ITEM_ARRAY_LIST, CoreGsonUtils.toJson(boxItems));
        SearchDetailItemsFragment searchDetailItemsFragment = new SearchDetailItemsFragment();
        searchDetailItemsFragment.setArguments(bundle);
        return searchDetailItemsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initVariables();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search_detail_items, container, false);
            initViews();
            if ((userItems == null || userItems.isEmpty()) && (boxItems == null || boxItems.isEmpty())) {
                if (catId == 0) {
                    getSearchDetails();
                } else {
                    getCategoryDetail();
                }
            } else {
                setupRecyclerView();
            }
        }
        return rootView;

    }

    private void initVariables() {
        query = getArguments().getString(EXTRA_QUERY);
        catId = getArguments().getInt(EXTRA_CAT_ID);
        userItems = CoreGsonUtils.fromJsontoArrayList(getArguments().getString(EXTRA_USER_ITEM_ARRAY_LIST), UserItem.class);
        boxItems = CoreGsonUtils.fromJsontoArrayList(getArguments().getString(EXTRA_BOX_ITEM_ARRAY_LIST), BoxItem.class);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        linearLayoutHolder = (LinearLayout) rootView.findViewById(R.id.holder_linear_layout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        linearLayoutHolder.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (getActivity() == null) {
            return;
        }
        searchDetailAdapter = new SearchDetailAdapter(getActivity());
        searchDetailAdapter.setBoxItems(boxItems, userItems);
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

                        if (response.body() != null) {
                            if (response.body().getSearchedItems() != null && !response.body().getSearchedItems().isEmpty()) {
                                userItems.addAll(response.body().getMySearchItems());
                                userItems.addAll(response.body().getMyNonSearchedItems());
                                boxItems.addAll(response.body().getSearchedItems());
                                categories.add(response.body().getSearchedCategory());
                                categories.addAll(response.body().getRestCategories());
                                setupRecyclerView();
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

                        if (response.body() != null) {
                            userItems.addAll(response.body().getMyBoxItems());
                            boxItems.addAll(response.body().getNormalBoxItems());
                            categories.add(response.body().getSelectedCategory());
                            categories.addAll(response.body().getRestCategories());
                            setupRecyclerView();
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