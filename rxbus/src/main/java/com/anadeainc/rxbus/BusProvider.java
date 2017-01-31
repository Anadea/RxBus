package com.anadeainc.rxbus;

@SuppressWarnings("unused")
public final class BusProvider {

    private BusProvider() {
    }

    public static Bus getInstance() {
        return BusHolder.INSTANCE;
    }

    private static final class BusHolder {
        final static Bus INSTANCE = new RxBus();
    }
}
