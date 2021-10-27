package com.qiniu.client.curl;

class CurlTransactionMetrics {

    private long countOfRequestHeaderBytesSent;
    private long countOfRequestBodyBytesSent;

    private long countOfResponseHeaderBytesReceived;
    private long countOfResponseBodyBytesReceived;

    private String localAddress;
    private long localPort;

    private String remoteAddress;
    private long remotePort;

    private long startTimestamp;
    private long nameLookupTime;
    private long connectTime;
    private long appConnectTime;
    private long preTransferTime;
    private long startTransferTime;
    private long totalTime;
    private long redirectTime;

    long getCountOfRequestHeaderBytesSent() {
        return countOfRequestHeaderBytesSent;
    }

    void setCountOfRequestHeaderBytesSent(long countOfRequestHeaderBytesSent) {
        this.countOfRequestHeaderBytesSent = countOfRequestHeaderBytesSent;
    }

    long getCountOfRequestBodyBytesSent() {
        return countOfRequestBodyBytesSent;
    }

    void setCountOfRequestBodyBytesSent(long countOfRequestBodyBytesSent) {
        this.countOfRequestBodyBytesSent = countOfRequestBodyBytesSent;
    }

    long getCountOfResponseHeaderBytesReceived() {
        return countOfResponseHeaderBytesReceived;
    }

    void setCountOfResponseHeaderBytesReceived(long countOfResponseHeaderBytesReceived) {
        this.countOfResponseHeaderBytesReceived = countOfResponseHeaderBytesReceived;
    }

    long getCountOfResponseBodyBytesReceived() {
        return countOfResponseBodyBytesReceived;
    }

    void setCountOfResponseBodyBytesReceived(long countOfResponseBodyBytesReceived) {
        this.countOfResponseBodyBytesReceived = countOfResponseBodyBytesReceived;
    }

    String getLocalAddress() {
        return localAddress;
    }

    void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    long getLocalPort() {
        return localPort;
    }

    void setLocalPort(long localPort) {
        this.localPort = localPort;
    }

    String getRemoteAddress() {
        return remoteAddress;
    }

    void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    long getRemotePort() {
        return remotePort;
    }

    void setRemotePort(long remotePort) {
        this.remotePort = remotePort;
    }

    long getStartTimestamp() {
        return startTimestamp;
    }

    void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    long getNameLookupTime() {
        return nameLookupTime;
    }

    void setNameLookupTime(long nameLookupTime) {
        this.nameLookupTime = nameLookupTime;
    }

    long getConnectTime() {
        return connectTime;
    }

    void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public long getAppConnectTime() {
        return appConnectTime;
    }

    public void setAppConnectTime(long appConnectTime) {
        this.appConnectTime = appConnectTime;
    }

    long getPreTransferTime() {
        return preTransferTime;
    }

    void setPreTransferTime(long preTransferTime) {
        this.preTransferTime = preTransferTime;
    }

    long getStartTransferTime() {
        return startTransferTime;
    }

    void setStartTransferTime(long startTransferTime) {
        this.startTransferTime = startTransferTime;
    }

    long getTotalTime() {
        return totalTime;
    }

    void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    long getRedirectTime() {
        return redirectTime;
    }

    void setRedirectTime(long redirectTime) {
        this.redirectTime = redirectTime;
    }
}
