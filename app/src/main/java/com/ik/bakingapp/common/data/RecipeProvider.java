package com.ik.bakingapp.common.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Ismail.Khan2 on 7/12/2017.
 */

@ContentProvider(authority = RecipeProvider.AUTHORITY, database = RecipeDatabase.class)
public final class RecipeProvider {
    public static final String AUTHORITY = "com.ik.bakingapp";

    @TableEndpoint(table = RecipeDatabase.RECIPE_LISTS)
    public static class Lists{
        @ContentUri(
                path = "lists",
                type = "vnd.android.cursor.dir/list"
                )
        public static final Uri RECIPE_LISTS_URI = Uri.parse("content://" + AUTHORITY + "/lists");
    }

}
