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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import one.thebox.android.Events.UpdateSavingsEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.saving.Saving;
import one.thebox.android.Models.user.Subscription;
import one.thebox.android.api.Responses.UserItemResponse;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.subscription.SubscriptionAdapter;
import one.thebox.android.api.Responses.boxes.SubscriptionResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static one.thebox.android.fragment.SearchDetailFragment.BROADCAST_EVENT_TAB;

/**
 * Created by vaibhav on 17/08/16.
 */
public class MyBoxesFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;
    public static final int RECYCLER_VIEW_TYPE_NORMAL = 300;
    private RecyclerView recyclerView;
    private SubscriptionAdapter subscriptionAdapter;
    private View rootLayout;
    private GifImageView progressBar;
    private AppBarObserver appBarObserver;
    private ConnectionErrorViewHelper connectionErrorViewHelper;
    private LinearLayout no_item_subscribed_view_holder;
    private String monthly_bill;
    private String total_no_of_items;
    private boolean show_loader_and_call = false;
    private boolean isRegistered;
    private Saving saving;

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
        RealmChangeManager.getInstance();

        initViews();
        initVariables();

        //Fetching arguments- silent notification for my items
        show_loader_and_call = getArguments().getBoolean("show_loader");

        fetchSubscription(show_loader_and_call);

        if (PrefUtils.getBoolean(getActivity(), Keys.LOAD_ORDERED_USER_ITEM, false)) {
            //fetchOrderedUserItem(show_loader_and_call);
            PrefUtils.putBoolean(getActivity(), Keys.LOAD_ORDERED_USER_ITEM, false);
        }

        setupAppBarObserver();
        initDataChangeListener();

        onTabEvent(new TabEvent(CartHelper.getNumberOfItemsInCart()));

        return rootLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private synchronized void initVariables() {
       /* ArrayList<Subscription> subscriptions = new ArrayList<>();

        //Fetch Subscription if stored in local database
        Realm realm = TheBox.getRealm();
        RealmResults<Subscription> realmResults1 = realm.where(Subscription.class).findAll();

        RealmList<Subscription> itemRealmList = new RealmList<>();
        itemRealmList.addAll(realm.copyFromRealm(realmResults1.subList(0, realmResults1.size())));

        if (itemRealmList.size() > 0) {
            subscriptions.clear();
            for (Subscription subscription : itemRealmList) {
                if (getUserItems(subscription).size() > 0) {
                    subscription.setUserItems(getUserItems(subscription));
                    subscriptions.add(subscription);
                }
            }
        }
*/
        //get savings from the preferances
        saving = CoreGsonUtils.fromJson(PrefUtils.getString(getActivity(), Constants.SAVINGS), Saving.class);

       /* if (subscriptions.size() > 0) {
            setupRecyclerView(subscriptions);
        } else {
            setupEmptyStateView();
        }
*/
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


    private void setupRecyclerView(ArrayList<Subscription> subscriptions) {
        //One or more items are subscribed
        no_item_subscribed_view_holder.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        if (subscriptionAdapter == null || null == recyclerView.getAdapter()) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            subscriptionAdapter = new SubscriptionAdapter(getActivity(), glideRequestManager);
            subscriptionAdapter.setSubscriptions(subscriptions);

            //This view will display the saving card as header and rest of the items
            setDataToRecyclerView(subscriptions);
            recyclerView.setAdapter(subscriptionAdapter);
        } else {
            setDataToRecyclerView(subscriptions);
        }
    }

    /**
     * Check and set the view type in recyclerview
     */
    public void setDataToRecyclerView(ArrayList<Subscription> subscriptions) {

        subscriptionAdapter.setSubscriptions(subscriptions);
        subscriptionAdapter.setSaving(saving);
        /**
         * Show Header savings ;if savings is true
         * and user items
         */
        if (subscriptions.size() > 0 && saving != null) {
            if (saving.isSaving()) {
                subscriptionAdapter.setViewType(RECYCLER_VIEW_TYPE_HEADER);
            } else {
                subscriptionAdapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
            }
        }
        /**
         * Show only user items
         */
        else if (subscriptions.size() > 0 && saving == null) {
            subscriptionAdapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
        }
    }

    public void setupEmptyStateView() {
        // No items are subscribed
        no_item_subscribed_view_holder.setVisibility(View.VISIBLE);
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

    /**
     * Fetch Subscriptions from server
     */
    public void fetchSubscription(boolean showLoader) {
        connectionErrorViewHelper.isVisible(false);
        if (showLoader) {
            progressBar.setVisibility(View.VISIBLE);
        }

        TheBox.getAPIService()
                .getSubscription(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<SubscriptionResponse>() {
                    @Override
                    public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                        connectionErrorViewHelper.isVisible(false);
                        progressBar.setVisibility(View.GONE);
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    //set the Response and update the list
                                    //get savings
                                    if (response.body().getSavings() != null) {
                                        if (response.body().getSavings().size() > 0) {
                                            if (response.body().getSavings().get(0).getType().equals("current")) {
                                                saving = response.body().getSavings().get(0);
                                            }
                                        }
                                    }

                                    //show recyclerview
                                    if (response.body().getSubscriptions() != null) {
                                        if (response.body().getSubscriptions().size() > 0) {
                                            setupRecyclerView(response.body().getSubscriptions());
                                        } else {
                                            setupEmptyStateView();
                                        }
                                    } else {
                                        setupEmptyStateView();
                                    }

                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            no_item_subscribed_view_holder.setVisibility(View.GONE);
                            connectionErrorViewHelper.isVisible(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                        Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        no_item_subscribed_view_holder.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });


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

                        //get savings
                        if (response.body().getSavings() != null) {
                            if (response.body().getSavings().size() > 0) {
                                if (response.body().getSavings().get(0).getType().equals("current")) {
                                    saving = response.body().getSavings().get(0);
                                }
                            }
                        }

                        //save saving in the preferance
                        PrefUtils.putString(getActivity(), Constants.SAVINGS, CoreGsonUtils.toJson(saving));

                    } else {
                        //Parse Error
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    no_item_subscribed_view_holder.setVisibility(View.GONE);
                    connectionErrorViewHelper.isVisible(true);
                }
            }

            @Override
            public void onFailure(Call<UserItemResponse> call, Throwable t) {
                Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                no_item_subscribed_view_holder.setVisibility(View.GONE);
                connectionErrorViewHelper.isVisible(true);
            }
        });


    }


    @Override
    public void onOffsetChange(int offset, int dOffset) {
    }

    public void onTabEvent(TabEvent tabEvent) {
        if (getActivity() == null) {
            return;
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

    @Subscribe
    public void onUpdateSavingsEvent(final UpdateSavingsEvent updateSavingsEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the saving card
                        if (updateSavingsEvent.getSavings() != null) {
                            if (updateSavingsEvent.getSavings().size() > 0) {

                                //save saving in the preferance
                                PrefUtils.putString(getActivity(), Constants.SAVINGS, CoreGsonUtils.toJson(updateSavingsEvent.getSavings().get(0)));

                                //update savings data in Adapter
                                if (recyclerView != null && null != subscriptionAdapter) {
                                    subscriptionAdapter.setSaving(updateSavingsEvent.getSavings().get(0));

                                }

                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }
}
