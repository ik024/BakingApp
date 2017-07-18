package com.ik.bakingapp.common.data;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ik.bakingapp.R;
import com.ik.bakingapp.common.dependencies.components.ContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerSharedPrefComponent;
import com.ik.bakingapp.common.dependencies.components.SharedPrefComponent;
import com.ik.bakingapp.common.dependencies.modules.ContextModule;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.widget.RecipeWidgetProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;

import timber.log.Timber;

public class DatabaseIntentService extends IntentService {
    private static final String ACTION_INSERT_BULK = "action_insert_bulk";
    private static final String ACTION_GET_RECIPE_LIST = "action_get_recipe_list";

    private static final String EXTRA_CONTENT_VALUES_LIST = "content_values_list";

    public DatabaseIntentService() {
        super("DatabaseIntentService");
    }

    public static void startActionBulkInsert(Context context, ArrayList<Recipe> recipeList) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_INSERT_BULK);
        intent.putParcelableArrayListExtra(EXTRA_CONTENT_VALUES_LIST, recipeList);
        context.startService(intent);
    }

    public static void startActionGetRecipeList(Context context) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_GET_RECIPE_LIST);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INSERT_BULK.equals(action)) {
                final ArrayList<Recipe> recipeList =
                        intent.getParcelableArrayListExtra(EXTRA_CONTENT_VALUES_LIST);
                handleActionBulkInsert(recipeList);
            } else if (ACTION_GET_RECIPE_LIST.equals(action)) {
                handleActionGetRecipeList();
            }
        }
    }


    private void handleActionBulkInsert(ArrayList<Recipe> recipeList) {
        ContentValues[] valuesList = new ContentValues[recipeList.size()];
        Gson gson = new Gson();
        Type type = new TypeToken<Recipe>() {
        }.getType();
        int index = 0;
        for (Recipe recipe : recipeList) {
            String recipeObject = gson.toJson(recipe, type);
            ContentValues values = new ContentValues();
            values.put(RecipeListColumns.RECIPE_ID, recipe.getId());
            values.put(RecipeListColumns.RECIPE_TITLE, recipe.getName());
            values.put(RecipeListColumns.RECIPE_OBJECT, recipeObject);
            valuesList[index] = values;
            index++;
        }

        try {
            getContentResolver().bulkInsert(
                    RecipeProvider.Lists.RECIPE_LISTS_URI, valuesList);

        } catch (SQLiteConstraintException e) {
            Timber.e(e.getMessage());
        }finally{
            handleActionGetRecipeList();
        }

    }

    private void handleActionGetRecipeList() {
        Recipe recipe;

        Cursor cursor = getContentResolver().query(RecipeProvider.Lists.RECIPE_LISTS_URI,
                null,
                null,
                null,
                null);


        if (cursor != null && cursor.getCount() > 0) {
            ContextComponent contextComponent = DaggerContextComponent.builder()
                    .contextModule(new ContextModule(this)).build();
            SharedPrefComponent component = DaggerSharedPrefComponent.builder()
                    .contextComponent(contextComponent)
                    .build();
            MySharedPref sharedPreferences = component.getSharedPref();

            cursor.moveToPosition(sharedPreferences.getLastVisitedRecipePosition());
            int idIndex = cursor.getColumnIndex(RecipeListColumns.RECIPE_ID);
            int titleIndex = cursor.getColumnIndex(RecipeListColumns.RECIPE_TITLE);
            int recipeIndex = cursor.getColumnIndex(RecipeListColumns.RECIPE_OBJECT);

            long id = cursor.getLong(idIndex);
            String recipeName = cursor.getString(titleIndex);
            String recipeObject = cursor.getString(recipeIndex);
            Gson gson = new Gson();
            recipe = gson.fromJson(recipeObject, Recipe.class);

            /*Update the widget when data is inserted in the db*/
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gv_widget);

            RecipeWidgetProvider.updateRecipeWidget(this, appWidgetManager, appWidgetIds, recipe);

        }


    }
}
