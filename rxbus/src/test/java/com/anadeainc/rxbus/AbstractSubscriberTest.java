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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSubscriberTest {

    @Mock
    Object event;

    @Spy
    AbstractSubscriber<Object> subscriber;

    @Test
    public void testAcceptEvent() throws Exception {
        subscriber.accept(event);
        verify(subscriber).acceptEvent(event);
    }

    @Test
    public void testAcceptEventWithException() throws Exception {
        doThrow(new RuntimeException()).when(subscriber).acceptEvent(event);
        try {
            subscriber.accept(event);
            fail("Expected an RuntimeException to be thrown");
        } catch (Exception e) {
            assertEquals("Could not dispatch event: " + event.getClass(), e.getMessage());
        }
    }

    @Test
    public void testDispose() {
        assertFalse(subscriber.isDisposed());
        subscriber.dispose();

        assertTrue(subscriber.isDisposed());
        verify(subscriber).release();

        subscriber.dispose();
        verify(subscriber, times(1)).release();
    }

}