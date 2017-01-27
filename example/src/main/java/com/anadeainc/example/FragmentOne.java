package com.anadeainc.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentOne extends BaseFragment {

    private TextView eventOneCountView;
    private int eventOneCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventBus.registerSubscriber(this, eventBus
                .obtainSubscriber(ExampleEvent.class, exampleEvent -> {
                    eventOneCount++;
                    eventOneCountView.setText(String.valueOf(eventOneCount));
                }).withFilter(exampleEvent -> exampleEvent == ExampleEvent.ONE));

        eventOneCountView = (TextView) view.findViewById(R.id.fragmentOne_eventOneCount);
        messageCountView = (TextView) view.findViewById(R.id.fragmentOne_eventMessageCount);

        sendToActivity = (Button) view.findViewById(R.id.fragmentOne_sendButton);
        sendToActivity.setOnClickListener(v -> eventBus.post(FragmentEvent.FRAGMENT_ONE));
    }

}
