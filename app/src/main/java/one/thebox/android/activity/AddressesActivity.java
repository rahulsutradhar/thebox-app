package one.thebox.android.activity;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import one.thebox.android.Models.Address;
import one.thebox.android.R;
import one.thebox.android.adapter.AddressesAdapter;
import one.thebox.android.adapter.ChangeAddressAdapter;
import one.thebox.android.adapter.EditDeliveryAddressAdapter;

public class AddressesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AddressesAdapter addressesAdapter;
    private TextView buttonCreateNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);
        initViews();
        setTitle("Addresses");
        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        addressesAdapter = new AddressesAdapter(this);
        for (int i = 0; i < 10; i++) {
            addressesAdapter.addAddress(new Address());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addressesAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        buttonCreateNew = (TextView) findViewById(R.id.button_create_new);
        buttonCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAddressBottomSheet();
            }
        });
    }

    @Override
    void onClick(int id) {
        switch (id) {
            case R.id.create_new_button: {
                openAddAddressBottomSheet();
                break;
            }
        }
    }

    private void openAddAddressBottomSheet() {
        View bottomSheet = getLayoutInflater().inflate(R.layout.layout_add_address, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
    }
}
