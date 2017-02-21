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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

class RxSchedulersRule implements TestRule {
    private final Func1<Scheduler, Scheduler> javaIOScheduler = new Func1<Scheduler, Scheduler>() {
        @Override
        public Scheduler call(Scheduler scheduler) {
            return Schedulers.immediate();
        }
    };

    private final RxAndroidSchedulersHook androidSchedulersHook = new RxAndroidSchedulersHook() {
        @Override
        public Scheduler getMainThreadScheduler() {
            return Schedulers.immediate();
        }
    };

    @Override
    public Statement apply(final Statement base, Description description) {

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                RxJavaHooks.reset();
                RxAndroidPlugins.getInstance().reset();

                RxJavaHooks.setOnIOScheduler(javaIOScheduler);
                RxAndroidPlugins.getInstance().registerSchedulersHook(androidSchedulersHook);

                base.evaluate();

                RxJavaHooks.reset();
                RxAndroidPlugins.getInstance().reset();
            }
        };

    }
}
