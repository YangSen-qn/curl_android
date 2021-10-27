package com.qiniu.client.curl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CurlThreadPool {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    static void run(Runnable r){
        executorService.submit(r);
    }
}
