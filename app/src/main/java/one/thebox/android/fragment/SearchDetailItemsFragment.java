package one.thebox.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import one.thebox.android.Events.ShowSpecialCardEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.ViewHelper.EndlessRecyclerViewScrollListener;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.api.RequestBodies.SearchDetailResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.api.Responses.category.BoxCategoryItemResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
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
    private static final int SOURCE_BOX_CATEGORY = 1;
    private static final int SOURCE_SEARCH = 2;
    private static final int PER_PAGE_ITEMS = 10;
    private int pageNumber = 1;
    private View rootView;
    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private LinearLayout linearLayoutHolder;
    private GifImageView progressBar;
    private String query;
    private int catId = -1;
    private List<SubscribeItem> subscribeItems = new ArrayList<>();
    private List<BoxItem> boxItems = new ArrayList<>();
    private int source;
    private TextView emptyText;

    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private int totalItems;
    private int maxPageNumber;

    private Category category;
    private int positionInViewPager;
    private RealmList<Category> suggestedCategories = new RealmList<>();

    private List<BoxItem> cartItems;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    public SearchDetailItemsFragment() {
    }


    /**
     * Called from Box Category; Source Category
     *
     * @param category
     * @return
     */
    public static SearchDetailItemsFragment getInstance(Category category, List<Category> categories, int positionInViewPager) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_CATEGORY, CoreGsonUtils.toJson(category));
        bundle.putInt(Constants.EXTRA_CLICK_POSITION, positionInViewPager);
        bundle.putString(Constants.EXTRA_CATEGORY_LIST, CoreGsonUtils.toJson(categories));
        bundle.putInt(EXTRA_SOURCE, SOURCE_BOX_CATEGORY);
        SearchDetailItemsFragment searchDetailItemsFragment = new SearchDetailItemsFragment();
        searchDetailItemsFragment.setArguments(bundle);
        return searchDetailItemsFragment;
    }


    /**
     * OLD
     */
    public static SearchDetailItemsFragment getInstance(SearchResult searchResult, int positionInViewPager) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_QUERY, searchResult.getResult());
        bundle.putInt(EXTRA_CAT_ID, searchResult.getId());
        if (searchResult.getId() == 0) {
            bundle.putInt(EXTRA_SOURCE, SOURCE_NON_CATEGORY);
        } else {
            bundle.putInt(EXTRA_SOURCE, SOURCE_BOX_CATEGORY);
        }
        bundle.putInt(EXTRA_POSITION_IN_VIEW_PAGER, positionInViewPager);
        bundle.putInt(EXTRA_SOURCE, SOURCE_BOX_CATEGORY);
        SearchDetailItemsFragment searchDetailItemsFragment = new SearchDetailItemsFragment();
        searchDetailItemsFragment.setArguments(bundle);
        return searchDetailItemsFragment;
    }

    public static SearchDetailItemsFragment getInstance(Context activity, ArrayList<UserItem> userItems, ArrayList<BoxItem> boxItems, int positionInViewPager) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SOURCE, SOURCE_BOX_CATEGORY);
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
            rootView = inflater.inflate(R.layout.fragment_search_detail_items, container, false);
            initVariables();
            initViews();

            //request Server to get the Boc Catgeory Items
            getBoxCategoryItems();

            //getDataBasedOnSource();
        }
        return rootView;
    }

    public void initDataChangeListener() {
        Realm realm = TheBox.getRealm();
        realm.addChangeListener(realmListener);
    }

    private RealmChangeListener realmListener = new RealmChangeListener() {

        @Override
        public void onChange(Object element) {
            Log.d("RealmChanged", "RealmData Changed");
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fillAllUserItems();
                    }
                });
            }
        }
    };

    private boolean isLoading;
    EndlessRecyclerViewScrollListener listener = new EndlessRecyclerViewScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            if (boxItems != null) {
                int boxItemCount = boxItems.size();
                if (boxItemCount < totalItems && !isLoading && pageNumber < maxPageNumber) {
                    pageNumber++;
                    getDataBasedOnSource();
                }
            }
        }
    };

    private void getDataBasedOnSource() {
        switch (source) {
            case SOURCE_BOX_CATEGORY: {
                //request server to get Box Category items
                getBoxCategoryItems();

                // getCategoryDetail();
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

    private void fillAllUserItems() {
        if (catId != -1) {
            Realm realm = TheBox.getRealm();
            RealmResults<UserItem> realmResults = realm.where(UserItem.class).equalTo("boxItem.categoryId", catId).findAll();
            setBoxItemsBasedOnUserItems(realmResults, boxItems);
        } else {
            getDataBasedOnSource();
        }
    }

    private RealmChangeManager.DBChangeListener boxItemChangeListener = new RealmChangeManager.DBChangeListener() {
        @Override
        public void onDBChanged() {
            fillAllUserItems();
        }
    };

    public void initVariables() {
        try {
            category = CoreGsonUtils.fromJson(getArguments().getString(Constants.EXTRA_CATEGORY), Category.class);
            suggestedCategories = CoreGsonUtils.fromJsontoRealmList(getArguments().getString(Constants.EXTRA_CATEGORY_LIST), Category.class);
            positionInViewPager = getArguments().getInt(Constants.EXTRA_CLICK_POSITION);
            source = getArguments().getInt(EXTRA_SOURCE);
            cartItems = CartHelper.getCart();

            updateSuggestionCatgeoryListBasedOnSelectedCategory();

            //old
            query = getArguments().getString(EXTRA_QUERY);
            catId = getArguments().getInt(EXTRA_CAT_ID);
            //userItems = CoreGsonUtils.fromJsontoRealmList(getArguments().getString(EXTRA_USER_ITEM_ARRAY_LIST), UserItem.class);
            boxItems = CoreGsonUtils.fromJsontoRealmList(getArguments().getString(EXTRA_BOX_ITEM_ARRAY_LIST), BoxItem.class);


        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    /**
     * Set Suggestion Category List based on Selected Category
     */
    public void updateSuggestionCatgeoryListBasedOnSelectedCategory() {

        for (int i = 0; i < suggestedCategories.size(); i++) {
            if (suggestedCategories.get(i).getUuid().equalsIgnoreCase(category.getUuid())) {
                suggestedCategories.remove(i);
                break;
            }
        }
    }

    private void initViews() {
        this.glideRequestManager = Glide.with(this);
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
                    case SOURCE_BOX_CATEGORY: {
                        getBoxCategoryItems();
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
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {

                    EventBus.getDefault().post(new ShowSpecialCardEvent(false));
                }
            }
        });
    }

    private void setupRecyclerView() {
        linearLayoutHolder.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (boxItems.isEmpty() && subscribeItems.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }

        if (searchDetailAdapter == null) {
            searchDetailAdapter = new SearchDetailAdapter(getActivity(), glideRequestManager);
            searchDetailAdapter.setPositionInViewPager(positionInViewPager);
            searchDetailAdapter.setSuggestedCategories(suggestedCategories);
            //check with cart item and evaluate data
            setData();
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(manager);
            listener.setmLayoutManager(manager);
            recyclerView.setAdapter(searchDetailAdapter);
            recyclerView.addOnScrollListener(listener);
            searchDetailAdapter.setCalledFromSearchDetailItem(true);
        }
    }

    /**
     * Set the refined list after matching with cart data
     */
    public void setData() {
        if (cartItems.size() > 0) {
            for (int i = 0; i < boxItems.size(); i++) {
                BoxItem boxItem = boxItems.get(i);
                int pos = isBoxItemInCart(boxItem);
                if (pos != -1) {
                    BoxItem cartItem = cartItems.get(pos);
                    boxItem.setQuantity(cartItem.getQuantity());
                    boxItem.setSelectedItemConfig(cartItem.getSelectedItemConfig());
                    boxItems.set(i, boxItem);
                }
            }
            searchDetailAdapter.setBoxItems(boxItems, subscribeItems);
        } else {
            searchDetailAdapter.setBoxItems(boxItems, subscribeItems);
        }
    }

    /**
     * Check if item is in Cart
     */
    public int isBoxItemInCart(BoxItem boxItem) {
        int pos = -1;
        for (int i = 0; i < cartItems.size(); i++) {
            if (boxItem.getUuid().equalsIgnoreCase(cartItems.get(i).getUuid())) {
                if (!boxItem.isInStock()) {
                    CartHelper.removeItemFromCart(boxItem);
                } else {
                    if (ifItemConfigInStock(boxItem, cartItems.get(i).getSelectedItemConfig())) {
                        pos = i;
                    } else {
                        CartHelper.removeItemFromCart(boxItem);
                    }
                }
                break;
            }
        }
        return pos;
    }

    /**
     * Check if Item Config is in Stock
     */
    public boolean ifItemConfigInStock(BoxItem boxItem, ItemConfig selectedItemConfig) {
        boolean flag = false;
        for (ItemConfig itemConfig : boxItem.getItemConfigs()) {
            if (selectedItemConfig.getUuid().equalsIgnoreCase(itemConfig.getUuid())) {
                if (itemConfig.isInStock()) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }


    private void getSearchDetails() {
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        TheBox.getAPIService().getSearchResults(PrefUtils.getToken(getActivity()), query)
                .enqueue(new Callback<SearchDetailResponse>() {
                    @Override
                    public void onResponse(Call<SearchDetailResponse> call, Response<SearchDetailResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        if (response.body() != null) {
                            clearData();
                            //userItems.add(response.body().getMySearchItem());
                            //userItems.addAll(response.body().getMyNonSearchedItems());
                            boxItems.add(response.body().getSearchedItem());
                            boxItems.addAll(response.body().getNormalItems());
                            // categories.add(response.body().getSearchedCategory());
                            //categories.addAll(response.body().getRestCategories());
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

    private void clearData() {
        //  userItems.clear();
        boxItems.clear();
        //categories.clear();
    }


    /**
     * Request Server to get Box Category Items
     */
    private void getBoxCategoryItems() {
        isLoading = true;
        linearLayoutHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);
        TheBox.getAPIService()
                .getCategoryItem(PrefUtils.getToken(getActivity()), category.getUuid())
                .enqueue(new Callback<BoxCategoryItemResponse>() {
                    @Override
                    public void onResponse(Call<BoxCategoryItemResponse> call, Response<BoxCategoryItemResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    if (response.body().getCategory() != null) {
                                        boxItems = response.body().getCategory().getBoxItems();
                                        setupRecyclerView();
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BoxCategoryItemResponse> call, Throwable t) {
                        isLoading = false;
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });

    }


    private void getCategoryDetail() {
        isLoading = true;
        if (pageNumber == 1) {
            linearLayoutHolder.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            connectionErrorViewHelper.isVisible(false);
        }
        TheBox.getAPIService().getItems(PrefUtils.getToken(getActivity()), catId, pageNumber, PER_PAGE_ITEMS)
                .enqueue(new Callback<CategoryBoxItemsResponse>() {
                    @Override
                    public void onResponse(Call<CategoryBoxItemsResponse> call, Response<CategoryBoxItemsResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        isLoading = false;
                        if (response.body() != null) {
                            if (pageNumber == 1) {
                                clearData();
                            }

                            CategoryBoxItemsResponse categoryBoxItemsResponse = response.body();
                            totalItems = categoryBoxItemsResponse.getTotalCount();
                            maxPageNumber = (totalItems % PER_PAGE_ITEMS) > 0 ? (totalItems / PER_PAGE_ITEMS) + 1 : (totalItems / PER_PAGE_ITEMS);

                            if (categoryBoxItemsResponse.getNormalBoxItems() != null) {
                                boxItems.addAll(response.body().getNormalBoxItems());
                            }
                            setBoxItemsBasedOnUserItems(response.body().getMyBoxItems(), boxItems);

                            if (null != response.body().getSelectedCategory()) {
                                //  categories.add(response.body().getSelectedCategory());
                            }
                            if (null != response.body().getRestCategories()) {
                                // categories.addAll(response.body().getRestCategories());
                            }
                            initDataChangeListener();

                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryBoxItemsResponse> call, Throwable t) {
                        isLoading = false;
                        emptyText.setText(t.toString());
                        linearLayoutHolder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });
    }

    private void setBoxItemsBasedOnUserItems(List<UserItem> items, List<BoxItem> bItems) {
        try {
            if (items == null && catId != -1) {
                Realm realm = TheBox.getRealm();
                items = realm.where(UserItem.class).equalTo("boxItem.categoryId", catId).findAll();
            }
            LinkedHashMap<Integer, BoxItem> map = new LinkedHashMap<>();
            LinkedHashMap<Integer, BoxItem> mapCart = new LinkedHashMap<>();

            //boxitem maps
            for (BoxItem item : bItems) {
                map.put(item.getId(), item);
            }
            //  userItems.clear();
            for (UserItem item : items) {

                //Item subscribed and has devilvery scheduled
                if (!TextUtils.isEmpty(item.getNextDeliveryScheduledAt())) {
                    // userItems.add(item);
                }// item not subscribed but is in cart; no delivery scheduled
                else if (item.getStillSubscribed()) {

                    BoxItem boxItem = item.getBoxItem();
                    if (mapCart.containsKey(boxItem.getId())) {
                        BoxItem box = mapCart.get(boxItem.getId());
                        box.setUserItemId(item.getId());
                        box.setQuantity(item.getQuantity());
                        box.setSelectedItemConfig(box.getItemConfigById(item.getSelectedConfigId()));
                        box.setSavingsTitle(item.getSelectedItemConfigSavingsTitle());
                        mapCart.put(box.getId(), box);
                    } else {
                        boxItem.setUserItemId(item.getId());
                        boxItem.setQuantity(item.getQuantity());
                        boxItem.setSelectedItemConfig(boxItem.getItemConfigById(item.getSelectedConfigId()));
                        boxItem.setSavingsTitle(item.getSelectedItemConfigSavingsTitle());
                        mapCart.put(boxItem.getId(), boxItem);
                    }

                }
            }
            boxItems.clear();
            //display all cart item on top
            if (mapCart != null) {
                boxItems.addAll(mapCart.values());
            }
            //normal items
            if (map != null) {
                boxItems.addAll(map.values());
            }
            setupRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
