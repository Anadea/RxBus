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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

    @Mock
    Object object;

    @Test
    public void privateConstructor() throws Exception {
        final Constructor<?>[] constructors = Utils.class.getDeclaredConstructors();

        for (final Constructor<?> constructor : constructors) {
            Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }

        constructors[0].setAccessible(true);
        constructors[0].newInstance((Object[]) null);
    }

    @Test
    public void testCheckNonNull() {
        assertEquals(object, Utils.checkNonNull(object, ""));
    }

    @Test
    public void testCheckNonNullException() {
        try {
            Utils.checkNonNull(null, "excepted message");
            fail("Expected an NullPointerException to be thrown");
        } catch (Exception e) {
            assertEquals("excepted message", e.getMessage());
        }
    }


}