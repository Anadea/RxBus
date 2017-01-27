package com.anadeainc.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.RxBus;

public abstract class BaseFragment extends Fragment {

    protected Bus eventBus = RxBus.getInstance();

    protected TextView messageCountView;
    protected int messageCount;

    protected Button sendToActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventBus.register(this);
        eventBus.registerSubscriber(this, eventBus.obtainSubscriber(String.class, this::updateStringLabel));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sendToActivity.setOnClickListener(null);
        eventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus = null;
    }

    private void updateStringLabel(String message) {
        messageCount++;
        messageCountView.setText(String.valueOf(messageCount));
    }
}
