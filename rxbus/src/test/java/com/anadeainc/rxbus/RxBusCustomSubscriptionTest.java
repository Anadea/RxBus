/*
 * Copyright (C) 2017 Anadea Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anadeainc.rxbus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RxBusCustomSubscriptionTest {

    @Mock
    Consumer<Object> consumer;

    @Mock
    Consumer<Bus> busConsumer;

    @Mock
    Consumer<TestEvent> testEventConsumer;

    @Mock
    Object event;

    @Mock
    Object observer;

    private Bus bus = new RxBus();

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler current) throws Exception {
                return Schedulers.trampoline();
            }
        });
        RxAndroidPlugins.setMainThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler current) throws Exception {
                return Schedulers.trampoline();
            }
        });
    }

    @Test
    public void obtainSubscriberRejections() {
        try {
            //noinspection ConstantConditions
            bus.obtainSubscriber(null, consumer);
            fail("Must reject null event class");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }

        try {
            bus.obtainSubscriber(Bus.class, busConsumer);
            fail("Must reject if event class is interface");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            //noinspection ConstantConditions
            bus.obtainSubscriber(Object.class, null);
            fail("Must reject null consumer");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void obtainCustomSubscriber() throws Exception {
        CustomSubscriber<Object> subscriber = bus.obtainSubscriber(Object.class, consumer);
        assertNotNull(subscriber);
        assertEquals(Object.class, subscriber.getEventClass());
        subscriber.accept(event);
        verify(consumer).accept(event);
    }

    @Test
    public void registerCustomRejections() {
        CustomSubscriber<Object> subscriber = bus.obtainSubscriber(Object.class, consumer);

        try {
            //noinspection ConstantConditions
            bus.registerSubscriber(null, subscriber);
            fail("Must reject null observer");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }

        try {
            //noinspection ConstantConditions
            bus.registerSubscriber(observer, null);
            fail("Must reject null subscriber");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }

        bus.registerSubscriber(observer, subscriber);

        try {
            //noinspection ConstantConditions
            bus.registerSubscriber(observer, subscriber);
            fail("Must reject - subscriber has already been registered");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
            bus.unregister(observer);
        }
    }

    @Test
    public void postRejectionOfNullEvent() {
        try {
            //noinspection ConstantConditions
            bus.post(null);
            fail("Must reject null event");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void registerCustomSubscriberAndPostEvent() throws Exception {
        CustomSubscriber<Object> subscriber = bus.obtainSubscriber(Object.class, consumer);
        bus.registerSubscriber(observer, subscriber);
        bus.post(event);
        verify(consumer).accept(event);

        bus.unregister(observer);
    }

    @Test
    public void registerCustomSubscriberWithScheduler() throws Exception {
        CustomSubscriber<Object> subscriber = bus
                .obtainSubscriber(Object.class, consumer)
                .withScheduler(Schedulers.io());
        bus.registerSubscriber(observer, subscriber);
        bus.post(event);
        verify(consumer).accept(event);

        bus.unregister(observer);
    }

    @Test
    public void registerCustomSubscriberWithFilter() throws Exception {
        CustomSubscriber<TestEvent> subscriber = bus
                .obtainSubscriber(TestEvent.class, testEventConsumer)
                .withFilter(new Predicate<TestEvent>() {
                    @Override
                    public boolean test(TestEvent testEvent) throws Exception {
                        return testEvent == TestEvent.TWO;
                    }
                });
        bus.registerSubscriber(observer, subscriber);
        bus.post(TestEvent.ONE);
        verify(testEventConsumer, never()).accept(TestEvent.ONE);
        bus.post(TestEvent.TWO);
        verify(testEventConsumer).accept(TestEvent.TWO);

        bus.unregister(observer);
    }

    @Test
    public void unregisterRejections() {
        try {
            //noinspection ConstantConditions
            bus.unregister(null);
            fail("Must reject null observer");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }

        try {
            bus.unregister(observer);
            fail("Must reject without observer registration");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void unregister() throws Exception {
        CustomSubscriber<Object> subscriber = bus.obtainSubscriber(Object.class, consumer);
        bus.registerSubscriber(observer, subscriber);
        bus.post(event);
        verify(consumer).accept(event);
        bus.unregister(observer);
        bus.post(event);
        verify(consumer, times(1)).accept(event);
    }

    private enum TestEvent {
        ONE, TWO
    }

}