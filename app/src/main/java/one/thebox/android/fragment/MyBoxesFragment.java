package one.thebox.android.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.ViewHelper.Montserrat;
import one.thebox.android.ViewHelper.MontserratTextView;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.MyBoxRecyclerAdapter;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static one.thebox.android.fragment.SearchDetailFragment.BROADCAST_EVENT_TAB;
import static one.thebox.android.fragment.SearchDetailFragment.EXTRA_NUMBER_OF_TABS;

/**
 * Created by vaibhav on 17/08/16.
 */
public class MyBoxesFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private RecyclerView recyclerView;
    private MyBoxRecyclerAdapter myBoxRecyclerAdapter;
    private View rootLayout;
    private GifImageView progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView noOfItemsInCart;
    private List<Box> boxes = new ArrayList<>();
    private AppBarObserver appBarObserver;
    private FrameLayout fabHolder;
    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private LinearLayout no_item_subscribed_view_holder;
    private User user;
    private String monthly_bill;
    private String total_no_of_items;
    private boolean isLocallyUpdated;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int noOfTabs = intent.getIntExtra(EXTRA_NUMBER_OF_TABS, 0);
                    if (noOfTabs > 0) {
                        noOfItemsInCart.setVisibility(View.VISIBLE);
                        noOfItemsInCart.setText(String.valueOf(noOfTabs));
                    } else {
                        noOfItemsInCart.setVisibility(View.GONE);
                    }
                }
            });

        }
    };


    public MyBoxesFragment() {

    }

    public void initDataChangeListener() {
        Realm realm = MyApplication.getRealm();
        realm.addChangeListener(realmListener);
    }

    private void removeChangeListener() {
        Realm realm = MyApplication.getRealm();
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
                        if (!isLocallyUpdated) {
                            isLocallyUpdated = true;
                            getMyBoxes();
                        } else {
                            isLocallyUpdated = false;
                        }
                    }
                });
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootLayout = inflater.inflate(R.layout.fragment_my_boxes, container, false);
        initVariables();
        initViews();
        getMyBoxes();
        setupAppBarObserver();

        if (!boxes.isEmpty()) {
            setupRecyclerView();
        }
        onTabEvent(new TabEvent(CartHelper.getNumberOfItemsInCart()));
        RealmChangeManager.getInstance();
        initDataChangeListener();
        return rootLayout;
    }

    private void initVariables() {
        this.user = PrefUtils.getUser(this.getActivity());
        Realm realm = MyApplication.getRealm();
        RealmQuery<Box> query = realm.where(Box.class);
        RealmResults<Box> realmResults = query.notEqualTo(Box.FIELD_ID, 0).findAll();
        RealmList<Box> boxes = new RealmList<>();
        boxes.addAll(realmResults.subList(0, realmResults.size()));
        this.boxes.addAll(realm.copyFromRealm(boxes));
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
        user = PrefUtils.getUser(this.getActivity());
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            no_item_subscribed_view_holder.setVisibility(View.VISIBLE);
            fabHolder.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

            // Adding Onclick listener directing to Store Fragment
            no_item_subscribed_view_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new OnHomeTabChangeEvent(1));
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            fabHolder.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            if (myBoxRecyclerAdapter == null || null == recyclerView.getAdapter()) {
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                myBoxRecyclerAdapter = new MyBoxRecyclerAdapter(getActivity());
                myBoxRecyclerAdapter.setBoxes(boxes);
                myBoxRecyclerAdapter.setMonthly_bill(monthly_bill);
                myBoxRecyclerAdapter.setTotal_no_of_items(total_no_of_items);
                recyclerView.setAdapter(myBoxRecyclerAdapter);
            } else {
                myBoxRecyclerAdapter.setBoxes(boxes);
                myBoxRecyclerAdapter.setMonthly_bill(monthly_bill);
                myBoxRecyclerAdapter.setTotal_no_of_items(total_no_of_items);
                myBoxRecyclerAdapter.notifyDataSetChanged();
            }

        }
    }

    private void initViews() {
        this.progressBar = (GifImageView) rootLayout.findViewById(R.id.progress_bar);
        this.recyclerView = (RecyclerView) rootLayout.findViewById(R.id.recycler_view);
        this.no_item_subscribed_view_holder = (LinearLayout) rootLayout.findViewById(R.id.no_item_subscribed_view_holder);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        this.floatingActionButton = (FloatingActionButton) rootLayout.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(fabClickListener);
        noOfItemsInCart = (TextView) rootLayout.findViewById(R.id.no_of_items_in_cart);
        fabHolder = (FrameLayout) rootLayout.findViewById(R.id.fab_holder);
        no_item_subscribed_view_holder = (LinearLayout) rootLayout.findViewById(R.id.no_item_subscribed_view_holder);
        connectionErrorViewHelper = new ConnectionErrorViewHelper(rootLayout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyBoxes();
            }
        });
    }

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);
        MyApplication.getAPIService().getMyBoxes(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<MyBoxResponse>() {
                    @Override
                    public void onResponse(Call<MyBoxResponse> call, Response<MyBoxResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        progressBar.setVisibility(View.GONE);

                        if (response.body() != null) {
                            if (!(boxes.equals(response.body().getBoxes()))) {
//                                boxes.clear();
//                                boxes.addAll(response.body().getBoxes());
//                                storeToRealm();
//                                setupRecyclerView();

                            }
                            removeChangeListener();
                            boxes.clear();
                            monthly_bill = response.body().getMonthly_bill();
                            total_no_of_items = response.body().getTotal_no_of_items();
                            boxes.addAll(response.body().getBoxes());
                            isLocallyUpdated = true;
                            storeToRealm();
                            setupRecyclerView();

                        }
                    }

                    @Override
                    public void onFailure(Call<MyBoxResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        no_item_subscribed_view_holder.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });
    }

    private void storeToRealm() {
        final Realm superRealm = MyApplication.getRealm();
        superRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(boxes);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (null != getActivity()) {
                    ((MainActivity) getActivity()).addBoxesToMenu();
                    initDataChangeListener();
                }
                // Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).showTimeSlotBottomSheet();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //  Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).showTimeSlotBottomSheet();
            }
        });

    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
        fabHolder.setTranslationY(-offset);
    }

    public void onTabEvent(TabEvent tabEvent) {
        if (getActivity() == null) {
            return;
        }
        FloatingActionButton mFab = (FloatingActionButton) fabHolder.findViewById(R.id.fab);
        if (tabEvent.getNumberOfItemsInCart() > 0) {
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.light_grey)));
            mFab.setOnClickListener(fabClickListener);
            noOfItemsInCart.setVisibility(View.VISIBLE);
            noOfItemsInCart.setText(String.valueOf(tabEvent.getNumberOfItemsInCart()));
        } else {
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.light_grey)));
            mFab.setOnClickListener(null);
            noOfItemsInCart.setVisibility(View.GONE);
        }
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
    }

    @Subscribe
    public void UpdateOrderItemEvent() {
//        getMyBoxes();
    }

    @Subscribe
    public void onUpdateOrderEvent(UpdateOrderItemEvent onUpdateOrderItem) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    getMyBoxes();
                }
            });
        }
    }

}
