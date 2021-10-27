package com.qiniu.client.curl;

import static org.junit.Assert.assertEquals;

import android.util.Log;

import org.junit.Test;

public class CurlTest extends BaseTest {

    @Test
    public void request() {
        CurlConfiguration.Builder builder = new CurlConfiguration.Builder();
//        builder.dnsResolverArray = new String[]{"www.baidu.com:442:61.135.169.121"};

        CurlConfiguration curlConfiguration = builder.build();

        Curl curl = new Curl();
        long code = curl.globalInit();

        final boolean[] isCompleted = {false};
        final CurlResponse[] res = {null};

        CurlRequest request = null; // new CurlRequest();
        curl.request(request, curlConfiguration, new Curl.Handler() {

            @Override
            public void receiveResponse(CurlResponse response) {
                res[0] = response;
                Log.i("Curl", "====== Response: url:" + response.getUrl() + " statusCode:" + response.getStatusCode() + " headerInfo:" + response.getAllHeaderFields());
            }

            @Override
            public byte[] sendData(long dataLength) {
                Log.i("Curl", "====== sendData:");
                return new byte[0];
            }

            @Override
            public void receiveData(byte[] data) {
                String info = new String(data);
                Log.i("Curl", "====== receiveData:" + info);
            }

            @Override
            public void completeWithError(int errorCode, String errorInfo) {
                isCompleted[0] = true;
                Log.i("Curl", "====== completeWithError errorCode:" + errorCode + " errorInfo:" + errorInfo);
            }

            @Override
            public void sendProgress(long bytesSent, long totalBytesSent, long totalBytesExpectedToSend) {
                Log.i("Curl", "====== sendProgress bytesSent:" + bytesSent + " totalBytesSent:" + totalBytesSent + " totalBytesExpectedToSend:" + totalBytesExpectedToSend);
            }

            @Override
            public void receiveProgress(long bytesReceive, long totalBytesReceive, long totalBytesExpectedToReceive) {
                Log.i("Curl", "====== receiveProgress bytesReceive:" + bytesReceive + " totalBytesReceive:" + totalBytesReceive + " totalBytesExpectedToReceive:" + totalBytesExpectedToReceive);
            }

            @Override
            public void didFinishCollectingMetrics(CurlTransactionMetrics metrics) {
                Log.i("Curl", "====== didFinishCollectingMetrics metrics:" + metrics);
            }
        });


        wait(new WaitConditional() {
            @Override
            public boolean shouldWait() {
                return !isCompleted[0];
            }
        }, 60);

        assertEquals("response:" + res[0], 0, res[0].getStatusCode());
    }
}

