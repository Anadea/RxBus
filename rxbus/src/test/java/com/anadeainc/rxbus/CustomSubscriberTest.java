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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CustomSubscriberTest {

    @Mock
    Object object;

    @Mock
    Consumer<Object> receiver;

    @Mock
    Consumer<Object> otherReceiver;

    @Mock
    Consumer<String> anotherReceiver;

    @Mock
    Predicate<Object> filter;

    @Mock
    Scheduler scheduler;

    private CustomSubscriber<Object> subscriber;

    @Before
    public void setUp() {
        subscriber = new CustomSubscriber<>(Object.class, receiver);
    }

    @Test
    public void testInitSubscriber() {
        int expectedHashCode = receiver.hashCode();
        assertEquals(expectedHashCode, subscriber.hashCode());
        assertNotNull(subscriber.getEventClass());
        assertEquals(Object.class, subscriber.getEventClass());
    }

    @Test
    public void testFilter() {
        assertNull(subscriber.getFilter());

        try {
            //noinspection ConstantConditions
            subscriber.withFilter(null);
            fail("Expected an NullPointerException to be thrown");
        } catch (Exception e) {
            assertTrue(true);
        }

        subscriber.withFilter(filter);

        assertNotNull(subscriber.getFilter());
        assertEquals(filter, subscriber.getFilter());
    }

    @Test
    public void testScheduler() {
        assertNull(subscriber.getScheduler());

        try {
            //noinspection ConstantConditions
            subscriber.withScheduler(null);
            fail("Expected an NullPointerException to be thrown");
        } catch (Exception e) {
            assertTrue(true);
        }

        subscriber.withScheduler(scheduler);

        assertNotNull(subscriber.getScheduler());
        assertEquals(scheduler, subscriber.getScheduler());
    }

    @Test
    public void testAcceptEvent() throws Exception {
        subscriber.acceptEvent(object);
        verify(receiver).accept(object);
    }

    @Test
    public void testRelease() throws Exception {
        subscriber.withFilter(filter);
        subscriber.withScheduler(scheduler);
        subscriber.release();

        assertNull(subscriber.getEventClass());
        assertNull(subscriber.getFilter());
        assertNull(subscriber.getScheduler());

        try {
            subscriber.acceptEvent(object);
            fail("Expected an NullPointerException to be thrown");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testIdentity() {
        CustomSubscriber<Object> other = subscriber;

        assertTrue(subscriber.equals(other));
        assertTrue(other.equals(subscriber));
        assertEquals(subscriber.hashCode(), other.hashCode());

        //noinspection ObjectEqualsNull
        assertFalse(subscriber.equals(null));

        assertFalse(subscriber.equals(object));

        other = new CustomSubscriber<>(Object.class, otherReceiver);
        assertFalse(subscriber.equals(other));
        assertFalse(other.equals(subscriber));
        assertNotEquals(subscriber.hashCode(), other.hashCode());

        CustomSubscriber<String> another = new CustomSubscriber<>(String.class, anotherReceiver);
        assertFalse(subscriber.equals(another));
        assertFalse(another.equals(subscriber));
        assertNotEquals(subscriber.hashCode(), another.hashCode());
    }

}