package com.example.bin.myapplication.mvp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * description
 *
 * @author bin
 * @date 2018/4/13 10:07
 */
public abstract class ControllerFragment extends BaseCleanFragment implements Controller, Backable {

    public ArrayMap<Class, UIController> controllerArrayMap = new ArrayMap<>();
    private List<FragmentLifecycle> mFragmentLifecycles = new ArrayList<>();

    public void addFragmentLifecycle(FragmentLifecycle fragmentLifecycle) {
        if (mFragmentLifecycles == null) {
            mFragmentLifecycles = new ArrayList<>();
        }
        mFragmentLifecycles.add(fragmentLifecycle);
    }

    @Override
    public <T extends UIController> T getUIController(Class<T> cls) {
        return (T) controllerArrayMap.get(cls);
    }

    @Override
    public <T extends UIController> void addUIController(T t) {
        controllerArrayMap.put(t.getClass(), t);
        if (t instanceof FragmentLifecycle) {
            addFragmentLifecycle((FragmentLifecycle) t);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUIController();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (mFragmentLifecycles != null && mFragmentLifecycles.size() > 0) {
            for (FragmentLifecycle fragmentLifecycle : mFragmentLifecycles) {
                fragmentLifecycle.onCreateView(view);
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFragmentLifecycles != null && mFragmentLifecycles.size() > 0) {
            for (FragmentLifecycle fragmentLifecycle : mFragmentLifecycles) {
                fragmentLifecycle.onDestroyView();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Set<Map.Entry<Class, UIController>> set = controllerArrayMap.entrySet();
        for (Map.Entry<Class, UIController> controllerEntry : set) {
            UIController uiController = controllerEntry.getValue();
            if (uiController != null) uiController.onSaveState(outState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Set<Map.Entry<Class, UIController>> set = controllerArrayMap.entrySet();
        for (Map.Entry<Class, UIController> controllerEntry : set) {
            UIController uiController = controllerEntry.getValue();
            if (uiController != null) uiController.onStateRestored(savedInstanceState);
        }
    }

    @Override
    public void initView() {
        Set<Map.Entry<Class, UIController>> set = controllerArrayMap.entrySet();
        for (Map.Entry<Class, UIController> controllerEntry : set) {
            UIController uiController = controllerEntry.getValue();
            if (uiController != null) uiController.initView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Set<Map.Entry<Class, UIController>> set = controllerArrayMap.entrySet();
        for (Map.Entry<Class, UIController> controllerEntry : set) {
            UIController uiController = controllerEntry.getValue();
            if (uiController != null) {
                uiController.onResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        boolean hasBackAction = false;
        Set<Map.Entry<Class, UIController>> set = controllerArrayMap.entrySet();
        for (Map.Entry<Class, UIController> controllerEntry : set) {
            UIController uiController = controllerEntry.getValue();
            if (uiController != null && uiController.onBackPressed()) {
                hasBackAction = true;
            }
        }
        return hasBackAction;
    }

    public abstract void initUIController();

    public interface FragmentLifecycle {
        void onCreateView(View view);

        void onDestroyView();
    }
}