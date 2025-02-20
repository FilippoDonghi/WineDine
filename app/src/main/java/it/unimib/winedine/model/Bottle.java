package it.unimib.winedine.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Bottle implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;
    private String averageRating;
    private String ratingCount;
    private String score;
    private String link;

    private boolean liked;

public Bottle(){};

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getUid() {
            return uid;
    }

        public void setUid(long uid) {
            this.uid = uid;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
    parcel.writeLong(this.uid);
    parcel.writeString(this.id);
    parcel.writeString(this.title);
    parcel.writeString(this.description);
    parcel.writeString(this.price);
    parcel.writeString(this.imageUrl);
    parcel.writeString(this.averageRating);
    parcel.writeString(this.ratingCount);
    parcel.writeString(this.score);
    parcel.writeString(this.link);
    parcel.writeByte(this.liked ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.uid = source.readLong();
        this.id = source.readString();
        this.title = source.readString();
        this.description = source.readString();
        this.price = source.readString();
        this.imageUrl = source.readString();
        this.averageRating = source.readString();
        this.ratingCount = source.readString();
        this.score = source.readString();
        this.link = source.readString();
        this.liked = source.readByte() != 0;
    }

    protected Bottle(Parcel in) {
        this.uid= in.readLong();
        this.id= in.readString();
        this.title= in.readString();
        this.description= in.readString();
        this.price= in.readString();
        this.imageUrl= in.readString();
        this.averageRating= in.readString();
        this.ratingCount= in.readString();
        this.score= in.readString();
        this.link= in.readString();
        this.liked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Bottle> CREATOR = new Parcelable.Creator<Bottle>() {
        @Override
        public Bottle createFromParcel(Parcel source) {
            return new Bottle(source);
        }

        @Override
        public Bottle[] newArray(int size) {
            return new Bottle[size];
        }
    };

}
