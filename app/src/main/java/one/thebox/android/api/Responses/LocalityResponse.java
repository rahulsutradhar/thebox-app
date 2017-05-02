package one.thebox.android.api.Responses;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.address.Locality;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 02/05/17.
 */

public class LocalityResponse extends ApiResponse implements Serializable {

    ArrayList<Locality> localities;

    public LocalityResponse() {
    }

    public ArrayList<Locality> getLocalities() {
        return localities;
    }

    public void setLocalities(ArrayList<Locality> localities) {
        this.localities = localities;
    }
}
