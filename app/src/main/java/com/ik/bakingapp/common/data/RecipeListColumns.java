package com.ik.bakingapp.common.data;


import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface RecipeListColumns {

    @DataType(INTEGER)
    @Unique
    @NotNull
    String RECIPE_ID = "recipe_id";

    @DataType(TEXT)
    @NotNull
    String RECIPE_TITLE = "title";

    @DataType(TEXT)
    @NotNull
    String RECIPE_OBJECT = "recipe";


}
