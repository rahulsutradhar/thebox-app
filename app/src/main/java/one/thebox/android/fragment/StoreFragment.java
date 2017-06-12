package one.thebox.android.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import one.thebox.android.Events.DisplayProductForBoxEvent;
import one.thebox.android.Events.DisplayProductForCarouselEvent;
import one.thebox.android.Events.DisplayProductForSavingsEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.items.Category;
import one.thebox.android.Models.items.Box;
import one.thebox.android.Models.carousel.Offer;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.StoreRecyclerAdapter;
import one.thebox.android.api.Responses.CarouselApiResponse;
import one.thebox.android.api.Responses.boxes.BoxResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static one.thebox.android.fragment.SearchDetailFragment.BROADCAST_EVENT_TAB;

public class StoreFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    public static final int RECYCLER_VIEW_TYPE_NORMAL = 300;
    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;

    private RecyclerView recyclerView;
    private StoreRecyclerAdapter storeRecyclerAdapter;
    private View rootLayout;
    private GifImageView progressBar;
    private ArrayList<Offer> carousel = new ArrayList<>();
    private AppBarObserver appBarObserver;
    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private boolean mIsRestoredFromBackstack;

    private RealmList<Box> boxes = new RealmList<>();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });

        }
    };


    public StoreFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootLayout == null) {
            rootLayout = inflater.inflate(R.layout.fragment_store, container, false);
            RealmChangeManager.getInstance();
            initViews();
            initVariables();

            //fetch box from server
            getBoxesFromServer();

            if (PrefUtils.getBoolean(getActivity(), Keys.LOAD_CAROUSEL)) {
                //fetch carousel from server
                getCarousel();
                PrefUtils.putBoolean(getActivity(), Keys.LOAD_CAROUSEL, false);
            }

            setupAppBarObserver();

            onTabEvent(new TabEvent(CartHelper.getCartSize()));
            initDataChangeListener();
        }
        return rootLayout;
    }

    private void initVariables() {

        /**
         * Fetch Carousel from Preferances
         */
        getValidOffer(CoreGsonUtils.fromJsontoArrayList(PrefUtils.getString(getActivity(), Constants.CAROUSEL_BANNER), Offer.class));


        if (!boxes.isEmpty()) {
            setupRecyclerView();
        }
    }

    /**
     * Check Carousel Banner validity
     */
    public void getValidOffer(ArrayList<Offer> carousel) {
        try {

            ArrayList<Offer> offers = new
                    ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date date = sdf.parse("2017-03-06T03:05:00.000+05:30");
            sdf.setTimeZone(TimeZone.getTimeZone("IST"));
            String current = sdf.format(date);
            Date currentDate = DateTimeUtil.convertStringToDate(current);

            for (Offer offer : carousel) {
                Date startDate = DateTimeUtil.convertStringToDate(offer.getStartDate());
                Date endDate = DateTimeUtil.convertStringToDate(offer.getEndDate());

                if (DateTimeUtil.checkCurrentDateIsWithinRange(startDate, endDate, currentDate)) {
                    offers.add(offer);
                }
            }

            if (offers.size() > 0) {
                //sort the items priority wise
                Collections.sort(offers, new Comparator<Offer>() {
                    @Override
                    public int compare(Offer offer1, Offer offer2) {
                        return offer1.getPriority() > offer2.getPriority() ? -1 : (offer1.getPriority() < offer2.getPriority()) ? 1 : 0;

                    }
                });

                this.carousel.clear();
                this.carousel.addAll(offers);
                offers.clear();
            }
        } catch (Exception e) {

        }
    }

    private void setupAppBarObserver() {
        Activity activity = getActivity();
        AppBarLayout appBarLayout = (AppBarLayout) activity
                .findViewById(R.id.app_bar_layout);
        if (appBarLayout != null) {
            appBarObserver = AppBarObserver.observe(appBarLayout);
            appBarObserver.addOffsetChangeListener(this);
        }
    }

    private void setupRecyclerView() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (storeRecyclerAdapter == null) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            storeRecyclerAdapter = new StoreRecyclerAdapter(getActivity(), Glide.with(this));
            setRecycelerViewData();
            recyclerView.setAdapter(storeRecyclerAdapter);
        } else {
            setRecycelerViewData();
        }
    }

    public void setRecycelerViewData() {
        try {
            storeRecyclerAdapter.setBoxes(boxes);
            storeRecyclerAdapter.setCarousel(carousel);

            if (carousel == null) {
                storeRecyclerAdapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
            } else if (carousel.size() == 0) {
                storeRecyclerAdapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
            } else {
                storeRecyclerAdapter.setViewType(RECYCLER_VIEW_TYPE_HEADER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        this.progressBar = (GifImageView) rootLayout.findViewById(R.id.progress_bar);
        this.recyclerView = (RecyclerView) rootLayout.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        connectionErrorViewHelper = new ConnectionErrorViewHelper(rootLayout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBoxesFromServer();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIsRestoredFromBackstack = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeChangeListener();
        this.mIsRestoredFromBackstack = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDataChangeListener();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_EVENT_TAB));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Fetch Boxes from Server
     */
    public void getBoxesFromServer() {

        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);
        Log.d("TOKEN", PrefUtils.getToken(getActivity()));
        TheBox.getAPIService()
                .getBoxes(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<BoxResponse>() {
                    @Override
                    public void onResponse(Call<BoxResponse> call, Response<BoxResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        progressBar.setVisibility(View.GONE);
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    boxes.addAll(response.body().getBoxes());
                                    setupRecyclerView();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BoxResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });

    }


    public void getCarousel() {
        TheBox.getAPIService().getCarousel(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<CarouselApiResponse>() {
                    @Override
                    public void onResponse(Call<CarouselApiResponse> call, Response<CarouselApiResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getOffers() != null) {
                                        //save in the Preferances
                                        PrefUtils.putString(getActivity(), Constants.CAROUSEL_BANNER, CoreGsonUtils.toJson(response.body().getOffers()));

                                        initVariables();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarouselApiResponse> call, Throwable t) {
                    }
                });
    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
    }

    public void onTabEvent(TabEvent tabEvent) {

    }

    private boolean isRegistered;

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

    @Subscribe
    public void onUpdateOrderEvent(UpdateOrderItemEvent onUpdateOrderItem) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    public void initDataChangeListener() {
        Realm realm = TheBox.getRealm();
        realm.addChangeListener(realmListener);
    }

    private void removeChangeListener() {
        Realm realm = TheBox.getRealm();
        realm.removeChangeListener(realmListener);
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


    /**
     * Search Box Uuid for Offer; Carousel and get All Category
     */
    public void searchCategoryFoCarousel(Offer offer) {
        if (boxes != null) {
            if (boxes.size() > 0) {
                for (Box box : boxes) {
                    if (box.getUuid().equalsIgnoreCase(offer.getBoxUuid())) {
                        int index = 0;
                        for (Category category : box.getCategories()) {
                            if (category.getUuid().equalsIgnoreCase(offer.getCategoryUuid())) {
                                displayProduct(box.getCategories(), category.getUuid(), box.getTitle(), index);
                            }
                            index++;
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Search Box UUid for Savings;
     */
    public void searchCategoryForSavings(String boxUuid, String boxTitle) {
        if (boxes != null) {
            if (boxes.size() > 0) {
                for (Box box : boxes) {
                    if (box.getUuid().equalsIgnoreCase(boxUuid)) {
                        displayProduct(box.getCategories(), "", boxTitle, 0);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Search Box UUid for Box;
     */
    public void searchCategoryForBox(Box selectedBox) {
        if (boxes != null) {
            if (boxes.size() > 0) {
                for (Box box : boxes) {
                    if (box.getUuid().equalsIgnoreCase(selectedBox.getUuid())) {
                        displayProduct(box.getCategories(), "", selectedBox.getTitle(), 0);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Set Data and Display Products
     */
    public void displayProduct(RealmList<Category> categories, String categoryUuid, String boxTitle, int position) {
        Intent intent = new Intent(getActivity(), MainActivity.class)
                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 6)
                .putExtra(Constants.EXTRA_BOX_CATEGORY, CoreGsonUtils.toJson(categories))
                .putExtra(Constants.EXTRA_CLICKED_CATEGORY_UID, categoryUuid)
                .putExtra(Constants.EXTRA_CLICK_POSITION, position)
                .putExtra(Constants.EXTRA_BOX_NAME, boxTitle);
        startActivity(intent);
    }

    /**
     * OnClicking Carousel
     */
    @Subscribe
    public void eventDisplayProductForCarousel(final DisplayProductForCarouselEvent displayProductForCarouselEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    searchCategoryFoCarousel(displayProductForCarouselEvent.getOffer());
                }
            });
        }
    }

    /**
     * On Clicking Savings
     */
    @Subscribe
    public void eventDisplayProductForSavings(final DisplayProductForSavingsEvent displayProductForSavingsEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchCategoryForSavings(displayProductForSavingsEvent.getBoxUuid(), displayProductForSavingsEvent.getBoxTitle());
                }
            });
        }
    }

    /**
     * On Clicking Box
     */
    @Subscribe
    public void eventDisplayProductForBox(final DisplayProductForBoxEvent displayProductForBoxEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchCategoryForBox(displayProductForBoxEvent.getBox());
                }
            });
        }
    }

}
