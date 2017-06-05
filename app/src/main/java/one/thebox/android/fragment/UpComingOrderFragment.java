package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Events.UpdateDeliveriesAfterReschedule;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.adapter.OrdersItemAdapter;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.Constants;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;


public class UpComingOrderFragment extends Fragment implements View.OnClickListener {

    //Variables
    public int scene_number = 0;

    //Views
    private View rootView;
    private RecyclerView recyclerView;
    private OrdersItemAdapter ordersItemAdapter;
    private RealmList<Order> orders = new RealmList<>();
    private LinearLayout no_orders_subscribed_view_holder;
    private GifImageView progress_bar;

    public UpComingOrderFragment() {
    }

    public static UpComingOrderFragment newInstance() {
        UpComingOrderFragment fragment = new UpComingOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_up_coming_order, container, false);
        initViews();
        initVariables(false);


        if (PrefUtils.getBoolean(getActivity(), Keys.LOAD_ORDERED_MY_DELIVERIES, false)) {
            //fetchDeliveriesFromServer();
            PrefUtils.putBoolean(getActivity(), Keys.LOAD_ORDERED_MY_DELIVERIES, false);
        }

        return rootView;
    }

    private void initVariables(boolean checkOrderSize) {

        //ignore Order class if order successful is false
        Realm realm = TheBox.getRealm();
        RealmQuery<Order> query = realm.where(Order.class).notEqualTo("successful", false);
        RealmResults<Order> realmResults = query.notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_IS_CART, false).findAll();

        orders.clear();
        for (Order order : realmResults) {
            orders.add(order);
        }

        if (!orders.isEmpty()) {
            scene_number = 0;
        } else {
            //show error message
            scene_number = -1;
        }

        //this called when we remove useritem from OrderedItem
        if (checkOrderSize) {
            if (orders != null) {
                if (orders.size() < 4) {
                    //if order size is less than 4 fetch orders again
                    fetchDeliveriesFromServer();
                }
            }
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        no_orders_subscribed_view_holder = (LinearLayout) rootView.findViewById(R.id.no_orders_subscribed_view_holder);
        progress_bar = (GifImageView) rootView.findViewById(R.id.progress_bar);

        switch (scene_number) {
            case -1: {
                no_orders_subscribed_view_holder.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                no_orders_subscribed_view_holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new OnHomeTabChangeEvent(1));
                    }
                });
                break;
            }
            case 0: {
                setupRecyclerView();
                no_orders_subscribed_view_holder.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);
                break;
            }
        }
    }

    /**
     * Fetch Deliveries from Server
     */
    public void fetchDeliveriesFromServer() {
        OrderHelper.getOrderAndNotify(false);
    }

    private void setupRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        ordersItemAdapter = new OrdersItemAdapter(getActivity(), orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(ordersItemAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    // Called after Constants.PREF_IS_ORDER_IS_LOADING is switched from false to true
    @Subscribe
    public void onUpdateUpcomingDeliveries(final UpdateUpcomingDeliveriesEvent updateUpcomingDeliveriesEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    initVariables(false);
                    initViews();

                }
            });
        }
    }

    //called after rescheudle delivery
    @Subscribe
    public void onUpdateDeliveries(UpdateDeliveriesAfterReschedule updateDeliveriesAfterReschedule) {
        try {
            //fetch order and update accordingly
            OrderHelper.getOrderAndNotify(updateDeliveriesAfterReschedule.getNotifyTo(), updateDeliveriesAfterReschedule.isShallNotifyAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if (PrefUtils.getBoolean(getActivity(), "UPDATE_UI_UPCOMING_FRAGMENT", false)) {
            //first show update locally
            initVariables(true);
            initViews();
            PrefUtils.putBoolean(getActivity(), "UPDATE_UI_UPCOMING_FRAGMENT", false);
        }
    }

}
