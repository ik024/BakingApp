package com.ik.bakingapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.data.DatabaseIntentService;
import com.ik.bakingapp.common.data.RecipeProvider;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.recipeDetail.RecipeDetailActivity;
import com.ik.bakingapp.recipeList.RecipeListActivity;

import java.util.List;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {
        Bundle option = appWidgetManager.getAppWidgetOptions(appWidgetId);

        int width = option.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews rv;

        if (width < 300){
            rv = getSingleRemoteView(context, recipe);
        } else {
            rv = getGridViewRemoteView(context);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        DatabaseIntentService.startActionGetRecipeList(context);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        DatabaseIntentService.startActionGetRecipeList(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static RemoteViews getGridViewRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, GridRemoteViewsService.class);
        views.setRemoteAdapter(R.id.gv_widget, intent);
        // Set the RecipeDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, RecipeDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.gv_widget, appPendingIntent);
        /*// Handle empty gardens
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);*/
        return views;
    }

    private static RemoteViews getSingleRemoteView(Context context, Recipe recipe) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_item);
        remoteViews.setTextViewText(R.id.tv_widget_recipe_title, recipe.getName());
        remoteViews.setOnClickPendingIntent(R.id.tv_widget_recipe_title, getPendingIntent(context,recipe));
        return remoteViews;
    }

    public static void updateRecipeWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Recipe recipe) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }



    private static PendingIntent getPendingIntent(Context context, Recipe recipe){
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.setAction(RecipeDetailActivity.ACTION_CREATE_NEW_STACK);
        Bundle bundle = new Bundle();
        bundle.putInt("position", 0);
        bundle.putParcelable("selectedRecipe", recipe);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}

