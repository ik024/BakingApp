package com.ik.bakingapp.common.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeSteps implements Parcelable{
    private String videoURL;
    private String description;
    private int id;
    private String shortDescription;
    private String thumbnailURL;

    protected RecipeSteps(Parcel in) {
        videoURL = in.readString();
        description = in.readString();
        id = in.readInt();
        shortDescription = in.readString();
        thumbnailURL = in.readString();
    }

    public static final Creator<RecipeSteps> CREATOR = new Creator<RecipeSteps>() {
        @Override
        public RecipeSteps createFromParcel(Parcel in) {
            return new RecipeSteps(in);
        }

        @Override
        public RecipeSteps[] newArray(int size) {
            return new RecipeSteps[size];
        }
    };

    public String getVideoURL() {
        return this.videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getThumbnailURL() {
        return this.thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoURL);
        dest.writeString(description);
        dest.writeInt(id);
        dest.writeString(shortDescription);
        dest.writeString(thumbnailURL);
    }
}
