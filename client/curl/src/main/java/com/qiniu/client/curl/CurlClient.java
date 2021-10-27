package com.qiniu.client.curl;

import android.util.Log;

import com.qiniu.android.http.ProxyConfiguration;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.http.metrics.UploadSingleRequestMetrics;
import com.qiniu.android.http.request.IRequestClient;
import com.qiniu.android.http.request.IUploadServer;
import com.qiniu.android.http.request.Request;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class CurlClient implements IRequestClient {

    private Curl curl = null;
    private Request request = null;
    private CurlResponse curlResponse = null;
    private UploadSingleRequestMetrics metrics = new UploadSingleRequestMetrics();
    private ByteArrayInputStream requestDataStream = null;
    private ByteArrayOutputStream responseDataStream = null;

    @Override
    public void request(final Request request,
                        IUploadServer server,
                        boolean isAsync,
                        ProxyConfiguration connectionProxy,
                        final RequestClientProgress progress,
                        final RequestClientCompleteHandler complete) {
        this.request = request;

        String host = server != null ? server.getHost() : null;
        String ip = server != null ? server.getIp() : null;
        if (host != null && host.equals("upload-z0.qbox.me")) {
            ip = "111.1.36.180";
        }

        CurlConfiguration.Builder builder = new CurlConfiguration.Builder();
        if (host != null && ip != null) {
            int port = request.urlString.contains("https://") ? 443 : 80;
            CurlConfiguration.DnsResolver resolver = new CurlConfiguration.DnsResolver(host, ip, port);
            builder.setDnsResolverArray(new CurlConfiguration.DnsResolver[]{resolver});
        }
        if (connectionProxy != null) {
            builder.setProxy("");
            builder.setProxyUserPwd("");
        }
        final CurlConfiguration curlConfiguration = builder.build();

        int httpMethod = CurlRequest.HttpMethodGet;
        if (request.httpMethod.equals(Request.HttpMethodHEAD)) {
            httpMethod = CurlRequest.HttpMethodHead;
        } else if (request.httpMethod.equals(Request.HttpMethodGet)) {
            httpMethod = CurlRequest.HttpMethodGet;
        } else if (request.httpMethod.equals(Request.HttpMethodPOST)) {
            httpMethod = CurlRequest.HttpMethodPost;
        } else if (request.httpMethod.equals(Request.HttpMethodPUT)) {
            httpMethod = CurlRequest.HttpMethodPut;
        }

        int httpVersion = CurlRequest.HttpVersionCurlMatch;
//        if (curlConfiguration.getDnsResolverArray() != null) {
//            httpVersion = CurlRequest.HttpVersion3;
//        }

        if (request.httpBody != null) {
            requestDataStream = new ByteArrayInputStream(request.httpBody);
        }
        final CurlRequest curlRequest = new CurlRequest(request.urlString, httpVersion, httpMethod,
                request.allHeaders, request.httpBody, request.timeout);
        if (isAsync) {
            CurlThreadPool.run(new Runnable() {
                @Override
                public void run() {
                    request(curlRequest, curlConfiguration, progress, complete);
                }
            });
        } else {
            request(curlRequest, curlConfiguration, progress, complete);
        }
    }

    private void request(CurlRequest curlRequest,
                         CurlConfiguration configuration,
                         final RequestClientProgress progress,
                         final RequestClientCompleteHandler complete) {

        metrics.start();
        responseDataStream = new ByteArrayOutputStream();
        curl = new Curl();
        curl.request(curlRequest, configuration, new Curl.Handler() {

            @Override
            public void receiveResponse(CurlResponse response) {
                curlResponse = response;
                if (response != null) {
                    metrics.httpVersion = response.httpVersion;
                }
                Log.i("Curl", "====== Response: url:" + response.getUrl() + " statusCode:" + response.getStatusCode() + " headerInfo:" + response.getAllHeaderFields());
            }

            @Override
            public byte[] sendData(long dataLength) {
                // 设置了 body 不需要在设置 此回调
                Log.i("Curl", "====== sendData:");
                byte[] data = new byte[(int)dataLength];
                try {
                    int length = requestDataStream.read(data);
                    data = Arrays.copyOf(data, length);
                } catch (Exception e) {
                    data = new byte[0];
                }
                return data;
            }

            @Override
            public void receiveData(byte[] data) {
                try {
                    responseDataStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String info = new String(data);
                Log.i("Curl", "====== receiveData:" + info);
            }

            @Override
            public void completeWithError(int errorCode, String errorInfo) {
                metrics.end();

                int statusCode = errorCode;
                Map<String, String> header = null;
                JSONObject response = null;
                if (curlResponse != null) {
                    if (statusCode == 0) {
                        statusCode = curlResponse.statusCode;
                    }
                    header = curlResponse.allHeaderFields;
                }

                byte[] responseData = responseDataStream.toByteArray();
                if (responseData.length > 0) {
                    try {
                        String responseString = new String(responseData, "utf-8");
                        response = new JSONObject(responseString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                releaseResource();

                ResponseInfo responseInfo = ResponseInfo.create(request, statusCode, header, response, errorInfo);
                if (complete != null) {
                    complete.complete(responseInfo, metrics, response);
                }
                Log.i("Curl", "====== completeWithError errorCode:" + errorCode + " errorInfo:" + errorInfo);
            }

            @Override
            public void sendProgress(long bytesSent, long totalBytesSent, long totalBytesExpectedToSend) {
                if (progress != null) {
                    progress.progress(totalBytesSent, totalBytesExpectedToSend);
                }
                Log.i("Curl", "====== sendProgress bytesSent:" + bytesSent + " totalBytesSent:" + totalBytesSent + " totalBytesExpectedToSend:" + totalBytesExpectedToSend);
            }

            @Override
            public void receiveProgress(long bytesReceive, long totalBytesReceive, long totalBytesExpectedToReceive) {
                Log.i("Curl", "====== receiveProgress bytesReceive:" + bytesReceive + " totalBytesReceive:" + totalBytesReceive + " totalBytesExpectedToReceive:" + totalBytesExpectedToReceive);
            }

            @Override
            public void didFinishCollectingMetrics(CurlTransactionMetrics transactionMetrics) {
                metrics.clientName = "Curl";
                metrics.clientVersion = "";
                metrics.countOfRequestHeaderBytesSent = transactionMetrics.getCountOfRequestHeaderBytesSent();
                metrics.countOfRequestBodyBytesSent = transactionMetrics.getCountOfRequestBodyBytesSent();
                metrics.countOfResponseHeaderBytesReceived = transactionMetrics.getCountOfResponseHeaderBytesReceived();
                metrics.countOfResponseBodyBytesReceived = transactionMetrics.getCountOfResponseBodyBytesReceived();
                metrics.remoteAddress = transactionMetrics.getRemoteAddress();
                metrics.remotePort = (int)transactionMetrics.getRemotePort();
                metrics.localAddress = transactionMetrics.getLocalAddress();
                metrics.localPort = (int)transactionMetrics.getLocalPort();
                Log.i("Curl", "====== didFinishCollectingMetrics metrics:" + metrics);
            }
        });
    }

    @Override
    public void cancel() {
        if (curl != null) {
            curl.cancel();
        }
    }

    private void releaseResource() {
        if (requestDataStream != null) {
            try {
                requestDataStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (responseDataStream != null) {
            try {
                responseDataStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
