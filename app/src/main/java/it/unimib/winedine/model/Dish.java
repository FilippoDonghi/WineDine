package it.unimib.winedine.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Dish implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    private String id;
    private String title;
    private String description;
    private String image;
    private String imageType;


public Dish(){};
    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String imageUrl) {
        this.image = image;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageType() {
        return imageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
    parcel.writeString(this.id);
    parcel.writeString(this.title);
    parcel.writeString(this.image);
    parcel.writeString(this.imageType);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.image = source.readString();
        this.imageType = source.readString();
    }

    protected Dish(Parcel in) {
        this.id= in.readString();
        this.title= in.readString();
        this.image= in.readString();
        this.imageType= in.readString();
    }

    public static final Creator<Dish> CREATOR = new Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel source) {
            return new Dish(source);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

}
