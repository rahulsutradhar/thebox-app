package one.thebox.android.adapter.address;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.address.Locality;
import one.thebox.android.R;

/**
 * Created by developers on 02/05/17.
 */

public class LocalitySpinnerAdapter extends ArrayAdapter<Locality> {

    private LayoutInflater inflater;
    private ArrayList<Locality> localities;
    private Context context;

    public LocalitySpinnerAdapter(Context context, ArrayList<Locality> localities) {
        super(context, R.layout.item_row_spinner_locality, localities);
        this.context = context;
        this.localities = localities;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomDropDownView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);

    }


    public View getCustomDropDownView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_row_spinner_locality, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.locality_name);
        if (localities.get(position) != null) {
            textView.setText(localities.get(position).getName() + " - " + localities.get(position).getPincode());
        }
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.warm_grey));
        return view;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_row_spinner_locality, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.locality_name);
        if (localities.get(position) != null) {
            textView.setText(localities.get(position).getName() + " - " + localities.get(position).getPincode());
        }
        textView.setGravity(Gravity.LEFT);
        textView.setTextColor(context.getResources().getColor(R.color.primary_text_color));
        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
