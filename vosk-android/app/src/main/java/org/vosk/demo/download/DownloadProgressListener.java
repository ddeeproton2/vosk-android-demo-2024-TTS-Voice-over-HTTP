package org.vosk.demo.download;

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
