package one.thebox.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import one.thebox.android.Models.ExploreItem;
import one.thebox.android.R;
<<<<<<< HEAD
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
=======
import one.thebox.android.ViewHelper.CustomShowcaseView;
>>>>>>> adding_buttons_to_My_Boxes
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.ExploreItemAdapter;
import one.thebox.android.api.Responses.ExploreItemResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreBoxesFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private ExploreItemAdapter exploreItemAdapter;
    private GifImageView progressBar;
    private ArrayList<ExploreItem> exploreItems = new ArrayList<>();
    private ConnectionErrorViewHelper connectionErrorViewHelper;

    public ExploreBoxesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getToolbar().setTitle("Explore Boxes");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_explore_boxes, container, false);
            initViews();
            setupRecyclerView();
            getAllBoxes();
        }
        return rootView;
    }

    private void setupRecyclerView() {
        exploreItemAdapter = new ExploreItemAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        progressBar = (GifImageView) rootView.findViewById(R.id.progress_bar);
        connectionErrorViewHelper = new ConnectionErrorViewHelper(rootView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllBoxes();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getAllBoxes() {
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);
        MyApplication.getAPIService().getAllExploreBoxes(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<ExploreItemResponse>() {
                    @Override
                    public void onResponse(Call<ExploreItemResponse> call, Response<ExploreItemResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(false);
                        if (response.body() != null && response.body().getBoxes() != null) {
                            recyclerView.setVisibility(View.VISIBLE);
                            exploreItems = new ArrayList<ExploreItem>(response.body().getBoxes());
                            exploreItemAdapter.setExploreItems(exploreItems);
                            recyclerView.setAdapter(exploreItemAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ExploreItemResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
        ((MainActivity) getActivity()).getSearchView().getText().clear();
        ((MainActivity) getActivity()).getSearchViewHolder().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(null);
    }
}
