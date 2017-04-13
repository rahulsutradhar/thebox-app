package one.thebox.android.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.common.collect.Ordering;
import com.squareup.haha.trove.THash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.Size;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.carousel.Offer;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.StoreRecyclerAdapter;
import one.thebox.android.api.Responses.CarouselApiResponse;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.AccountManager;
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
    private FloatingActionButton floatingActionButton;
    private RealmList<Box> boxes = new RealmList<>();
    private ArrayList<Offer> carousel = new ArrayList<>();
    private AppBarObserver appBarObserver;
    private ConnectionErrorViewHelper connectionErrorViewHelper;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    int noOfTabs = intent.getIntExtra(EXTRA_NUMBER_OF_TABS, 0);
//                    if (noOfTabs > 0) {
//                        noOfItemsInCart.setVisibility(View.VISIBLE);
//                        noOfItemsInCart.setText(String.valueOf(noOfTabs));
//                    } else {
//                        noOfItemsInCart.setVisibility(View.GONE);
//                    }
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
            getMyBoxes();

            if (PrefUtils.getBoolean(getActivity(), Keys.LOAD_CAROUSEL)) {
                //fetch carousel from server
                getCarousel();
                PrefUtils.putBoolean(getActivity(), Keys.LOAD_CAROUSEL, false);
            }

            setupAppBarObserver();

            onTabEvent(new TabEvent(CartHelper.getNumberOfItemsInCart()));
            initDataChangeListener();
        }
        return rootLayout;
    }

    private void setUpBoxes() {
        // Add to boxes list only if there are items in box
        Iterator<Box> iterator = this.boxes.iterator();
        while (iterator.hasNext()) {
            Box box = iterator.next();
            if (box.getAllItemInTheBox() == null || box.getAllItemInTheBox().isEmpty()) {
                iterator.remove();
            } else {
                box.setAllItemsInTheBox(getUserItems(box.getBoxId()));
            }
        }

    }

    private List<UserItem> getUserItems(int boxId) {
        Realm realm = TheBox.getRealm();
        RealmResults<UserItem> items = realm.where(UserItem.class).equalTo("boxId", boxId).findAll();
        List<UserItem> list = new ArrayList<>();
        list.addAll(items);
        return list;
    }

    private void initVariables() {
        Realm realm = TheBox.getRealm();
        RealmQuery<Box> query = realm.where(Box.class);
        RealmResults<Box> realmResults = query.notEqualTo(Box.FIELD_ID, 0).findAll();
        realmResults = realmResults.sort("priority", Sort.DESCENDING);

        RealmList<Box> boxes = new RealmList<>();
        boxes.addAll(realmResults.subList(0, realmResults.size()));
        this.boxes.clear();
        this.boxes.addAll(boxes);

        /**
         * Fetch Carousel from Preferances
         */
        getValidOffer(CoreGsonUtils.fromJsontoArrayList(PrefUtils.getString(getActivity(), Constants.CAROUSEL_BANNER), Offer.class));


        if (!boxes.isEmpty()) {
            setupRecyclerView();
        }
    }

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
//        this.floatingActionButton = (FloatingActionButton) rootLayout.findViewById(R.id.fab);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
//            }
//        });
//        fabHolder = (FrameLayout) rootLayout.findViewById(R.id.fab_holder);
        connectionErrorViewHelper = new ConnectionErrorViewHelper(rootLayout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyBoxes();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeChangeListener();
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

    public void getMyBoxes() {
        Log.d("From Store", "Calling gogetmybox");
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);
        TheBox.getAPIService().getMyBoxes(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<MyBoxResponse>() {
                    @Override
                    public void onResponse(Call<MyBoxResponse> call, Response<MyBoxResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        progressBar.setVisibility(View.GONE);

                        if (response.body() != null) {
                            if (!(boxes.equals(response.body().getBoxes()))) {
                                boxes.clear();
                                boxes.addAll(response.body().getBoxes());

                                //save locally
                                storeToRealm(boxes);
                            }
                        } else if (response.raw().code() == 401) {
                            (new AccountManager(getActivity()))
                                    .delete_account_data();
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyBoxResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });
    }

    public void getCarousel() {
        TheBox.getAPIService().getCarousel()
                .enqueue(new Callback<CarouselApiResponse>() {
                    @Override
                    public void onResponse(Call<CarouselApiResponse> call, Response<CarouselApiResponse> response) {
                        try {
                            if (response.body() != null) {

                                if (response.body().getOffers() != null) {

                                    //save in the Preferances
                                    PrefUtils.putString(getActivity(), Constants.CAROUSEL_BANNER, CoreGsonUtils.toJson(response.body().getOffers()));

                                    initVariables();
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

    private void storeToRealm(final RealmList<Box> boxes) {
        final Realm superRealm = TheBox.getRealm();
        superRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.copyToRealmOrUpdate(boxes);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (null != getActivity()) {
                    initVariables();

                    ((MainActivity) getActivity()).addBoxesToMenu();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });

    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
//        fabHolder.setTranslationY(-offset);
    }

    public void onTabEvent(TabEvent tabEvent) {
//        if (getActivity() == null) {
//            return;
//        }
//        if (tabEvent.getNumberOfItemsInCart() > 0) {
//            noOfItemsInCart.setVisibility(View.VISIBLE);
//            noOfItemsInCart.setText(String.valueOf(tabEvent.getNumberOfItemsInCart()));
//        } else {
//            noOfItemsInCart.setVisibility(View.GONE);
//        }
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

}
