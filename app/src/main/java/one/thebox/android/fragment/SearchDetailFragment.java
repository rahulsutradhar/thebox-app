package one.thebox.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RequestBodies.SearchDetailResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.api.Responses.ExploreBoxResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchDetailFragment extends BaseFragment {

    public static final String EXTRA_MY_BOX_CATEGORIES_ID = "my_box_category_click_event";
    public static final String EXTRA_CLICK_POSITION = "extra_click_position";
    public static final String BOX_NAME = "box_name";
    private static final String EXTRA_QUERY = "extra_query";
    private static final String EXTRA_CAT_ID = "extra_cat_id";
    private static final String EXTRA_EXPLORE_ITEM = "extra_explore_item";
    String boxName;
    private String query;
    private int catId;
    private View rootView;
    private LinearLayout linearLayoutHolder;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Category> categories = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<UserItem> userItems = new ArrayList<>();
    private ArrayList<BoxItem> boxItems = new ArrayList<>();
    private ExploreItem exploreItem;
    private ArrayList<Integer> catIds;
    private int clickPosition;

    public SearchDetailFragment() {
        // Required empty public constructor
    }

    public static SearchDetailFragment getInstance(ArrayList<Integer> catIds, int clickPosition, String boxName) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CLICK_POSITION, clickPosition);
        bundle.putString(EXTRA_MY_BOX_CATEGORIES_ID, CoreGsonUtils.toJson(catIds));
        bundle.putString(BOX_NAME, boxName);
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    public static SearchDetailFragment getInstance(SearchResult searchResult) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_QUERY, searchResult.getResult());
        bundle.putInt(EXTRA_CAT_ID, searchResult.getId());
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    public static SearchDetailFragment getInstance(ExploreItem exploreItem) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_EXPLORE_ITEM, CoreGsonUtils.toJson(exploreItem));
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    private void initVariables() {
        query = getArguments().getString(EXTRA_QUERY);
        catId = getArguments().getInt(EXTRA_CAT_ID);
        exploreItem = CoreGsonUtils.fromJson(getArguments().getString(EXTRA_EXPLORE_ITEM), ExploreItem.class);
        catIds = CoreGsonUtils.fromJsontoArrayList(getArguments().getString(EXTRA_MY_BOX_CATEGORIES_ID), Integer.class);
        clickPosition = getArguments().getInt(EXTRA_CLICK_POSITION);
        if (catIds != null && !catIds.isEmpty()) {
            setCategories();
        }
    }

    private void setCategories() {
        Realm realm = MyApplication.getRealm();
        RealmQuery<Category> query = realm.where(Category.class).notEqualTo(Category.FIELD_ID, 0);
        for (int i = 0; i < catIds.size(); i++) {
            if (catIds.size() - 1 == i) {
                query.equalTo(Category.FIELD_ID, catIds.get(i));
            } else {
                query.equalTo(Category.FIELD_ID, catIds.get(i)).or();
            }
        }
        RealmResults<Category> realmResults = query.findAll();
        for (Category category : realmResults) {
            categories.add(category);
        }
    }

    private void initViews() {
        linearLayoutHolder = (LinearLayout) rootView.findViewById(R.id.holder);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
            }
        });
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initVariables();
        if (catIds != null && !catIds.isEmpty()) {
            ((MainActivity) getActivity()).getToolbar().setTitle(getArguments().getString(BOX_NAME));
        } else {
            if (exploreItem != null) {
                ((MainActivity) getActivity()).getToolbar().setTitle(exploreItem.getTitle());
            } else {
                ((MainActivity) getActivity()).getToolbar().setTitle(query);
            }
        }
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search_detail, container, false);
            initViews();
            if (catIds != null && !catIds.isEmpty()) {
                setupViewPagerAndTabsMyBox();
            } else {
                if (exploreItem == null) {
                    if (catId == 0) {
                        getSearchDetails();
                    } else {
                        getCategoryDetail();
                    }
                } else {
                    getExploreDetails();
                }
            }
        }
        return rootView;
    }

    private void setupViewPagerAndTabsMyBox() {
        if (getActivity() == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        for (int i = 0; i < categories.size(); i++) {
            adapter.addFragment(SearchDetailItemsFragment.getInstance(new SearchResult(categories.get(i).getId(), categories.get(i).getTitle())), categories.get(i));
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        int length = tabLayout.getTabCount();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), true));
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), false));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, true));
            } else {
                tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, false));
            }
        }
        viewPager.setCurrentItem(clickPosition);
    }

    private void setupViewPagerAndTabs() {
        if (getActivity() == null) {
            return;
        }
        ((MainActivity) getActivity()).getToolbar().setSubtitle(boxName);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        adapter.addFragment(SearchDetailItemsFragment.getInstance(getActivity(), userItems, boxItems), categories.get(0));
        for (int i = 1; i < categories.size(); i++) {
            adapter.addFragment(SearchDetailItemsFragment.getInstance(new SearchResult(categories.get(i).getId(), categories.get(i).getTitle())), categories.get(i));
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        int length = tabLayout.getTabCount();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), true));
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), false));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, true));
            } else {
                tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, false));
            }
        }
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
                            boxName = response.body().getBoxName();
                            userItems.addAll(response.body().getMySearchItems());
                            userItems.addAll(response.body().getMyNonSearchedItems());
                            boxItems.addAll(response.body().getSearchedItems());
                            categories.add(response.body().getSearchedCategory());
                            categories.addAll(response.body().getRestCategories());
                            setupViewPagerAndTabs();
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
                            boxName = response.body().getBoxName();
                            userItems.addAll(response.body().getMyBoxItems());
                            boxItems.addAll(response.body().getNormalBoxItems());
                            categories.add(response.body().getSelectedCategory());
                            categories.addAll(response.body().getRestCategories());
                            setupViewPagerAndTabs();
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryBoxItemsResponse> call, Throwable t) {
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void getExploreDetails() {
        MyApplication.getAPIService().getExploreBox(PrefUtils.getToken(getActivity()), exploreItem.getId())
                .enqueue(new Callback<ExploreBoxResponse>() {
                    @Override
                    public void onResponse(Call<ExploreBoxResponse> call, Response<ExploreBoxResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null) {
                            categories.add(response.body().getSelectedCategory());
                            categories.addAll(response.body().getRestCategories());
                            boxItems.addAll(response.body().getNormalItems());
                            userItems.addAll(response.body().getMyItems());
                            setupViewPagerAndTabs();
                        }
                    }

                    @Override
                    public void onFailure(Call<ExploreBoxResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Subscribe
    public void OnCategorySelectEvent(OnCategorySelectEvent onCategorySelectEvent) {
        int tabPosition = getTabPosition(onCategorySelectEvent.getCategory());
        if (tabPosition != -1) {
            viewPager.setCurrentItem(tabPosition);
        } else {
            startActivity(new Intent(getActivity(), MainActivity.class)
                    .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 4)
                    .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_DATA, CoreGsonUtils.toJson(
                            new SearchResult(onCategorySelectEvent.getCategory().getId(), onCategorySelectEvent.getCategory().getTitle()))));
        }
    }

    public int getTabPosition(Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (category.equals(categories.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(boxName);
    }
}
