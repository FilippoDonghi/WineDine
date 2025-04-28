package it.unimib.winedine.model;

import static java.lang.System.in;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DishAPIResponse implements Parcelable {
    private int id;
    private String image;
    private String imageType;
    private String title;
    private int readyInMinutes;
    private int servings;
    private String sourceUrl;
    private String summary;
    private String spoonacularSourceUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(int readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSpoonacularSourceUrl() {
        return spoonacularSourceUrl;
    }

    public void setSpoonacularSourceUrl(String spoonacularSourceUrl) {
        this.spoonacularSourceUrl = spoonacularSourceUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeInt(this.id);
        dest.writeString(this.image);
        dest.writeString(this.imageType);
        dest.writeString(this.title);
        dest.writeInt(this.readyInMinutes);
        dest.writeInt(this.servings);
        dest.writeString(this.sourceUrl);
        dest.writeString(this.summary);
        dest.writeString(this.spoonacularSourceUrl);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.image = source.readString();
        this.imageType = source.readString();
        this.title = source.readString();
        this.readyInMinutes = source.readInt();
        this.servings = source.readInt();
        this.sourceUrl = source.readString();
        this.summary = source.readString();
        this.spoonacularSourceUrl = source.readString();
    }


    protected DishAPIResponse(Parcel in) {
        this.id = in.readInt();
        this.image = in.readString();
        this.imageType = in.readString();
        this.title = in.readString();
        this.readyInMinutes = in.readInt();
        this.servings = in.readInt();
        this.sourceUrl = in.readString();
        this.summary = in.readString();
        this.spoonacularSourceUrl = in.readString();
    }

    public static final Parcelable.Creator<DishAPIResponse> CREATOR = new Parcelable.Creator<DishAPIResponse>(){
        @Override
        public DishAPIResponse createFromParcel(Parcel in) {
            return new DishAPIResponse(in);
        }

        @Override
        public DishAPIResponse[] newArray(int size) {
            return new DishAPIResponse[size];
        }
    };
}