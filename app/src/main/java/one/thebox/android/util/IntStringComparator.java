package one.thebox.android.util;

import java.util.Comparator;

import one.thebox.android.Models.IntStringObject;

/**
 * Created by Ajeet Kumar Meena on 25-05-2016.
 */

public class IntStringComparator implements Comparator<IntStringObject>{
    @Override
    public int compare(IntStringObject lhs, IntStringObject rhs) {
        if(lhs.getInteger()>rhs.getInteger()){
            return 1;
        } else if(lhs.getInteger() == rhs.getInteger()) {
            return 0;
        }
        else {
            return -1;
        }
    }
}
