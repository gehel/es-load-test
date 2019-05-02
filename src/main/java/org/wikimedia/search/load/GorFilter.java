package org.wikimedia.search.load;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.wikimedia.search.load.GorRequest.Method.GET;
import static org.wikimedia.search.load.GorRequest.Method.HEAD;
import static org.wikimedia.search.load.GorRequest.Method.POST;
import static org.wikimedia.search.load.GorRequest.Method.PUT;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import de.thetaphi.forbiddenapis.SuppressForbidden;

public class GorFilter {

    private final GorReader in;
    private final GorWriter out;

    @SuppressForbidden // we're streaming from stdin to stdout
    public static void main(String... args) throws IOException {
        try (GorWriter out = new GorWriter(new OutputStreamWriter(new BufferedOutputStream(System.out), UTF_8))) {
            GorReader in = new GorReader(new BufferedInputStream(System.in));
            GorFilter gorFilter = new GorFilter(in, out);
            gorFilter.run();
        }
    }

    public GorFilter(GorReader in, GorWriter out) {
        this.in = in;
        this.out = out;
    }

    private void run() throws IOException {
        while (in.hasNext()) {
            GorRequest request = in.next();
            if (filter(request)) out.write(cleanup(request));
        }
    }

    private boolean filter(GorRequest request) {
        return methodFilter(request)
                && !isBulk(request)
                && !isLvsMonitoring(request)
                && !isStatsMonitoring(request);
    }

    private GorRequest cleanup(GorRequest request) {
        return new GorRequest(request.meta, request.requestLine, cleanup(request.headers), request.payload);
    }

    private Map<String, String> cleanup(Map<String, String> headers) {
        Map<String, String> result = new HashMap<>();
        headers.forEach((key, value) -> {
            if (key.equals("Host")) result.put(key, "search.svc.codfw.wmnet");
            else result.put(key, value);
        });
        return result;
    }

    private boolean methodFilter(GorRequest request) {
        GorRequest.Method method = request.requestLine.method;
        return method == GET
                || method == POST
                || method == HEAD;
    }

    private boolean isBulk(GorRequest request) {
        return request.requestLine.method == PUT
                && request.requestLine.uri.matches("/[a-z_]+/page/_bulk\\?timeout=1ms")
                && request.requestLine.protocol.equals("HTTP/1.1");
    }

    private boolean isLvsMonitoring(GorRequest request) {
        return !request.headers.containsKey("Content-Length")
                && request.requestLine.method == GET
                && request.requestLine.uri.equals("/")
                && request.requestLine.protocol.equals("HTTP/1.0");
    }

    private boolean isStatsMonitoring(GorRequest request) {
        return !request.headers.containsKey("Content-Length")
                && request.headers.containsKey("User-Agent")
                && request.headers.get("User-Agent").equals("Python-urllib/2.7");
    }
}
