package com.ik.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.ik.bakingapp.R;
import com.ik.bakingapp.common.data.MySharedPref;
import com.ik.bakingapp.common.data.RecipeListColumns;
import com.ik.bakingapp.common.data.RecipeProvider;
import com.ik.bakingapp.common.dependencies.components.ContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerNetworkComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerSharedPrefComponent;
import com.ik.bakingapp.common.dependencies.components.NetworkComponent;
import com.ik.bakingapp.common.dependencies.components.SharedPrefComponent;
import com.ik.bakingapp.common.dependencies.modules.ContextModule;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.common.models.RecipeIngredients;
import com.ik.bakingapp.recipeDetail.RecipeDetailActivity;

import timber.log.Timber;

public class GridRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;
    MySharedPref sharedPref;
    Recipe recipe;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
        ContextComponent contextComponent = DaggerContextComponent.builder()
                .contextModule(new ContextModule(mContext)).build();
        SharedPrefComponent component = DaggerSharedPrefComponent.builder()
                .contextComponent(contextComponent)
                .build();
        sharedPref = component.getSharedPref();
        Timber.d("context component: "+contextComponent.hashCode());
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(RecipeProvider.Lists.RECIPE_LISTS_URI,
                null,
                null,
                null,
                null);

        int lastVisitedRecipePosition = sharedPref.getLastVisitedRecipePosition();
        mCursor.moveToPosition(lastVisitedRecipePosition);

        int recipeIndex = mCursor.getColumnIndex(RecipeListColumns.RECIPE_OBJECT);

        String recipeObject = mCursor.getString(recipeIndex);
        Gson gson = new Gson();
        recipe = gson.fromJson(recipeObject, Recipe.class);
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null)
            return 0;
        else
           return recipe.getIngredients().length+1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor != null && mCursor.getCount() == 0) return null;

        /*int lastVisitedRecipePosition = sharedPref.getLastVisitedRecipePosition();

        mCursor.moveToPosition(lastVisitedRecipePosition);

        int idIndex = mCursor.getColumnIndex(RecipeListColumns.RECIPE_ID);
        int titleIndex = mCursor.getColumnIndex(RecipeListColumns.RECIPE_TITLE);
        int recipeIndex = mCursor.getColumnIndex(RecipeListColumns.RECIPE_OBJECT);

        long id = mCursor.getLong(idIndex);
        String recipeName = mCursor.getString(titleIndex);
        String recipeObject = mCursor.getString(recipeIndex);
        Gson gson = new Gson();
        Recipe recipe = gson.fromJson(recipeObject, Recipe.class);*/

        RemoteViews viewItem = new RemoteViews(mContext.getPackageName(), R.layout.item_recipe_ingredients);
        if (position == 0) {
            viewItem.setViewVisibility(R.id.tv_recipe_name, View.VISIBLE);
            viewItem.setTextViewText(R.id.tv_recipe_name, ""+recipe.getName());
            viewItem.setViewVisibility(R.id.ll_labels, View.VISIBLE);
            viewItem.setViewVisibility(R.id.ll_values, View.GONE);
        } else {
            viewItem.setViewVisibility(R.id.tv_recipe_name, View.GONE);
            viewItem.setViewVisibility(R.id.ll_labels, View.GONE);
            viewItem.setViewVisibility(R.id.ll_values, View.VISIBLE);
            RecipeIngredients ingredient = recipe.getIngredients()[position-1];
            viewItem.setTextViewText(R.id.tv_quantity_value, ""+ingredient.getQuantity());
            viewItem.setTextViewText(R.id.tv_ingredients_value, ""+ingredient.getIngredient());
            viewItem.setTextViewText(R.id.tv_measure_value, ""+ingredient.getMeasure());
        }

        Intent intent = new Intent();
        intent.setAction(RecipeDetailActivity.ACTION_CREATE_NEW_STACK);
        Bundle args = new Bundle();
        args.putInt("position", 0);
        args.putParcelable("selectedRecipe", recipe);
        intent.putExtras(args);
        viewItem.setOnClickFillInIntent(R.id.ll_ingredients_root, intent);

        return viewItem;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
