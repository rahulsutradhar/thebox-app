package one.thebox.android.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Events.ShowSpecialCardEvent;
import one.thebox.android.Events.ShowTabTutorialEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.ViewHelper.ShowcaseHelper;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RequestBodies.SearchDetailResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.api.Responses.ExploreBoxResponse;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchDetailFragment extends BaseFragment implements AppBarObserver.OnOffsetChangeListener {

    public static final String EXTRA_MY_BOX_CATEGORIES_ID = "my_box_category_click_event";
    public static final String EXTRA_MY_BOX_USER_CATEGORIES_ID = "my_box_user_category_click_event";
    public static final String EXTRA_CLICK_POSITION = "extra_click_position";
    public static final String BOX_NAME = "box_name";
    public static final String EXTRA_CLICKED_CATEGORY_ID = "extra_clicked_category";
    public static final String TRIGERED_FROM = "trigered_from";
    private static final String EXTRA_QUERY = "extra_query";
    private static final String EXTRA_CAT_ID = "extra_cat_id";
    private static final String EXTRA_EXPLORE_ITEM = "extra_explore_item";
    public static final String EXTRA_NUMBER_OF_TABS = "extra_number_of_tabs";
    public static final String BROADCAST_EVENT_TAB = "broadcast_event_tab";
    private String boxName;
    private String query;
    private int catId;
    private View rootView;
    private LinearLayout linearLayoutHolder;
    private GifImageView progressBar;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Category> categories = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<UserItem> userItems = new ArrayList<>();
    private ArrayList<BoxItem> boxItems = new ArrayList<>();
    private ExploreItem exploreItem;
    private ArrayList<Integer> catIds = new ArrayList<>();
    private ArrayList<Integer> user_catIds = new ArrayList<>();

    private int clickPosition, clickedCategoryId;
    private TextView noResultFound, itemsInCart, savings;
    //    private FrameLayout fabHolder;
    private CardView specialCardView;
    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private int source;
    public static int POSITION_OF_VIEW_PAGER;
    private boolean previousScrollAction = false;
    private int noOfTabs;
    private String title;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity() == null) {
                return;
            }
            int temp = noOfTabs;
            noOfTabs = intent.getIntExtra(EXTRA_NUMBER_OF_TABS, 0);
            if (noOfTabs > 0) {
                if (temp != noOfTabs)
                    showSpecialCardEvent(new ShowSpecialCardEvent(true));
            } else {
            }
        }
    };

    public SearchDetailFragment() {
        // Required empty public constructor
    }

    public static SearchDetailFragment getInstance(ArrayList<Integer> catIds, ArrayList<Integer> user_catIds, int clickPosition, String boxName, int clickedCategoryId) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CLICK_POSITION, clickPosition);
        bundle.putString(EXTRA_MY_BOX_CATEGORIES_ID, CoreGsonUtils.toJson(catIds));
        bundle.putString(EXTRA_MY_BOX_USER_CATEGORIES_ID, CoreGsonUtils.toJson(user_catIds));
        bundle.putString(BOX_NAME, boxName);
        bundle.putInt(EXTRA_CLICKED_CATEGORY_ID, clickedCategoryId);
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

    /**
     * Call for Notification Click
     */
    public static SearchDetailFragment getInstance(int catId, String boxName) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CAT_ID, catId);
        bundle.putString(BOX_NAME, boxName);
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    private void initVariables() {
        try {
            query = getArguments().getString(EXTRA_QUERY);
            catId = getArguments().getInt(EXTRA_CAT_ID);
            title = getArguments().getString(BOX_NAME);
            exploreItem = CoreGsonUtils.fromJson(getArguments().getString(EXTRA_EXPLORE_ITEM), ExploreItem.class);
            catIds = CoreGsonUtils.fromJsontoArrayList(getArguments().getString(EXTRA_MY_BOX_CATEGORIES_ID), Integer.class);//Null Pointer Here
            user_catIds = CoreGsonUtils.fromJsontoArrayList(getArguments().getString(EXTRA_MY_BOX_USER_CATEGORIES_ID), Integer.class);//Null Pointer Here
            clickPosition = getArguments().getInt(EXTRA_CLICK_POSITION);
            clickedCategoryId = getArguments().getInt(EXTRA_CLICKED_CATEGORY_ID);
            if ((!catIds.isEmpty()) || (!user_catIds.isEmpty())) {
                setCategories();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCategories() {
        Realm realm = TheBox.getRealm();
        RealmQuery<Category> query = realm.where(Category.class).notEqualTo(Category.FIELD_ID, 0);
        for (int i = 0; i < catIds.size(); i++) {
            if (catIds.size() - 1 == i) {
                query.equalTo(Category.FIELD_ID, catIds.get(i));
            } else {
                query.equalTo(Category.FIELD_ID, catIds.get(i)).or();
            }
        }
        if (catIds.size() != 0) {
            RealmResults<Category> realmResults = query.findAll();
            for (Category category : realmResults) {
                categories.add(category);

                if (clickedCategoryId != -1) {
                    if (clickedCategoryId == category.getId()) {
                        clickPosition = categories.size() - 1;
                    }
                }
            }
        }
        RealmQuery<Category> query_user_cat = realm.where(Category.class).notEqualTo(Category.FIELD_ID, 0);
        for (int i = 0; i < user_catIds.size(); i++) {
            if (user_catIds.size() - 1 == i) {
                query_user_cat.equalTo(Category.FIELD_ID, user_catIds.get(i));
            } else {
                query_user_cat.equalTo(Category.FIELD_ID, user_catIds.get(i)).or();
            }
        }
        if (user_catIds.size() != 0) {
            RealmResults<Category> realmResults_user_cat = query_user_cat.findAll();
            for (Category category : realmResults_user_cat) {
                categories.add(category);

                if (clickedCategoryId != -1) {
                    if (clickedCategoryId == category.getId()) {
                        clickPosition = categories.size() - 1;
                    }
                }
            }
        }
    }

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
        }
    };

    private void initViews() {
        linearLayoutHolder = (LinearLayout) rootView.findViewById(R.id.holder);
        progressBar = (GifImageView) rootView.findViewById(R.id.progress_bar);
//        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
//        floatingActionButton.setOnClickListener(fabClickListener);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
//        numberOfItemsInCart = (TextView) rootView.findViewById(R.id.no_of_items_in_cart);
//        fabHolder = (FrameLayout) rootView.findViewById(R.id.fab_holder);
        onTabEvent(new TabEvent(CartHelper.getNumberOfItemsInCart()));
        connectionErrorViewHelper = new ConnectionErrorViewHelper(rootView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (source) {
                    case 0: {
                        getSearchDetails();
                        break;
                    }
                    case 1: {
                        getCategoryDetail();
                        break;
                    }
                    case 2: {
                        getExploreDetails();
                        break;
                    }
                }
            }
        });
        noResultFound = (TextView) rootView.findViewById(R.id.no_result_found);
        specialCardView = (CardView) rootView.findViewById(R.id.special_card);
        specialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
            }
        });
        itemsInCart = (TextView) rootView.findViewById(R.id.items_in_cart);
        savings = (TextView) rootView.findViewById(R.id.savings);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initVariables();

        if ((!catIds.isEmpty()) || (!user_catIds.isEmpty())) {
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
            setupAppBarObserver();

            if ((!catIds.isEmpty()) || (!user_catIds.isEmpty())) {
                setupViewPagerAndTabsMyBox();
            } else {
                if (exploreItem == null) {
                    if (catId == 0) {
                        source = 0;
                        getSearchDetails();
                    } else {
                        source = 1;
                        getCategoryDetail();
                    }
                } else {
                    source = 2;
                    getExploreDetails();
                }
            }
        }


        return rootView;
    }

    private void setupViewPagerAndTabsMyBox() {
        try {
            if (getActivity() == null) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            final ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
            for (int i = 0; i < categories.size(); i++) {
                adapter.addFragment(SearchDetailItemsFragment.getInstance(new SearchResult(categories.get(i).getId(), categories.get(i).getTitle()), i), categories.get(i));
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
            POSITION_OF_VIEW_PAGER = clickPosition;
            ExploreItem.setDefaultPositionOfViewPager(getArguments().getString(BOX_NAME), clickPosition);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    POSITION_OF_VIEW_PAGER = position;
                    ExploreItem.setDefaultPositionOfViewPager(getArguments().getString(BOX_NAME), position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            /**
             * Save CleverTap Event; BrowseCategory
             */
            setCleverTapEventBrowseCategory(categories.get(POSITION_OF_VIEW_PAGER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPagerAndTabs() {
        if (getActivity() == null) {
            return;
        }

        if (title != null) {
            ((MainActivity) getActivity()).getToolbar().setTitle(title);
        } else {
            ((MainActivity) getActivity()).getToolbar().setSubtitle(boxName);
        }

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        adapter.addFragment(SearchDetailItemsFragment.getInstance(getActivity(), userItems, boxItems, 0), categories.get(0));
        for (int i = 1; i < categories.size(); i++) {
            adapter.addFragment(SearchDetailItemsFragment.getInstance(new SearchResult(categories.get(i).getId(), categories.get(i).getTitle()), i), categories.get(i));
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        int length = tabLayout.getTabCount();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), true));
                try {
                    Field field = tab.getClass().getField("mView");
                    field.setAccessible(true);
                    View view = null;
                    view = (View) field.get(view);
                    final View finalView = view;
                    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                        }
                    });
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (exploreItem != null) {
                    exploreItem.setDefaultPositionOfViewPager(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (exploreItem != null) {
            viewPager.setCurrentItem(exploreItem.getDefaultPositionOfViewPager());
        }
    }

    public void getSearchDetails() {
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);

        TheBox.getAPIService().getSearchResults(PrefUtils.getToken(getActivity()), query)
                .enqueue(new Callback<SearchDetailResponse>() {
                    @Override
                    public void onResponse(Call<SearchDetailResponse> call, Response<SearchDetailResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        if (response.body() != null) {
                            boxName = response.body().getBoxName();

                            setBoxItemBasedOnSearch(response.body());
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

    public void setBoxItemBasedOnSearch(SearchDetailResponse response) {
        try {
            /**
             * Display the searched Item first
             */
            //searched Item if User Item
            setUserItems(response.getMySearchItem());

            //search Item if Box Item
            if (response.getSearchedItem().getId() != 0) {
                boxItems.add(response.getSearchedItem());
            }

            //set Subscribed item
            for (UserItem item : response.getMyNonSearchedItems()) {
                setUserItems(item);
            }

            //normal items
            boxItems.addAll(response.getNormalItems());

            categories.add(response.getSearchedCategory());
            categories.addAll(response.getRestCategories());

            setupViewPagerAndTabs();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * If Cart Item add it to boxItems
     * else add it to user items
     */
    public void setUserItems(UserItem userItem) {

        try {
            if (userItem != null) {
                if (userItem.getId() != 0) {
                    //subscribed Item
                    if (!TextUtils.isEmpty(userItem.getNextDeliveryScheduledAt())) {
                        userItems.add(userItem);
                    }//cart items
                    else if (userItem.getStillSubscribed()) {

                        BoxItem boxItem = userItem.getBoxItem();
                        boxItem.setUserItemId(userItem.getId());
                        boxItem.setQuantity(userItem.getQuantity());
                        boxItem.setSelectedItemConfig(boxItem.getItemConfigById(userItem.getSelectedConfigId()));
                        boxItem.setSavingsTitle(userItem.getSelectedItemConfigSavingsTitle());

                        boxItems.add(boxItem);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getCategoryDetail() {
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);

        TheBox.getAPIService().getCategoryBoxItems(PrefUtils.getToken(getActivity()), catId)
                .enqueue(new Callback<CategoryBoxItemsResponse>() {
                    @Override
                    public void onResponse(Call<CategoryBoxItemsResponse> call, Response<CategoryBoxItemsResponse> response) {
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(false);

                        if (response.body() != null) {
                            boxName = response.body().getBoxName();
                            title = response.body().getBoxName();

                            for (UserItem item : response.body().getMyBoxItems()) {
                                setUserItems(item);
                            }
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
                        connectionErrorViewHelper.isVisible(true);
                    }
                });
    }

    public void getExploreDetails() {
        connectionErrorViewHelper.isVisible(false);
        title = exploreItem.getTitle();
        TheBox.getAPIService().getExploreBox(PrefUtils.getToken(getActivity()), exploreItem.getId())
                .enqueue(new Callback<ExploreBoxResponse>() {
                    @Override
                    public void onResponse(Call<ExploreBoxResponse> call, Response<ExploreBoxResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(false);
                        if (response.body() != null) {

                            for (UserItem item : response.body().getMyItems()) {
                                setUserItems(item);
                            }
                            boxItems.addAll(response.body().getNormalItems());

                            categories.add(response.body().getSelectedCategory());
                            categories.addAll(response.body().getRestCategories());
                            setupViewPagerAndTabs();
                        }
                    }

                    @Override
                    public void onFailure(Call<ExploreBoxResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(false);
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
        ((MainActivity) getActivity()).getSearchViewHolder().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.VISIBLE);
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

        ((MainActivity) getActivity()).getSearchAction().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getSearchAction().setImageResource(R.drawable.menu_icon);
        ((MainActivity) getActivity()).getSearchAction().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);
            }
        });


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_EVENT_TAB));
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
        specialCardView.setTranslationY(-offset);
    }

    private void setupAppBarObserver() {
        AppBarObserver appBarObserver;
        Activity activity = getActivity();
        AppBarLayout appBarLayout = (AppBarLayout) activity
                .findViewById(R.id.app_bar_layout);
        if (appBarLayout != null) {
            appBarObserver = AppBarObserver.observe(appBarLayout);
            appBarObserver.addOffsetChangeListener(this);
        }
    }

    @Subscribe
    public void OnShowTabTutorialEvent(ShowTabTutorialEvent showTabTutorialEvent) {
        moveViewPager(clickPosition);
        if (!RestClient.is_in_development) {
            new ShowcaseHelper(getActivity(), 4).show("Categories", "Swipe right or left to browse categories. Tap to select", tabLayout)
                    .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
                        @Override
                        public void onComplete() {
//                            new ShowcaseHelper(getActivity(), 5).show("Cart", "All added items in your current session are here in the cart", fabHolder).setShouldBeOnMiddle(true);
                            tabLayout.setScrollPosition(clickPosition, 0, true);
                            shouldMoveMore = false;
                        }
                    });
        }
    }

    @Subscribe
    public void showSpecialCard() {

    }

    private boolean shouldMoveMore = true;

    public void moveViewPager(final int position) {
        if (shouldMoveMore) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(position);
                    if (position >= 1) {
                        moveViewPager(position - 1);
                    } else {
                        moveViewPager(position + 1);
                    }
                }
            }, 1000);
        }
    }

    public void onTabEvent(TabEvent tabEvent) {
        if (getActivity() == null) {
            return;
        }
//        FloatingActionButton mFab = (FloatingActionButton) fabHolder.findViewById(R.id.fab);
//        mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.primary)));
//        mFab.setOnClickListener(fabClickListener);
//        numberOfItemsInCart.setVisibility(View.VISIBLE);
//        numberOfItemsInCart.setText(String.valueOf(tabEvent.getNumberOfItemsInCart()));
    }

    @Subscribe
    public void showSpecialCardEvent(ShowSpecialCardEvent showSpecialCardEvent) {
        if (noOfTabs == 1) {
            itemsInCart.setText("You have " + noOfTabs + " item in your cart");
        } else {
            itemsInCart.setText("You have " + noOfTabs + " items in your cart");
        }
        if (previousScrollAction != showSpecialCardEvent.isVisible()) {
            if (showSpecialCardEvent.isVisible()) {
                specialCardView.setVisibility(View.VISIBLE);
                specialCardView.startAnimation(AnimationUtils.loadAnimation(TheBox.getInstance(), R.anim.passport_options_popup));
//                fabHolder.startAnimation(AnimationUtils.loadAnimation(TheBox.getInstance(), R.anim.passport_options_popdown));

            } else {
                specialCardView.setVisibility(View.GONE);
                specialCardView.startAnimation(AnimationUtils.loadAnimation(TheBox.getInstance(), R.anim.passport_options_popdown));
//                fabHolder.startAnimation(AnimationUtils.loadAnimation(TheBox.getInstance(), R.anim.passport_options_popup));
            }
            previousScrollAction = showSpecialCardEvent.isVisible();
        }
    }

    /**
     * CleverTap Event
     */
    public void setCleverTapEventBrowseCategory(Category category) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("box_id", category.getBoxId());
            hashMap.put("box_title", getArguments().getString(BOX_NAME));
            hashMap.put("category_id", category.getId());
            hashMap.put("category_title", category.getTitle());
            hashMap.put("number_of_items", category.getNoOfItems());

            TheBox.getCleverTap().event.push("browse_category", hashMap);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
