package com.ik.bakingapp.recipeDetail;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.ik.bakingapp.R;
import com.ik.bakingapp.recipeList.RecipeListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RecipeDetailListActivityTest {
    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityRule =
             new ActivityTestRule<>(RecipeListActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Clicking on 0th position must show the RecipeIngredientFragment
     */

    @Test
    public void onFirstItemClickedOfRecipeDetailList_DisplaysIngredientFragment(){
        onData(anything()).inAdapterView(withId(R.id.gv_recipe_list)).atPosition(1).perform(click());
        onView(withId(R.id.rv_recipe_details))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.rv_recipe_ingredients)).check(matches(isDisplayed()));
    }

    /**
     * Clicking on >0 position show show the RecipeStepFragment
     */
    @Test
    public void onSecondOrGreaterItemClickedOfRecipeDetailList_DisplaysStepFragment(){
        onData(anything()).inAdapterView(withId(R.id.gv_recipe_list)).atPosition(1).perform(click());
        onView(withId(R.id.rv_recipe_details))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.ll_step_root)).check(matches(isDisplayed()));
    }

}