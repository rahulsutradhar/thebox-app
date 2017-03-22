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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.user.OrderedUserItem;
import one.thebox.android.api.Responses.UserItemResponse;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.MyBoxRecyclerAdapter;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
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

    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;
    private RecyclerView recyclerView;
    private MyBoxRecyclerAdapter myBoxRecyclerAdapter;
    private View rootLayout;
    private GifImageView progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView noOfItemsInCart;
    private AppBarObserver appBarObserver;
    private FrameLayout fabHolder;
    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private LinearLayout no_item_subscribed_view_holder;
    private String monthly_bill;
    private String total_no_of_items;
    private boolean show_loader_and_call = false;
    private boolean isRegistered;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;


    private boolean isLocallyUpdated;
    private boolean has_one_or_more_subscribed_items = false;
    private boolean show_loader = false;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootLayout = inflater.inflate(R.layout.fragment_my_boxes, container, false);

        initViews();
        initVariables();

        //Fetching arguments- silent notification for my items
        show_loader_and_call = getArguments().getBoolean("show_loader");

        if (PrefUtils.getBoolean(getActivity(), Keys.LOAD_ORDERED_USER_ITEM, false)) {
            if (show_loader) {
                fetchOrderedUserItem(false);
            } else {
                fetchOrderedUserItem(true);
            }
            PrefUtils.putBoolean(getActivity(), Keys.LOAD_ORDERED_USER_ITEM, false);
        }
        setupAppBarObserver();

        onTabEvent(new TabEvent(CartHelper.getNumberOfItemsInCart()));

        RealmChangeManager.getInstance();

        initDataChangeListener();

        return rootLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private synchronized void initVariables() {
        ArrayList<OrderedUserItem> orderedUserItems = new ArrayList<>();

        //Fetch OrderedUserItem if stored in local database
        Realm realm1 = TheBox.getRealm();
        RealmResults<OrderedUserItem> realmResults1 = realm1.where(OrderedUserItem.class).findAll();

        RealmList<OrderedUserItem> itemRealmList = new RealmList<>();
        itemRealmList.addAll(realm1.copyFromRealm(realmResults1.subList(0, realmResults1.size())));

        if (itemRealmList.size() > 0) {
            orderedUserItems.clear();
            for (OrderedUserItem orderedUserItem : itemRealmList) {
                if (getUserItems(orderedUserItem).size() > 0) {
                    orderedUserItem.setUserItems(getUserItems(orderedUserItem));
                    orderedUserItems.add(orderedUserItem);
                }
            }
        }

        if (orderedUserItems.size() > 0) {
            setupRecyclerView(orderedUserItems);
        } else {
            setupEmptyStateView();
        }
    }


    private RealmList<UserItem> getUserItems(OrderedUserItem orderedUserItem) {
        Realm realm = TheBox.getRealm();
        RealmList<UserItem> list = new RealmList<>();

        RealmResults<UserItem> items = realm.where(UserItem.class).equalTo("boxId", orderedUserItem.getBoxId()).findAll()
                .where().notEqualTo("stillSubscribed", false).findAll()
                .where().isNotNull("nextDeliveryScheduledAt").findAll();

        if (items.isLoaded()) {
            list = new RealmList<>();
            list.addAll(realm.copyFromRealm(items.subList(0, items.size())));
            return list;
        }

        return list;
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


    private void setupRecyclerView(ArrayList<OrderedUserItem> orderedUserItems) {
        //One or more items are subscribed
        no_item_subscribed_view_holder.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fabHolder.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        if (myBoxRecyclerAdapter == null || null == recyclerView.getAdapter()) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            myBoxRecyclerAdapter = new MyBoxRecyclerAdapter(getActivity(), glideRequestManager);
            myBoxRecyclerAdapter.setOrderedUserItems(orderedUserItems);
            //This view will display the saving card as header and rest of the items
            myBoxRecyclerAdapter.setViewType(RECYCLER_VIEW_TYPE_HEADER);
            recyclerView.setAdapter(myBoxRecyclerAdapter);
        } else {
            myBoxRecyclerAdapter.setOrderedUserItems(orderedUserItems);
        }

    }

    public void setupEmptyStateView() {
        // No items are subscribed
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

    }

    private void initViews() {
        this.glideRequestManager = Glide.with(this);
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
                fetchOrderedUserItem(true);
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

    /**
     * Fetch User Item
     */
    public synchronized void fetchOrderedUserItem(boolean showLoader) {

        connectionErrorViewHelper.isVisible(false);
        if (showLoader) {
            progressBar.setVisibility(View.VISIBLE);
        }

        String token = PrefUtils.getToken(getActivity());
        TheBox.getAPIService().getMyItems(token).enqueue(new Callback<UserItemResponse>() {
            @Override
            public void onResponse(Call<UserItemResponse> call, Response<UserItemResponse> response) {

                connectionErrorViewHelper.isVisible(false);
                progressBar.setVisibility(View.GONE);

                try {
                    if (response.isSuccessful() && response.body() != null) {

                        //get the OrderedUserItem
                        removeChangeListener();
                        if (response.body().getOrderedUserItems().size() > 0) {
                            //store to local database
                            storeToRealm(response.body().getOrderedUserItems());
                        }

                        Toast.makeText(TheBox.getAppContext(), "Saving " + response.body().getSavings().size(), Toast.LENGTH_SHORT).show();
                    } else {
                        //Parse Error
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    no_item_subscribed_view_holder.setVisibility(View.GONE);
                    connectionErrorViewHelper.isVisible(true);
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserItemResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                no_item_subscribed_view_holder.setVisibility(View.GONE);
                connectionErrorViewHelper.isVisible(true);
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private synchronized void storeToRealm(final ArrayList<OrderedUserItem> list) {

        final Realm superRealm1 = TheBox.getRealm();
        superRealm1.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (null != getActivity()) {
                    initVariables();

                    ((MainActivity) getActivity()).addBoxesToMenu();
                    initDataChangeListener();
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
    public synchronized void onUpdateOrderItemEvent(UpdateOrderItemEvent onUpdateOrderItem) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //fetch data from server and update the list
                    initVariables();
                }
            });
        }
    }
}
