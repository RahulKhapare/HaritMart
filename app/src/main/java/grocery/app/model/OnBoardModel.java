package grocery.app.model;

import android.graphics.drawable.Drawable;

public class OnBoardModel {

    private Drawable image;
    private String title;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
