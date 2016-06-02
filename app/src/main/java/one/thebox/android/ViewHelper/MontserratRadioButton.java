package one.thebox.android.ViewHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RadioButton;

import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 01-04-2016.
 */
public class MontserratRadioButton extends RadioButton{


        public MontserratRadioButton(Context context) {
            super(context);
            if( isInEditMode() ) return;
            parseAttributes(null);
        }

        public MontserratRadioButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            if( isInEditMode() ) return;
            parseAttributes(attrs);
        }

        public MontserratRadioButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            if( isInEditMode() ) return;
            parseAttributes(attrs);
        }

        private void parseAttributes(AttributeSet attrs) {
            int typeface;
            if( attrs == null ) { //Not created from xml
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
