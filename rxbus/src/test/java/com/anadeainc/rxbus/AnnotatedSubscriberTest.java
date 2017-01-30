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

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class AnnotatedSubscriberTest {

    @Mock
    Object object;

    private Method method;
    private AnnotatedSubscriber<Object> subscriber;

    private boolean methodCalled;
    private Object receivedEvent;

    @Before
    public void setUp() throws NoSuchMethodException {
        methodCalled = false;
        receivedEvent = null;

        method = getValidMethod();
        subscriber = new AnnotatedSubscriber<>(this, method);
    }

    @Test
    public void testInitSubscriber() throws Exception {
        int expectedHashCode = 31 * hashCode() + method.hashCode();
        assertEquals(expectedHashCode, subscriber.hashCode());
    }

    @Test
    public void testAcceptEvent() throws Exception {
        subscriber.acceptEvent(object);

        assertTrue(methodCalled);
        assertNotNull(receivedEvent);
    }

    @Test
    public void testRelease() throws Exception {
        subscriber.release();

        try {
            subscriber.acceptEvent(object);
            fail("Expected an NullPointerException to be thrown");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testIdentity() throws NoSuchMethodException {
        AnnotatedSubscriber<Object> other = subscriber;

        assertTrue(subscriber.equals(other));
        assertTrue(other.equals(subscriber));
        assertEquals(subscriber.hashCode(), other.hashCode());

        //noinspection ObjectEqualsNull
        assertFalse(subscriber.equals(null));

        assertFalse(subscriber.equals(object));

        other = new AnnotatedSubscriber<>(object, method);
        assertFalse(subscriber.equals(other));
        assertFalse(other.equals(subscriber));
        assertNotEquals(subscriber.hashCode(), other.hashCode());

        Method otherMethod = getErrorMethod();
        other = new AnnotatedSubscriber<>(object, otherMethod);
        assertFalse(subscriber.equals(other));
        assertFalse(other.equals(subscriber));
        assertNotEquals(subscriber.hashCode(), other.hashCode());

        other = new AnnotatedSubscriber<>(this, otherMethod);
        assertFalse(subscriber.equals(other));
        assertFalse(other.equals(subscriber));
        assertNotEquals(subscriber.hashCode(), other.hashCode());

        other = new AnnotatedSubscriber<>(this, method);
        assertTrue(subscriber.equals(other));
        assertTrue(other.equals(subscriber));
        assertEquals(subscriber.hashCode(), other.hashCode());
    }

    private Method getValidMethod() throws NoSuchMethodException {
        return getClass().getMethod("validMethod", Object.class);
    }

    private Method getErrorMethod() throws NoSuchMethodException {
        return getClass().getMethod("errorMethod", Object.class);
    }

    @SuppressWarnings("unused")
    public void validMethod(Object event) {
        methodCalled = true;
        receivedEvent = event;
    }

    @SuppressWarnings("unused")
    public void errorMethod(Object event) throws Exception {
        throw new RuntimeException();
    }

}