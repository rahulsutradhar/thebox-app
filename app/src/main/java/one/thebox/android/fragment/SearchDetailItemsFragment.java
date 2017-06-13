package one.thebox.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import one.thebox.android.Events.ShowSpecialCardEvent;
import one.thebox.android.Events.SyncCartItemEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.Category;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.ViewHelper.EndlessRecyclerViewScrollListener;
import one.thebox.android.adapter.SearchDetailAdapter;
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

    private int pageNumber = 1;
    private View rootView;
    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private LinearLayout linearLayoutHolder;
    private GifImageView progressBar;
    private List<SubscribeItem> subscribeItems = new ArrayList<>();
    private List<BoxItem> boxItems = new ArrayList<>();
    private TextView emptyText;

    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private int totalItems;
    private int maxPageNumber;
    private boolean isRegistered;

    private Category category;
    private int positionInViewPager;
    private RealmList<Category> suggestedCategories = new RealmList<>();
    private String searchQuery = "";

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
    public static SearchDetailItemsFragment getInstance(Category category, List<Category> categories, int positionInViewPager, String searchQuery) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_CATEGORY, CoreGsonUtils.toJson(category));
        bundle.putInt(Constants.EXTRA_CLICK_POSITION, positionInViewPager);
        bundle.putString(Constants.EXTRA_CATEGORY_LIST, CoreGsonUtils.toJson(categories));
        bundle.putString(Constants.EXTRA_SEARCH_QUERY, searchQuery);
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
                    //do somthing
                }
            }
        }
    };


    public void initVariables() {
        try {
            category = CoreGsonUtils.fromJson(getArguments().getString(Constants.EXTRA_CATEGORY), Category.class);
            suggestedCategories = CoreGsonUtils.fromJsontoRealmList(getArguments().getString(Constants.EXTRA_CATEGORY_LIST), Category.class);
            positionInViewPager = getArguments().getInt(Constants.EXTRA_CLICK_POSITION);
            searchQuery = getArguments().getString(Constants.EXTRA_SEARCH_QUERY);
            cartItems = CartHelper.getCart();

            updateSuggestionCatgeoryListBasedOnSelectedCategory();
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
                //get box category item
                getBoxCategoryItems();
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {

                    /**
                     * Specail Cart Event
                     */
                    EventBus.getDefault().post(new ShowSpecialCardEvent(false));

                    if (dy > 60) {
                        if (searchDetailAdapter != null) {
                            searchDetailAdapter.onScrollingListUpdateView();
                        }
                    }

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
            searchDetailAdapter.setSuggestedCategories(suggestedCategories);
            //check with cart item and evaluate data
            setData();
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(manager);
            listener.setmLayoutManager(manager);
            recyclerView.setAdapter(searchDetailAdapter);
            recyclerView.addOnScrollListener(listener);
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
                    boxItem.setItemViewType(Constants.VIEW_TYPE_SEARCH_ITEM);
                    boxItems.set(i, boxItem);
                } else {
                    if (boxItem.getUserItem() != null) {
                        if (checkForSubscribeItem(boxItem.getUserItem())) {
                            boxItem.setItemViewType(Constants.VIEW_TYPE_SUBSCRIBE_ITEM);
                        } else {
                            boxItem.setItemViewType(Constants.VIEW_TYPE_SEARCH_ITEM);
                        }
                        boxItems.set(i, boxItem);
                    }
                }
            }
        } else {
            for (int i = 0; i < boxItems.size(); i++) {
                BoxItem boxItem = boxItems.get(i);
                if (boxItem.getUserItem() != null) {
                    if (checkForSubscribeItem(boxItem.getUserItem())) {
                        boxItem.setItemViewType(Constants.VIEW_TYPE_SUBSCRIBE_ITEM);
                    } else {
                        boxItem.setItemViewType(Constants.VIEW_TYPE_SEARCH_ITEM);
                    }
                    boxItems.set(i, boxItem);
                }
            }
        }

        searchDetailAdapter.setBoxItems(boxItems);
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

    /**
     * Check For Subscribe Item
     */
    public boolean checkForSubscribeItem(UserItem userItem) {
        boolean flag = false;
        if (userItem.isSubscribeItemAvailable()) {
            flag = true;
        }
        return flag;
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
                .getCategoryItem(PrefUtils.getToken(getActivity()), category.getUuid(), searchQuery)
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

    @Override
    public void onStart() {
        super.onStart();
        if (!isRegistered) {
            EventBus.getDefault().register(this);
            isRegistered = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRegistered) {
            EventBus.getDefault().unregister(this);
            isRegistered = false;
        }
    }

    /**
     * Synced with cart product item on editing cart
     */
    @Subscribe
    public void syncedWithCart(final SyncCartItemEvent syncCartItemEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (syncCartItemEvent.getEventType()) {

                        case Constants.UPDATE_QUANTITY_EVENT: //update Quantity
                            if (searchDetailAdapter != null) {
                                searchDetailAdapter.updateQuantityEvent(syncCartItemEvent.getBoxItem());
                            }
                            break;
                        case Constants.REMOVE_ITEM_EVENT://remove qunatity
                            if (searchDetailAdapter != null) {
                                searchDetailAdapter.removeQuantityEvent(syncCartItemEvent.getBoxItem());
                            }
                            break;
                        case Constants.UPDATE_ITEMCONFIG_EVENT:
                            if (searchDetailAdapter != null) {
                                searchDetailAdapter.updateItemConfigEvent(syncCartItemEvent.getBoxItem());
                            }
                            break;
                    }
                }
            });
        }
    }

}
