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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CustomSubscriptionTest {

    @Rule
    public RxSchedulersRule rxSchedulersRule = new RxSchedulersRule();

    @Mock
    Action1<Object> consumer;

    @Mock
    Action1<Bus> busConsumer;

    @Mock
    Action1<TestEvent> testEventConsumer;

    @Mock
    Object event;

    @Mock
    Object observer;

    private Bus bus = new RxBus();

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
        subscriber.call(event);
        verify(consumer).call(event);
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
        verify(consumer).call(event);

        bus.unregister(observer);
    }

    @Test
    public void registerCustomSubscriberWithScheduler() throws Exception {
        CustomSubscriber<Object> subscriber = bus
                .obtainSubscriber(Object.class, consumer)
                .withScheduler(Schedulers.io());
        bus.registerSubscriber(observer, subscriber);
        bus.post(event);
        verify(consumer).call(event);

        bus.unregister(observer);
    }

    @Test
    public void registerCustomSubscriberWithFilter() throws Exception {
        CustomSubscriber<TestEvent> subscriber = bus
                .obtainSubscriber(TestEvent.class, testEventConsumer)
                .withFilter(new Func1<TestEvent, Boolean>() {
                    @Override
                    public Boolean call(TestEvent testEvent) {
                        return testEvent == TestEvent.TWO;
                    }
                });
        bus.registerSubscriber(observer, subscriber);
        bus.post(TestEvent.ONE);
        verify(testEventConsumer, never()).call(TestEvent.ONE);
        bus.post(TestEvent.TWO);
        verify(testEventConsumer).call(TestEvent.TWO);

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
        verify(consumer).call(event);
        bus.unregister(observer);
        bus.post(event);
        verify(consumer, times(1)).call(event);
    }

    private enum TestEvent {
        ONE, TWO
    }

}