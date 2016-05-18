package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Models.Box;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.MyBoxRecyclerAdapter;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBoxesFragment extends Fragment {

    //private LinearLayout stickyHolder;
    int totalScroll = 0;
    private RecyclerView recyclerView;
    private MyBoxRecyclerAdapter myBoxRecyclerAdapter;
    private View rootLayout;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private RealmList<Box> boxes = new RealmList<>();

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
            if (!boxes.isEmpty()) {
                setupRecyclerView();
            }
            getMyBoxes();
        }
        return rootLayout;
    }

    private void initVariables() {
        if (!boxes.isEmpty()) {
            Realm realm = MyApplication.getRealm();
            RealmQuery<Box> query = realm.where(Box.class);
            RealmResults<Box> realmResults = query.notEqualTo(Box.FIELD_ID, 0).findAll();
            boxes.addAll(realmResults.subList(0, realmResults.size()));
        }
    }

    private void setupRecyclerView() {
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
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 0));
            }
        });
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
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
    }

    @Override
    public void onStop() {
        super.onStop();
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
}
