package one.thebox.android.api.Responses;

import java.util.List;

import one.thebox.android.Models.address.Locality;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class LocalitiesResponse extends ApiResponse {

    private List<Locality> localities;

    public LocalitiesResponse(List<Locality> localities) {
        this.localities = localities;
    }

    public List<Locality> getLocalities() {
        return localities;
    }

    public void setLocalities(List<Locality> localities) {
        this.localities = localities;
    }
}
