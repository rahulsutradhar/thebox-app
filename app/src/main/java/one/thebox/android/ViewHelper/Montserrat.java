package one.thebox.android.ViewHelper;

import android.content.Context;
import android.graphics.Typeface;

public class Montserrat {

    public static final int MONTSERRAT_LIGHT = 0;
    public static final int MONTSERRAT_REGULAR = 1;
    public static final int MONTSERRAT_SEMI_BOLD = 2;
    public static final int MONTSERRAT_ULTRALIGHT = 3;
    public static final int MONTSERRAT_BOLD = 4;

    public static Typeface sMontserratLight;
    public static Typeface sMontserratRegular;
    public static Typeface sMontserratSemibold;
    public static Typeface sMontserratUltralight;
    private static Typeface sMontserratBold;

    public static Typeface getRoboto(Context context, int typeface) {
        switch (typeface) {
            case Montserrat.MONTSERRAT_BOLD:
                if( Montserrat.sMontserratBold == null ) {
                    Montserrat.sMontserratBold = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-SemiBold.otf");
                }
                return Montserrat.sMontserratBold;
            case Montserrat.MONTSERRAT_LIGHT:
                if (Montserrat.sMontserratLight == null) {
                    Montserrat.sMontserratLight = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf");
                }
                return Montserrat.sMontserratLight;
            case Montserrat.MONTSERRAT_REGULAR:
                if (Montserrat.sMontserratRegular == null) {
                    Montserrat.sMontserratRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf");
                }
                return Montserrat.sMontserratRegular;
            case Montserrat.MONTSERRAT_SEMI_BOLD:
                if (Montserrat.sMontserratSemibold == null) {
                    Montserrat.sMontserratSemibold = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-SemiBold.otf");
                }
                return Montserrat.sMontserratSemibold;
            case Montserrat.MONTSERRAT_ULTRALIGHT:
                if (Montserrat.sMontserratUltralight == null) {
                    Montserrat.sMontserratUltralight = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-UltraLight.otf");
                }
                return Montserrat.sMontserratUltralight;
            default:
                if (Montserrat.sMontserratRegular == null) {
                    Montserrat.sMontserratRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf");
                }
                return Montserrat.sMontserratRegular;
        }
    }
}