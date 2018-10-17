package com.zxl.casual.living.utils;


import com.zxl.casual.living.BuildConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;

import java.util.concurrent.Executors;
import java.util.logging.Level;

public class EventBusUtils {

    private EventBusUtils() {

    }

    public static void init() {
        if (EventBus.getDefault() == null) {
            EventBus.builder()
                    .executorService(Executors.newFixedThreadPool(3))
                    .logNoSubscriberMessages(BuildConfig.DEBUG)
                    .logSubscriberExceptions(BuildConfig.DEBUG)
                    .logger(BuildConfig.DEBUG ? new Logger.AndroidLogger("eventbus") : new EmptyEventBusLogger())
                    .throwSubscriberException(BuildConfig.DEBUG)
                    .installDefaultEventBus();
        }
    }


    public static void post(final Object event) {
        EventBus.getDefault().post(event);
    }

    public static void register(Object subscribe) {
        EventBus.getDefault().register(subscribe);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    static class EmptyEventBusLogger implements Logger {

        @Override
        public void log(Level level, String msg) {

        }

        @Override
        public void log(Level level, String msg, Throwable th) {

        }
    }
}
