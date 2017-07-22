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
import android.widget.Toast;

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
import one.thebox.android.Models.Meta;
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

    private View rootView;
    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private LinearLayout linearLayoutHolder;
    private GifImageView progressBar;
    private TextView emptyText;

    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private boolean isRegistered;

    private Category category;
    private int positionInViewPager;
    private RealmList<Category> suggestedCategories = new RealmList<>();
    private String searchQuery = "";

    /**
     * Pagination
     */
    private int queryPage = 1;
    private int queryPerPage = Constants.LOAD_PER_PAGE;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private Meta meta;

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
            getBoxCategoryItems(true);

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
                getBoxCategoryItems(true);
            }
        });
    }

    /**
     * Set Up RecyclerView
     *
     * @param boxItems
     */
    private void setupRecyclerView(List<BoxItem> boxItems, boolean isFirstTimeLoad) {
        if (isFirstTimeLoad) {
            linearLayoutHolder.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (boxItems.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                emptyText.setVisibility(View.GONE);
            }
        }

        if (searchDetailAdapter == null) {
            searchDetailAdapter = new SearchDetailAdapter(getActivity(), glideRequestManager);
            searchDetailAdapter.setSuggestedCategories(suggestedCategories);
            //check with cart item and evaluate data
            setData(boxItems);
            final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(searchDetailAdapter);

            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy != 0) {

                        /**
                         * Specail CartProduct Event
                         */
                        EventBus.getDefault().post(new ShowSpecialCardEvent(false));

                        /**
                         * Pagination Logic
                         */

                        visibleItemCount = recyclerView.getChildCount();
                        totalItemCount = manager.getItemCount();
                        firstVisibleItem = manager.findFirstVisibleItemPosition();

                        if (loading) {
                            if (totalItemCount > previousTotal) {
                                loading = false;
                                previousTotal = totalItemCount;
                            }
                        }
                        if (!loading && (totalItemCount - visibleItemCount)
                                <= (firstVisibleItem + visibleThreshold)) {
                            // End has been reached
                            Log.i("PAGINATION", "END HAS REACHED");
                            // Logic for pagination
                            if (meta != null) {
                                if ((meta.getTotalItems() - (meta.getCurrentPage() * meta.getPerPage())) > 0) {
                                    /**
                                     * condition full fills more items are there
                                     * so paginate to next page
                                     */
                                    queryPage = meta.getCurrentPage() + 1;
                                    //Load Next Page data from Server
                                    getBoxCategoryItems(false);
                                    //next loading
                                    loading = true;
                                }
                            }
                        }

                    }
                }
            });

        } else {
            setData(boxItems);
        }
    }

    /**
     * Set the refined list after matching with cart data
     */
    public void setData(List<BoxItem> boxItems) {
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
     * Check if item is in CartProduct
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
    private void getBoxCategoryItems(final boolean isFirstTimeLoad) {
        if (isFirstTimeLoad) {
            linearLayoutHolder.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            connectionErrorViewHelper.isVisible(false);
        }
        TheBox.getAPIService()
                .getCategoryItem(PrefUtils.getToken(getActivity()), category.getUuid(), searchQuery, queryPage, queryPerPage)
                .enqueue(new Callback<BoxCategoryItemResponse>() {
                    @Override
                    public void onResponse(Call<BoxCategoryItemResponse> call, Response<BoxCategoryItemResponse> response) {
                        try {
                            if (isFirstTimeLoad) {
                                connectionErrorViewHelper.isVisible(false);
                                progressBar.setVisibility(View.GONE);
                                linearLayoutHolder.setVisibility(View.VISIBLE);
                            }

                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    if (response.body().getCategory() != null) {
                                        //set Meta Data for pagination
                                        meta = response.body().getMeta();
                                        if (response.body().getCategory().getBoxItems() != null) {
                                            if (isFirstTimeLoad) {
                                                setupRecyclerView(response.body().getCategory().getBoxItems(), isFirstTimeLoad);
                                            } else {
                                                //don't pass data if is empty
                                                if (response.body().getCategory().getBoxItems().size() > 0) {
                                                    setupRecyclerView(response.body().getCategory().getBoxItems(), isFirstTimeLoad);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                //parse error codes here
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BoxCategoryItemResponse> call, Throwable t) {
                        if (isFirstTimeLoad) {
                            linearLayoutHolder.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            connectionErrorViewHelper.isVisible(true);
                        }
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
                        case Constants.REMOVE_ITEM_EVENT://remove quantity
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
