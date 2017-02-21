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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func1;

public class CustomSubscriber<T> extends AbstractSubscriber<T> {

    private final int hashCode;

    private Class<T> eventClass;
    private Action1<T> receiver;
    private Func1<T, Boolean> filter;
    private Scheduler scheduler;

    CustomSubscriber(@NonNull Class<T> eventClass, @NonNull Action1<T> receiver) {
        this.eventClass = eventClass;
        this.receiver = receiver;

        hashCode = receiver.hashCode();
    }

    @SuppressWarnings("WeakerAccess")
    public CustomSubscriber<T> withFilter(@NonNull Func1<T, Boolean> filter) {
        Utils.checkNonNull(filter, "Filter must not be null.");
        this.filter = filter;
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public CustomSubscriber<T> withScheduler(@NonNull Scheduler scheduler) {
        Utils.checkNonNull(scheduler, "Scheduler must not be null.");
        this.scheduler = scheduler;
        return this;
    }

    @NonNull
    Class<T> getEventClass() {
        return eventClass;
    }

    @Nullable
    Func1<T, Boolean> getFilter() {
        return filter;
    }

    @Nullable
    Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    protected void acceptEvent(T event) throws Exception {
        receiver.call(event);
    }

    @Override
    protected void release() {
        eventClass = null;
        receiver = null;
        filter = null;
        scheduler = null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        CustomSubscriber<?> that = (CustomSubscriber<?>) other;

        return receiver.equals(that.receiver);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
