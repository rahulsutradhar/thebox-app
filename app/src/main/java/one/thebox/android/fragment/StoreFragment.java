package one.thebox.android.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.FrameLayout;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.StoreRecyclerAdapter;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static one.thebox.android.fragment.SearchDetailFragment.BROADCAST_EVENT_TAB;
import static one.thebox.android.fragment.SearchDetailFragment.EXTRA_NUMBER_OF_TABS;

public class StoreFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    private RecyclerView recyclerView;
    private StoreRecyclerAdapter storeRecyclerAdapter;
    private View rootLayout;
    private GifImageView progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView noOfItemsInCart;
    private RealmList<Box> boxes = new RealmList<>();
    private AppBarObserver appBarObserver;
    private FrameLayout fabHolder;
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


    public StoreFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootLayout == null) {
            rootLayout = inflater.inflate(R.layout.fragment_store, container, false);
            initVariables();
            initViews();
            getMyBoxes();
            setupAppBarObserver();
            if (!boxes.isEmpty()) {
                setupRecyclerView();
            }
            onTabEvent(new TabEvent(CartHelper.getNumberOfItemsInCart()));
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
        Realm realm = MyApplication.getRealm();
        RealmResults<UserItem> items = realm.where(UserItem.class).equalTo("boxId", boxId).findAll();
        List<UserItem> list = new ArrayList<>();
        list.addAll(items);
        return list;
    }

    private void initVariables() {
        Realm realm = MyApplication.getRealm();
        RealmQuery<Box> query = realm.where(Box.class);
        RealmResults<Box> realmResults = query.notEqualTo(Box.FIELD_ID, 0).findAll();
        RealmList<Box> boxes = new RealmList<>();
        boxes.addAll(realmResults.subList(0, realmResults.size()));
        this.boxes.clear();
        this.boxes.addAll(realm.copyFromRealm(boxes));
//        setUpBoxes();
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
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        storeRecyclerAdapter = new StoreRecyclerAdapter(getActivity());
        storeRecyclerAdapter.setBoxes(boxes);
        recyclerView.setAdapter(storeRecyclerAdapter);
    }

    private void initViews() {
        this.progressBar = (GifImageView) rootLayout.findViewById(R.id.progress_bar);
        this.recyclerView = (RecyclerView) rootLayout.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        this.floatingActionButton = (FloatingActionButton) rootLayout.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
            }
        });
        noOfItemsInCart = (TextView) rootLayout.findViewById(R.id.no_of_items_in_cart);
        fabHolder = (FrameLayout) rootLayout.findViewById(R.id.fab_holder);
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
        Log.d("From Store", "Calling gogetmybox");
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
                                boxes.clear();
                                boxes.addAll(response.body().getBoxes());
                                setupRecyclerView();
                                storeToRealm();
                            }
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

    private void storeToRealm() {
        final Realm superRealm = MyApplication.getRealm();
        superRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Box box : boxes) {
                    List<UserItem> items = box.getAllItemInTheBox();
                    for (UserItem item : items) {
                        item.setBoxId(box.getBoxId());
                    }
                }
                realm.copyToRealmOrUpdate(boxes);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (null != getActivity()) {
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
        fabHolder.setTranslationY(-offset);
    }

    public void onTabEvent(TabEvent tabEvent) {
        if (getActivity() == null) {
            return;
        }
        if (tabEvent.getNumberOfItemsInCart() > 0) {
            noOfItemsInCart.setVisibility(View.VISIBLE);
            noOfItemsInCart.setText(String.valueOf(tabEvent.getNumberOfItemsInCart()));
        } else {
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

}
