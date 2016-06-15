package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BoxDetail extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("photo_url")
    private String photoUrl;

    public BoxDetail() {
    }

    public BoxDetail(int id, String title, String photoUrl) {
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        BoxDetail boxDetail = (BoxDetail) o;
        return this.id == boxDetail.getId() && this.title.equals(boxDetail.getTitle()) && this.photoUrl.equals(boxDetail.getPhotoUrl());
    }
}