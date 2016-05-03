package one.thebox.android.ViewHelper;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;

import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.R;
import one.thebox.android.adapter.DeliverySlotsAdapter;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 03-05-2016.
 */
public class DelayDeliveryBottomSheet {
    private Activity context;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheet;
    private RecyclerView recyclerView;
    private DeliverySlotsAdapter deliverySlotsAdapter;
    private ProgressBar progressBar;

    public DelayDeliveryBottomSheet(Activity context) {
        this.context = context;
        bottomSheetDialog = new BottomSheetDialog(context);
    }

    public void show() {
        bottomSheet = (context).getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);

        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
    }

    public void initViews(){
        recyclerView = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view);
    }

    public void setupRecyclerView(){
        deliverySlotsAdapter = new DeliverySlotsAdapter(context);
        for (int i = 0; i < 10; i++)
            deliverySlotsAdapter.addDeliveryItems(new DeliverySlot());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(deliverySlotsAdapter);
    }

    public void getAllOrders() {
        MyApplication.getAPIService().getMyOrders(PrefUtils.getToken(context))
                .enqueue(new Callback<OrdersApiResponse>() {
                    @Override
                    public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {

                            } else {
                                Toast.makeText(context, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersApiResponse> call, Throwable t) {

                    }
                });
    }
}
