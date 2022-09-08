package com.arturkarwowski.testbench.graphql.asyncservlet.client.apachehcclient4;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CompletableFuturisationUtils {

    public static  CompletableFuture<HttpResponse> toCompletableFuture(Consumer<FutureCallback<HttpResponse>> c) {

        var cf = new CompletableFuture<HttpResponse>();

        c.accept(new FutureCallback<>() {
            @Override
            public void completed(HttpResponse result) {
                cf.complete(result);
            }

            @Override
            public void failed(Exception ex) {
                cf.completeExceptionally(ex);
            }

            @Override
            public void cancelled() {
                cf.cancel(true);
            }
        });
        return cf;
    }

}
