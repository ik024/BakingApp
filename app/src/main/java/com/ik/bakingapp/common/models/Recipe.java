package com.ik.bakingapp.common.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable{
    private String image;
    private int servings;
    private String name;
    private RecipeIngredients[] ingredients;
    private int id;
    private RecipeSteps[] steps;


    protected Recipe(Parcel in) {
        image = in.readString();
        servings = in.readInt();
        name = in.readString();
        ingredients = in.createTypedArray(RecipeIngredients.CREATOR);
        id = in.readInt();
        steps = in.createTypedArray(RecipeSteps.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getServings() {
        return this.servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeIngredients[] getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(RecipeIngredients[] ingredients) {
        this.ingredients = ingredients;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RecipeSteps[] getSteps() {
        return this.steps;
    }

    public void setSteps(RecipeSteps[] steps) {
        this.steps = steps;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeInt(servings);
        dest.writeString(name);
        dest.writeTypedArray(ingredients, flags);
        dest.writeInt(id);
        dest.writeTypedArray(steps, flags);
    }
}
