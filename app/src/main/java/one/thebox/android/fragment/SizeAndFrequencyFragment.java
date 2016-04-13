package one.thebox.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.Models.SizeAndFrequency;
import one.thebox.android.R;
import one.thebox.android.adapter.SizeAndFrequencyAdapter;

public class SizeAndFrequencyFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private SizeAndFrequencyAdapter sizeAndFrequencyAdapter;

    public SizeAndFrequencyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_choose_size_and_frequency, container, false);
        initViews();
        setupRecyclerView();
        return rootView;
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        sizeAndFrequencyAdapter = new SizeAndFrequencyAdapter(getActivity());
        for (int i = 0; i < 10; i++) {
            sizeAndFrequencyAdapter.addSizeAndFrequency(new SizeAndFrequency());
        }
        recyclerView.setAdapter(sizeAndFrequencyAdapter);

    }

}