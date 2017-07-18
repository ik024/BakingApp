package com.ik.bakingapp.common.dependencies.components;


import com.ik.bakingapp.common.MyApplication;
import com.ik.bakingapp.common.dependencies.modules.NetworkModule;
import com.ik.bakingapp.common.dependencies.scopes.ApplicationScope;

import dagger.Component;


@ApplicationScope
@Component(
        dependencies = ContextComponent.class,
        modules = NetworkModule.class
)
public interface NetworkComponent {
    void inject(MyApplication target);
}
