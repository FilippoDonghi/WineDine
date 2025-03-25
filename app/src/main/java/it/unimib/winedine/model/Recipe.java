package it.unimib.winedine.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Recipe implements Parcelable {
    private int id;
    private String title;
    private String image;
    private String imageType;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public String getImageType() { return imageType; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setImage(String image) { this.image = image; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.image);
        parcel.writeString(this.imageType);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.title = source.readString();
        this.image = source.readString();
        this.imageType = source.readString();
    }
    protected Recipe(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.image = in.readString();
        this.imageType = in.readString();
    }
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>(){
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}

