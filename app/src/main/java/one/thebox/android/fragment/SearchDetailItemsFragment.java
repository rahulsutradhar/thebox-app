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
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.api.RequestBodies.SearchDetailResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchDetailItemsFragment extends Fragment {

    private static final String EXTRA_QUERY = "extra_query";
    private static final String EXTRA_CAT_ID = "extra_cat_id";
    private static final String EXTRA_SOURCE = "extra_source";
    private static final String EXTRA_USER_ITEM_ARRAY_LIST = "extra_user_item_array_list";
    private static final String EXTRA_BOX_ITEM_ARRAY_LIST = "extra_box_item_array_list";
    private static final String EXTRA_POSITION_IN_VIEW_PAGER = "extra_position_of_fragment_in_tab";
    private static final int SOURCE_NON_CATEGORY = 0;
    private static final int SOURCE_CATEGORY = 1;
    private static final int SOURCE_SEARCH = 2;
    private View rootView;
    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private LinearLayout linearLayoutHolder;
    private GifImageView progressBar;
    private String query;
    private int catId;
    private RealmList<Category> categories = new RealmList<>();
    private RealmList<UserItem> userItems = new RealmList<>();
    private RealmList<BoxItem> boxItems = new RealmList<>();
    private int source;
    private TextView emptyText;
    private int positionInViewPager;
    private ConnectionErrorViewHelper connectionErrorViewHelper;

    public SearchDetailItemsFragment() {
    }

    public static SearchDetailItemsFragment getInstance(SearchResult searchResult, int positionInViewPager) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_QUERY, searchResult.getResult());
        bundle.putInt(EXTRA_CAT_ID, searchResult.getId());
        if (searchResult.getId() == 0) {
            bundle.putInt(EXTRA_SOURCE, SOURCE_NON_CATEGORY);
        } else {
            bundle.putInt(EXTRA_SOURCE, SOURCE_CATEGORY);
        }
        bundle.putInt(EXTRA_POSITION_IN_VIEW_PAGER, positionInViewPager);
        bundle.putInt(EXTRA_SOURCE, SOURCE_CATEGORY);
        SearchDetailItemsFragment searchDetailItemsFragment = new SearchDetailItemsFragment();
        searchDetailItemsFragment.setArguments(bundle);
        return searchDetailItemsFragment;
    }

    public static SearchDetailItemsFragment getInstance(Context activity, ArrayList<UserItem> userItems, ArrayList<BoxItem> boxItems, int positionInViewPager) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SOURCE, SOURCE_CATEGORY);
        bundle.putInt(EXTRA_SOURCE, SOURCE_SEARCH);
        bundle.putString(EXTRA_USER_ITEM_ARRAY_LIST, CoreGsonUtils.toJson(userItems));
        bundle.putString(EXTRA_BOX_ITEM_ARRAY_LIST, CoreGsonUtils.toJson(boxItems));
        bundle.putInt(EXTRA_POSITION_IN_VIEW_PAGER, positionInViewPager);
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

        if (rootView == null) {
            initVariables();
            rootView = inflater.inflate(R.layout.fragment_search_detail_items, container, false);
            initViews();
            switch (source) {
                case SOURCE_CATEGORY: {
                    getCategoryDetail();
                    break;
                }
                case SOURCE_NON_CATEGORY: {
                    getSearchDetails();
                    break;
                }
                case SOURCE_SEARCH: {
                    setupRecyclerView();
                    break;
                }
            }
        }
        return rootView;
    }

    private void initVariables() {
        source = getArguments().getInt(EXTRA_SOURCE);
        query = getArguments().getString(EXTRA_QUERY);
        catId = getArguments().getInt(EXTRA_CAT_ID);
        userItems = CoreGsonUtils.fromJsontoRealmList(getArguments().getString(EXTRA_USER_ITEM_ARRAY_LIST), UserItem.class);
        boxItems = CoreGsonUtils.fromJsontoRealmList(getArguments().getString(EXTRA_BOX_ITEM_ARRAY_LIST), BoxItem.class);
        positionInViewPager = getArguments().getInt(EXTRA_POSITION_IN_VIEW_PAGER);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        linearLayoutHolder = (LinearLayout) rootView.findViewById(R.id.holder_linear_layout);
        progressBar = (GifImageView) rootView.findViewById(R.id.progress_bar);
        emptyText = (TextView) rootView.findViewById(R.id.empty_text);
        connectionErrorViewHelper = new ConnectionErrorViewHelper(rootView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (source) {
                    case SOURCE_CATEGORY: {
                        getCategoryDetail();
                        break;
                    }
                    case SOURCE_NON_CATEGORY: {
                        getSearchDetails();
                        break;
                    }
                    case SOURCE_SEARCH: {
                        setupRecyclerView();
                        break;
                    }
                }
            }
        });
    }

    private void setupRecyclerView() {
        linearLayoutHolder.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (boxItems.isEmpty() && userItems.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        }
        searchDetailAdapter = new SearchDetailAdapter(getActivity());
        searchDetailAdapter.setPositionInViewPager(positionInViewPager);
        searchDetailAdapter.setBoxItems(boxItems, userItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchDetailAdapter);
    }

    private void getSearchDetails() {
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        MyApplication.getAPIService().getSearchResults(PrefUtils.getToken(getActivity()), query)
                .enqueue(new Callback<SearchDetailResponse>() {
                    @Override
                    public void onResponse(Call<SearchDetailResponse> call, Response<SearchDetailResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        if (response.body() != null) {
                            userItems.add(response.body().getMySearchItem());
                            userItems.addAll(response.body().getMyNonSearchedItems());
                            boxItems.add(response.body().getSearchedItem());
                            boxItems.addAll(response.body().getNormalItems());
                            categories.add(response.body().getSearchedCategory());
                            categories.addAll(response.body().getRestCategories());
                            setupRecyclerView();
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchDetailResponse> call, Throwable t) {
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });
    }

    private void getCategoryDetail() {
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);
        MyApplication.getAPIService().getCategoryBoxItems(PrefUtils.getToken(getActivity()), catId)
                .enqueue(new Callback<CategoryBoxItemsResponse>() {
                    @Override
                    public void onResponse(Call<CategoryBoxItemsResponse> call, Response<CategoryBoxItemsResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
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
                        emptyText.setText(t.toString());
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });
    }
}
