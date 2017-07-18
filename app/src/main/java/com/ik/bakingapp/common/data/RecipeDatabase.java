package com.ik.bakingapp.common.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import timber.log.Timber;

/**
 * Created by Ismail.Khan2 on 7/12/2017.
 */

@Database(version = RecipeDatabase.VERSION)
public final class RecipeDatabase {
    public static final int VERSION = 18;

    @Table(RecipeListColumns.class)
    public static final String RECIPE_LISTS = "lists";

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        Timber.i("onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS "+ RECIPE_LISTS);
        com.ik.bakingapp.common.data.generated.RecipeDatabase.getInstance(context).onCreate(db);
    }

}
