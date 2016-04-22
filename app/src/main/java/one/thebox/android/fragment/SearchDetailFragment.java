package one.thebox.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.R;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchDetailFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private String query;
    private static final String EXTRA_QUERY = "extra_query";

    public SearchDetailFragment() {
    }

    public static SearchDetailFragment getInstance(String query){
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_QUERY,query);
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_detail, container, false);
        initViews();
        initVariables();
        getSearchDetails();
        setupRecyclerView();
        return rootView;

    }

    private void initVariables() {
        query = getArguments().getString(EXTRA_QUERY);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        searchDetailAdapter = new SearchDetailAdapter(getActivity());
        for (int i = 0; i < 10; i++) {
            searchDetailAdapter.addBoxItem(new BoxItem());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchDetailAdapter);
    }

    public void getSearchDetails() {
        MyApplication.getAPIService().getSearchResults(PrefUtils.getToken(getActivity()),query)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
    }
}
