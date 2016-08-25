package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

import one.thebox.android.Models.AddressAndOrder;

/**
 * Created by vaibhav on 15/08/16.
 */
public class RescheduleRequestBody implements Serializable {

        @SerializedName("date")
        private Date reschedule_to;

        public RescheduleRequestBody(Date reschedule_to) {
            this.reschedule_to = reschedule_to;
        }
}


