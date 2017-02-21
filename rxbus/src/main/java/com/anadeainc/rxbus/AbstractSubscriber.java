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

import rx.Subscription;
import rx.functions.Action1;

abstract class AbstractSubscriber<T> implements Action1<T>, Subscription {

    private volatile boolean unsubscribed;

    @Override
    public void call(T event) {
        try {
            acceptEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not dispatch event: " + event.getClass());
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return unsubscribed;
    }

    @Override
    public void unsubscribe() {
        if (unsubscribed)
            return;
        unsubscribed = true;
        release();
    }

    protected abstract void acceptEvent(T event) throws Exception;

    protected abstract void release();

}
