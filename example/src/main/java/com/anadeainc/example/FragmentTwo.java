package com.anadeainc.example;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentTwo extends BaseFragment {

    private TextView eventTwoCountView;
    private int eventTwoCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventBus.registerSubscriber(this, eventBus
                .obtainSubscriber(ExampleEvent.class, exampleEvent -> {
                    eventTwoCount++;
                    eventTwoCountView.setText(String.valueOf(eventTwoCount));
                }).withFilter(exampleEvent -> exampleEvent == ExampleEvent.TWO));

        eventTwoCountView = (TextView) view.findViewById(R.id.fragmentTwo_eventTwoCount);
        messageCountView = (TextView) view.findViewById(R.id.fragmentTwo_eventMessageCount);

        sendToActivity = (Button) view.findViewById(R.id.fragmentTwo_sendButton);
        sendToActivity.setOnClickListener(v -> eventBus.post(FragmentEvent.FRAGMENT_TWO));
    }

}
