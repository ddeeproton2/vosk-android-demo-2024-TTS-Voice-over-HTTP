package org.vosk.demo.network;

import org.vosk.demo.VoskActivity;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WebServerHTTP  extends NanoHTTPD {
    VoskActivity base;
    public WebServerHTTP(String hostname, int port, VoskActivity _base) throws IOException {
        super(hostname, port);
        this.base = _base;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning server Address bind: "+hostname+":"+port+" \n");
    }

    @Override
    public Response serve(IHTTPSession session) {
        return this.base.serve(session);


    }

    public void startServer() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        stop();
    }
}