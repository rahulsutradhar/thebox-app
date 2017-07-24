package one.thebox.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
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
import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Events.UpdateSavingsEvent;
import one.thebox.android.Events.UpdateSubscribeItemEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.saving.Saving;
import one.thebox.android.Models.items.Subscription;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.subscription.SubscriptionAdapter;
import one.thebox.android.api.Responses.boxes.SubscriptionResponse;
import one.thebox.android.api.Responses.subscribeitem.SavingsResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vaibhav on 17/08/16.
 * <p>
 * Modified by Developers on 07/06/2017.
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
    private boolean show_loader_and_call = false;
    private boolean isRegistered;
    private Saving saving;
    ArrayList<Subscription> subscriptions = new ArrayList<>();

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

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

        //Fetching arguments- silent notification for my items
        show_loader_and_call = getArguments().getBoolean("show_loader");

        //fetch subscription from server
        fetchSubscription(show_loader_and_call);

        setupAppBarObserver();
        initDataChangeListener();

        onTabEvent(new TabEvent(CartHelper.getCartSize()));

        return rootLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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

    public void filterSubscriptionData() {
        if (subscriptions.size() > 0) {
            ArrayList<Subscription> filteredItem = new ArrayList<>();
            for (Subscription subscription : subscriptions) {
                if (subscription.getSubscribeItems() != null) {
                    if (subscription.getSubscribeItems().size() > 0) {
                        filteredItem.add(subscription);
                    }
                }
            }
            subscriptions.clear();
            subscriptions.addAll(filteredItem);
            setupRecyclerView();
        } else {
            setupEmptyStateView();
        }
    }

    private void setupRecyclerView() {
        if (subscriptions.size() > 0) {
            //One or more items are subscribed
            no_item_subscribed_view_holder.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (subscriptionAdapter == null || null == recyclerView.getAdapter()) {
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                subscriptionAdapter = new SubscriptionAdapter(getActivity(), glideRequestManager);

                //This view will display the saving card as header and rest of the items
                setDataToRecyclerView(subscriptions);
                recyclerView.setAdapter(subscriptionAdapter);
            } else {
                setDataToRecyclerView(subscriptions);
            }
        } else {
            setupEmptyStateView();
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
                fetchSubscription(true);
            }
        });
    }

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), MainActivity.class).putExtra(Constants.EXTRA_ATTACH_FRAGMENT_NO, 3));
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
                                    subscriptions.clear();
                                    if (response.body().getSubscriptions() != null) {
                                        if (!response.body().getSubscriptions().isEmpty()) {
                                            subscriptions.addAll(response.body().getSubscriptions());
                                        }
                                    }
                                    /**
                                     * Filter Subscription Data
                                     */
                                    filterSubscriptionData();

                                }
                            } else {
                                if (response.code() == 404) {
                                    recyclerView.setVisibility(View.GONE);
                                    no_item_subscribed_view_holder.setVisibility(View.GONE);
                                    connectionErrorViewHelper.isVisible(true);
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
     * Fetch Savings on Subscribe Item
     */
    public void fetchSavings() {

        TheBox.getAPIService()
                .getSavings(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<SavingsResponse>() {
                    @Override
                    public void onResponse(Call<SavingsResponse> call, Response<SavingsResponse> response) {
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
                                    updateSavingsUI(saving);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<SavingsResponse> call, Throwable t) {
                    }
                });
    }

    public void updateSavingsUI(Saving saving) {
        if (saving != null) {
            //save saving in the preferance
            PrefUtils.putString(getActivity(), Constants.SAVINGS, CoreGsonUtils.toJson(saving));

            if (subscriptionAdapter != null) {
                subscriptionAdapter.setSaving(saving);
            }
        }
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


    /**
     * Called from Updating Subscription Item
     */
    @Subscribe
    public void onUpdateSavingsEvent(UpdateSavingsEvent updateSavingsEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (subscriptionAdapter != null) {
                        //check id all items have been removed from the subscribed items list
                        if (subscriptionAdapter.getItemsCount() == 0) {

                            setupEmptyStateView();
                        } else {
                            //fetch savings to update
                            fetchSavings();
                        }
                    }
                }
            });
        }

    }

    /**
     * Called from Updating Subscription Item
     */
    @Subscribe
    public void onUpdateSubscribeItemEvent(UpdateSubscribeItemEvent updateSubscribeItemEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fetchSubscription(false);
                }
            });
        }

    }
}
