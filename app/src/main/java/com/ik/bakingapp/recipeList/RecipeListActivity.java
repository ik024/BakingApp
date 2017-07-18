package com.ik.bakingapp.recipeList;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.NetworkConnectionListener;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.recipeDetail.RecipeDetailActivity;
import com.ik.bakingapp.recipeDetail.RecipeDetailListActivity;
import com.ik.bakingapp.recipeDetail.SelectedRecipeViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeListActivity extends AppCompatActivity implements
        RecipeListFragment.OnFragmentInteractionListener, LifecycleOwner {

    private LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @BindView(R.id.layout_offline)
    LinearLayout mOfflineLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        NetworkConnectionListener networkConnectionListener = NetworkConnectionListener.getInstance(this);
        networkConnectionListener.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isOnline) {
                if (isOnline) {
                    mOfflineLayout.setVisibility(View.GONE);
                } else {
                    mOfflineLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRecipeSelected(Recipe selectedRecipe) {
        Intent intent = new Intent(this, RecipeDetailListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", 0);
        bundle.putParcelable("selectedRecipe", selectedRecipe);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public Lifecycle getLifecycle() {
        return mRegistry;
    }
}
