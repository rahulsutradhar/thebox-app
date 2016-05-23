package one.thebox.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.Box;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.MyBoxRecyclerAdapter;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBoxesFragment extends Fragment implements AppBarObserver.OnOffsetChangeListener {

    //private LinearLayout stickyHolder;
    int totalScroll = 0;
    private RecyclerView recyclerView;
    private MyBoxRecyclerAdapter myBoxRecyclerAdapter;
    private View rootLayout;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView noOfItemsInCart;
    private RealmList<Box> boxes = new RealmList<>();
    private AppBarObserver appBarObserver;
    private FrameLayout fabHolder;
    private boolean isRegistered;

    public MyBoxesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getToolbar().setTitle("My Boxes");
        if (rootLayout == null) {
            this.rootLayout = inflater.inflate(R.layout.fragment_my_boxes, container, false);
            initVariables();
            initViews();
            setupAppBarObserver();
            if (!boxes.isEmpty()) {
                setupRecyclerView();
            }
            getMyBoxes();
        }
        return rootLayout;
    }

    private void initVariables() {
        Realm realm = MyApplication.getRealm();
        RealmQuery<Box> query = realm.where(Box.class);
        RealmResults<Box> realmResults = query.notEqualTo(Box.FIELD_ID, 0).findAll();
        boxes.addAll(realmResults.subList(0, realmResults.size()));
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
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        myBoxRecyclerAdapter = new MyBoxRecyclerAdapter(getActivity());
        myBoxRecyclerAdapter.setBoxes(boxes);
        recyclerView.setAdapter(myBoxRecyclerAdapter);
        final int scrollingItemNumber = 0;
       /* stickyHolder.setVisibility(View.GONE);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScroll = totalScroll + dy;
                if (totalScroll > myBoxRecyclerAdapter.getStickyHeaderHeight()) {
                    Log.d("MyBox", String.valueOf(linearLayoutManager.findViewByPosition(scrollingItemNumber).getMeasuredHeight()));
                    stickyHolder.setVisibility(View.VISIBLE);
                } else {
                    if (myBoxRecyclerAdapter.getStickyHeaderHeight() != 0) {
                        stickyHolder.setAlpha((float) totalScroll / (float) myBoxRecyclerAdapter.getStickyHeaderHeight());
                    }
                }
            }
        });*/
    }

    private void initViews() {
        this.progressBar = (ProgressBar) rootLayout.findViewById(R.id.progress_bar);
        this.recyclerView = (RecyclerView) rootLayout.findViewById(R.id.recycler_view);
        this.floatingActionButton = (FloatingActionButton) rootLayout.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
            }
        });
        noOfItemsInCart = (TextView) rootLayout.findViewById(R.id.no_of_items_in_cart);
        onTabEvent(new TabEvent(CartHelper.getNumberOfItemsInCart()));
        fabHolder = (FrameLayout) rootLayout.findViewById(R.id.fab_holder);
/*
        stickyHolder = (LinearLayout) rootLayout.findViewById(R.id.holder);
*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
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
        MyApplication.getAPIService().getMyBoxes(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<MyBoxResponse>() {
                    @Override
                    public void onResponse(Call<MyBoxResponse> call, Response<MyBoxResponse> response) {
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
                    }
                });
    }

    private void storeToRealm() {
        final Realm superRealm = MyApplication.getRealm();
        for (final Box box : boxes) {
            superRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(box);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    //  Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
        fabHolder.setTranslationY(-offset);
    }

    @Subscribe
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
}
