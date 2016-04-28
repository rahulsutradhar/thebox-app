package one.thebox.android.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.SwapEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.R;
import one.thebox.android.adapter.MyBoxRecyclerAdapter;
import one.thebox.android.adapter.SwapAdapter;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.api.Responses.AddToMyBoxResponse;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBoxesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyBoxRecyclerAdapter myBoxRecyclerAdapter;
    private View rootLayout;
    private ProgressBar progressBar;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetDialog bottomSheetDialog;
    private SwapAdapter swapAdapter;

    private ArrayList<Box> boxes = new ArrayList<>();

    public MyBoxesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootLayout = inflater.inflate(R.layout.fragment_my_boxes, container, false);
        initViews();
        getMyBoxes();
        return rootLayout;
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        myBoxRecyclerAdapter = new MyBoxRecyclerAdapter(getActivity());
        myBoxRecyclerAdapter.setBoxes(boxes);
        recyclerView.setAdapter(myBoxRecyclerAdapter);
    }

    private void initViews() {
        this.progressBar = (ProgressBar) rootLayout.findViewById(R.id.progress_bar);
        this.recyclerView = (RecyclerView) rootLayout.findViewById(R.id.recycler_view);
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
                            boxes.addAll(new ArrayList<>(response.body().getBoxes()));
                            setupRecyclerView();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyBoxResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
