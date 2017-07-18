package com.ik.bakingapp.recipeDetail;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.NetworkConnectionListener;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.recipeDetail.ingredients.RecipeIngredientFragment;
import com.ik.bakingapp.recipeDetail.steps.RecipeStepFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeDetailListActivity extends AppCompatActivity implements
        RecipeDetailListFragment.OnFragmentInteractionListener,
        RecipeIngredientFragment.OnFragmentInteractionListener,
        RecipeStepFragment.OnFragmentInteractionListener,
        LifecycleRegistryOwner {


    @BindView(R.id.ll_detail_container)
    @Nullable
    LinearLayout llDetailContainer;
    private boolean mIsTablet;
    private Recipe mSelectedRecipe;
    private LifecycleRegistry registry = new LifecycleRegistry(this);
    private FragmentManager mFragmentManager;
    private SelectedRecipeViewModel mSelectedRecipeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_list);
        ButterKnife.bind(this);

        SelectedRecipeViewModel.Factory factory = new SelectedRecipeViewModel.Factory();
        mSelectedRecipeViewModel =
                ViewModelProviders.of(RecipeDetailListActivity.this, factory)
                        .get(SelectedRecipeViewModel.class);
        final int selectedPosition;

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            selectedPosition = bundle.getInt("position");
            mSelectedRecipe = bundle.getParcelable("selectedRecipe");

            mSelectedRecipeViewModel.setSelectedPosition(selectedPosition);

            if (selectedPosition > 0)
                mSelectedRecipeViewModel.setSteps(mSelectedRecipe.getSteps()[selectedPosition-1]);
            else
                mSelectedRecipeViewModel.setIngredients(mSelectedRecipe.getIngredients());

            mSelectedRecipeViewModel.setSelectedRecipe(mSelectedRecipe);

            initView(selectedPosition);
        } else {
            selectedPosition = mSelectedRecipeViewModel.getSelectedPosition();
            mSelectedRecipeViewModel.getSelectedRecipeLiveData().observe(this, new Observer<Recipe>() {
                @Override
                public void onChanged(@Nullable Recipe recipe) {
                    if (recipe != null) {
                        mSelectedRecipe = recipe;
                        initView(selectedPosition);
                    }
                }
            });
        }
    }

    private void initView(int selectedPosition){
        mIsTablet = llDetailContainer != null;
        if (mIsTablet) {
            mFragmentManager = getSupportFragmentManager();
            if (selectedPosition == 0) {
                Fragment fragIngredient = RecipeIngredientFragment.newInstance();
                doFragmentNavigation(fragIngredient, "ingredient_frag");
            } else {
                Fragment fragStep = RecipeStepFragment.newInstance();
                doFragmentNavigation(fragStep, "step_frag");
            }
        }
    }
    @Override
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onItemClicked(int position) {
        if (mIsTablet) {
            mSelectedRecipeViewModel.setSelectedPosition(position);
            mSelectedRecipeViewModel.setSelectedRecipe(mSelectedRecipe);
            if (position > 0)
                mSelectedRecipeViewModel.setSteps(mSelectedRecipe.getSteps()[position - 1]);

            if (position == 0) {
                Fragment fragIngredient = RecipeIngredientFragment.newInstance();
                doFragmentNavigation(fragIngredient, "ingredient_frag");
            } else {
                Fragment fragStep = RecipeStepFragment.newInstance();
                doFragmentNavigation(fragStep, "step_frag");
            }
        } else {
            gotoDetailActivity(position);
        }

    }

    private void gotoDetailActivity(int position) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putParcelable("selectedRecipe", mSelectedRecipe);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return registry;
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

    private void doFragmentNavigation(Fragment fragToShow, String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ll_detail_container, fragToShow, tag);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
