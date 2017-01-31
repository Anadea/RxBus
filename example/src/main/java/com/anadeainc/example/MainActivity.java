package com.anadeainc.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.RxBus;
import com.anadeainc.rxbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private Bus eventBus = BusProvider.getInstance();

    private Button eventOneButton;
    private Button eventTwoButton;
    private Button eventMessageButton;

    private TextView receivedLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventBus.register(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentOne_container, new FragmentOne(), FragmentOne.class.getSimpleName())
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentTwo_container, new FragmentTwo(), FragmentTwo.class.getSimpleName())
                    .commit();
        }

        eventOneButton = (Button) findViewById(R.id.eventOne_button);
        eventOneButton.setOnClickListener(v -> eventBus.post(ExampleEvent.ONE));

        eventTwoButton = (Button) findViewById(R.id.eventTwo_button);
        eventTwoButton.setOnClickListener(v -> eventBus.post(ExampleEvent.TWO));

        eventMessageButton = (Button) findViewById(R.id.eventMessage_button);
        eventMessageButton.setOnClickListener(v -> eventBus.post("Message"));

        receivedLabel = (TextView) findViewById(R.id.main_receivedLabel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        eventOneButton.setOnClickListener(null);
        eventTwoButton.setOnClickListener(null);
        eventMessageButton.setOnClickListener(null);

        eventBus.unregister(this);
        eventBus = null;
    }

    @Subscribe
    public void onEvent(FragmentEvent event) {
        receivedLabel.setText(getString(R.string.received_from));
        receivedLabel.append(event.name());
    }

}
