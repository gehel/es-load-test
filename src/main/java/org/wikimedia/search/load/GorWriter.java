package org.wikimedia.search.load;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class GorWriter implements Closeable {
    public static final String NEW_LINE = "\r\n";
    private final Writer out;

    public GorWriter(Writer out) {
        this.out = out;
    }

    public void write(GorRequest request) throws IOException {
        write(request.meta);
        write(request.requestLine);
        out.write(NEW_LINE);
        write(request.headers);
        out.write(NEW_LINE);
        write(request.payload);
        out.write(NEW_LINE);
        out.write(GorRequest.DELIMITER);
        out.write(NEW_LINE);
    }

    private void write(GorRequest.Meta meta) throws IOException {
        out.write(Integer.toString(meta.payloadType));
        out.write(" ");
        out.write(meta.uuid);
        out.write(" ");
        out.write(Long.toString(meta.timestamp.toEpochMilli()));
        out.write(NEW_LINE);
    }

    private void write(GorRequest.RequestLine requestLine) throws IOException {
        out.write(requestLine.method.toString());
        out.write(" ");
        out.write(requestLine.uri);
        out.write(" ");
        out.write(requestLine.protocol);
    }

    private void write(Map<String, String> headers) throws IOException {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            out.write(header.getKey());
            out.write(": ");
            out.write(header.getValue());
            out.write(NEW_LINE);
        }
    }

    private void write(String payload) throws IOException {
        out.write(payload);
    }

    @Override
    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
