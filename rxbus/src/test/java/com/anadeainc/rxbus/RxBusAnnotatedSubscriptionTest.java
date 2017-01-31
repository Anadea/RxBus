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
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class RxBusAnnotatedSubscriptionTest {


    @Mock
    Object event;

    private Bus bus = new RxBus();

    private Object receivedEvent;

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
    public void registerRejectNullObserver() {
        try {
            //noinspection ConstantConditions
            bus.register(null);
            fail("Must reject null observer");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void registerRejectMethods() {

        try {
            bus.register(new Object() {
                @Subscribe
                void onEvent(Object event) {

                }
            });
            fail("Must reject non-public method");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            bus.register(new Object() {
                @Subscribe
                protected void onEvent(Object event) {

                }
            });
            fail("Must reject non-public method");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            bus.register(new ObserverWithStaticMethod());
            fail("Must reject static method");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

    }

    @Test
    public void registerRejectInvalidArgs() {

        try {
            bus.register(new Object() {
                @Subscribe
                public void onEvent() {

                }
            });
            fail("Must reject without a single argument");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            bus.register(new Object() {
                @Subscribe
                public void onEvent(Object event, String string) {

                }
            });
            fail("Must reject without a single argument");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

        try {
            bus.register(new Object() {
                @Subscribe
                public void onEvent(TestEvent event) {

                }
            });
            fail("Must reject when event is interface");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

    }

    @Test
    public void registerRejectRegisterTwice() {
        DummyObserver dummyObserver = new DummyObserver();

        bus.register(dummyObserver);

        try {
            bus.register(dummyObserver);
            fail("Must reject, observer already been registered");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }

    }

    @Test
    public void registerRejectEventTypeTwice() {

        try {
            bus.register(new Object() {
                @Subscribe
                public void onEventOne(Object event) {

                }

                @Subscribe
                public void onEventTwo(Object event) {

                }
            });

            fail("Must reject, Object subscriber has already been registered");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void registerSkipBridgeMethod() {
        ObserverWithBridgeSyntheticMethod observer = new ObserverWithBridgeSyntheticMethod();
        assertEquals(0, observer.getReceivedCount());
        bus.register(observer);
        bus.post("");
        assertEquals(1, observer.getReceivedCount());
    }

    @Test
    public void registerSuccessful() {
        assertNull(receivedEvent);
        bus.register(this);
        bus.post(event);
        assertNotNull(receivedEvent);
        assertEquals(receivedEvent, event);
        bus.unregister(this);
    }

    @Subscribe
    public void onEvent(Object event) {
        receivedEvent = event;
    }

    interface TestEvent {

    }

    interface TestObserver<T> {
        @Subscribe
        void onEvent(T event);
    }

    static class ObserverWithStaticMethod {
        @Subscribe
        static void onEvent(Object event) {
        }
    }

    static class ObserverWithBridgeSyntheticMethod implements TestObserver<String> {

        private int receivedCount = 0;

        @Subscribe
        public void onEvent(String event) {
            receivedCount++;
        }

        int getReceivedCount() {
            return receivedCount;
        }
    }

    static class DummyObserver {
        @SuppressWarnings("unused")
        public void dummy() {
        }
    }
}