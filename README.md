RxBus
===========

Event bus based on RxJava and optimized for Android.

Usage
-------

##### Subscribing

To subscribe to an event, declare and annotate a method with @Subscribe. The method should be public and take only a single parameter.

```
@Subscribe
public void onEvent(SomeEvent event) {
    // TODO: Do something
}
```

You can also create subscription like following:

```
CustomSubscriber<SomeEvent> customSubscriber = eventBus.obtainSubscriber(SomeEvent.class,
    new Consumer<SomeEvent>() {
        @Override
        public void accept(SomeEvent someEvent) throws Exception {
            // TODO: Do something
        }
    })
    .withFilter(new Predicate<SomeEvent>() {
        @Override
        public boolean test(SomeEvent someEvent) throws Exception {
            return "Specific message".equals(someEvent.message);
        }
    })
    .withScheduler(Schedulers.trampoline());
```

##### Register and unregister your observer

To receive events, a class instance needs to register with the bus.

```
RxBus.getInstance().register(this);
```

The customSubscriber also needs to register with the bus.

```
RxBus.getInstance().registerSubscriber(this, customSubscriber);
```

Remember to also call the unregister method when appropriate.
```
RxBus.getInstance().unregister(this);
```

##### Publishing

To publish a new event, call the post method:

```
RxBus.getInstance().post(new SomeEvent("Message"));
```


ProGuard
-------

If you are using ProGuard, add the following lines to your ProGuard configuration file.

```
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.anadeainc.rxbus.Subscribe public *;
}
```

License
-------

    Copyright (C) 2017 Anadea Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
