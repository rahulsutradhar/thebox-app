package one.thebox.android.ViewHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;


import java.util.HashMap;

import one.thebox.android.R;


public class MontserratButton extends Button {

    private Context context;
    private Tracker tracker;

    public MontserratButton(Context context) {
        super(context);
        this.context = context;
        if (isInEditMode()) return;
        parseAttributes(null);
    }

    public MontserratButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (isInEditMode()) return;
        parseAttributes(attrs);
    }

    public MontserratButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        if (isInEditMode()) return;
        parseAttributes(attrs);
    }

    private void parseAttributes(AttributeSet attrs) {
        int typeface;
        if (attrs == null) { //Not created from xml
            typeface = Montserrat.MONTSERRAT_REGULAR;
        } else {
            TypedArray values = getContext().obtainStyledAttributes(attrs, R.styleable.MontserratTextView);
            typeface = values.getInt(R.styleable.MontserratTextView_typeface, Montserrat.MONTSERRAT_REGULAR);
            values.recycle();
        }
        setTypeface(getRoboto(typeface));
    }

    public void setRobotoTypeface(int typeface) {
        setTypeface(getRoboto(typeface));
    }

    private Typeface getRoboto(int typeface) {
        return Montserrat.getRoboto(getContext(), typeface);
    }


}
