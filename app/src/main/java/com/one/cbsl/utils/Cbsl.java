package com.one.cbsl.utils;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;

import java.io.IOException;
import java.net.SocketException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

public class Cbsl extends Application {
    private static Cbsl instance;
    String TAG = this.getClass().getName();


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.HEADERS);


        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if (e instanceof IOException || e instanceof SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }
            if (e instanceof NullPointerException || e instanceof IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            // log undeliverable exceptions
            e.printStackTrace();
        });
    }

    public static Cbsl getInstance() {
        return instance;
    }


}
