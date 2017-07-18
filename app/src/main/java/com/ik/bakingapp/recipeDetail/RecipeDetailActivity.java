package com.ik.bakingapp.recipeDetail;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.recipeDetail.ingredients.RecipeIngredientFragment;
import com.ik.bakingapp.recipeDetail.steps.RecipeStepFragment;

import timber.log.Timber;

public class RecipeDetailActivity extends AppCompatActivity implements
        RecipeIngredientFragment.OnFragmentInteractionListener,
        RecipeStepFragment.OnFragmentInteractionListener, LifecycleOwner {

    public static final String ACTION_CREATE_NEW_STACK = "action_create_new_stack";
    private LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    private Recipe mSelectedRecipe;
    private FragmentManager mFragmentManager;
    private SelectedRecipeViewModel mSelectedRecipeViewModel;
    private int mClickedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            getSupportActionBar().show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_recipe_detail);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        SelectedRecipeViewModel.Factory factory = new SelectedRecipeViewModel.Factory();
        mSelectedRecipeViewModel = ViewModelProviders.of(this, factory).get(SelectedRecipeViewModel.class);
       if (savedInstanceState == null) {

           mClickedPosition = bundle.getInt("position");
           mSelectedRecipe = bundle.getParcelable("selectedRecipe");

           mSelectedRecipeViewModel.setSelectedPosition(mClickedPosition);
           if (mClickedPosition > 0)
               mSelectedRecipeViewModel.setSteps(mSelectedRecipe.getSteps()[mClickedPosition-1]);
           else
               mSelectedRecipeViewModel.setIngredients(mSelectedRecipe.getIngredients());

           mSelectedRecipeViewModel.setSelectedRecipe(mSelectedRecipe);

           initView(mSelectedRecipe, mClickedPosition);

       } else {

           mClickedPosition = mSelectedRecipeViewModel.getSelectedPosition();
           mSelectedRecipeViewModel.getSelectedRecipeLiveData().observe(this, new Observer<Recipe>() {
               @Override
               public void onChanged(@Nullable Recipe recipe) {
                   if (recipe != null) {
                       mSelectedRecipe = recipe;
                   }
               }
           });
       }
    }

    private void initView(Recipe selectedRecipe, int clickedPosition){
        getSupportActionBar().setTitle(selectedRecipe.getName());

        mFragmentManager = getSupportFragmentManager();

        /*If clicked position is zero then show ingredient fragment else step fragment*/
        if(clickedPosition == 0){
            Fragment fragIngredient = RecipeIngredientFragment.newInstance();
            doFragmentNavigation(fragIngredient, "ingredient_frag");
        } else {
            Fragment fragStep = RecipeStepFragment.newInstance();
            doFragmentNavigation(fragStep, "step_frag");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        Timber.i("onResume state: "+mRegistry.getCurrentState().isAtLeast(Lifecycle.State.STARTED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        Timber.i("onPause state: "+mRegistry.getCurrentState().isAtLeast(Lifecycle.State.STARTED));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
    }

    @Override
    public void nextStep(int nextStepId) {
        mSelectedRecipeViewModel.setSteps(mSelectedRecipe.getSteps()[nextStepId]);
        Fragment fragStep = RecipeStepFragment.newInstance();
        doFragmentNavigation(fragStep, "step_frag");
    }

    @Override
    public void prevStep(int prevStepId) {
        mSelectedRecipeViewModel.setSteps(mSelectedRecipe.getSteps()[prevStepId]);
        Fragment fragStep = RecipeStepFragment.newInstance();
        doFragmentNavigation(fragStep, "step_frag");
    }

    private void doFragmentNavigation(Fragment fragToShow, String tag){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragToShow, tag);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (shouldUpRecreateTask(this)) {
                    fillUpIntentWithExtras(upIntent);
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean shouldUpRecreateTask(Activity from){
        String action = from.getIntent().getAction();
        return action != null && action.equals(ACTION_CREATE_NEW_STACK);
    }

    private void fillUpIntentWithExtras(Intent intent){
        Bundle bundle = new Bundle();
        bundle.putInt("position", mClickedPosition);
        bundle.putParcelable("selectedRecipe", mSelectedRecipe);
        intent.putExtras(bundle);
    }

    @Override
    public Lifecycle getLifecycle() {
        return mRegistry;
    }
}
