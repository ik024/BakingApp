package com.ik.bakingapp.common.dependencies.components;

import com.ik.bakingapp.common.data.MySharedPref;
import com.ik.bakingapp.common.dependencies.scopes.ApplicationScope;

import dagger.Component;

@ApplicationScope
@Component(
        dependencies = ContextComponent.class
)
public interface SharedPrefComponent {
    MySharedPref getSharedPref();
}
