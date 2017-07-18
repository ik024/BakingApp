package com.ik.bakingapp.common;

import android.app.Application;

import com.ik.bakingapp.BuildConfig;
import com.ik.bakingapp.common.dependencies.components.ContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerNetworkComponent;
import com.ik.bakingapp.common.dependencies.components.NetworkComponent;
import com.ik.bakingapp.common.dependencies.modules.ContextModule;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import timber.log.Timber;

public class MyApplication extends Application {

    @Inject
    Picasso picasso;
    @Inject
    RecipeApiService apiService;

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        //inject dependencies
        ContextComponent contextComponent = DaggerContextComponent.builder()
                .contextModule(new ContextModule(this)).build();
        NetworkComponent component = DaggerNetworkComponent.builder()
                .contextComponent(contextComponent)
                .build();
        component.inject(this);

    }

    public Picasso getPicasso(){
        return picasso;
    }

    public RecipeApiService getApiService(){
        return apiService;
    }
}
