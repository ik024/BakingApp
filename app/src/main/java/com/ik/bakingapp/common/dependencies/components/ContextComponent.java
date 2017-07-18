package com.ik.bakingapp.common.dependencies.components;

import android.content.Context;


import com.ik.bakingapp.common.dependencies.modules.ContextModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = ContextModule.class
)
public interface ContextComponent {
    Context getContext();
}
