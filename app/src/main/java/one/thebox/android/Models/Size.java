package one.thebox.android.Models;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class Size {
    private int height;
    private int width;

    public Size(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
