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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.SwapEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.R;
import one.thebox.android.adapter.MyBoxRecyclerAdapter;
import one.thebox.android.adapter.SwapAdapter;

public class MyBoxesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyBoxRecyclerAdapter myBoxRecyclerAdapter;
    private View rootLayout;
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
        setupRecyclerView();
        return rootLayout;
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        for (int i = 0; i < 5; i++) {
            ArrayList<Box.SmartItem> smartItems = new ArrayList<>();
            ArrayList<Box.BoxItem> boxItems = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                smartItems.add(new Box.SmartItem());
                boxItems.add(new Box.BoxItem());
            }
            boxes.add(new Box(smartItems, boxItems));
        }
        myBoxRecyclerAdapter = new MyBoxRecyclerAdapter(getActivity(), getChildFragmentManager());
        myBoxRecyclerAdapter.setBoxes(boxes);
        recyclerView.setAdapter(myBoxRecyclerAdapter);
    }

    private void initViews() {
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
}
