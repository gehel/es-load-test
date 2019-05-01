package org.wikimedia.search.load;

import java.time.Instant;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import lombok.ToString;

@ToString
public final class GorRequest {

    public static final String DELIMITER = "\uD83D\uDC35\uD83D\uDE48\uD83D\uDE49";

    public final Meta meta;
    public final RequestLine requestLine;
    public final Map<String, String> headers;
    public final String payload;

    public GorRequest(Meta meta, RequestLine requestLine, Map<String, String> headers, String payload) {
        this.meta = meta;
        this.requestLine = requestLine;
        this.headers = ImmutableMap.copyOf(headers);
        this.payload = payload;
    }


    public enum Method {
        GET, HEAD, POST, OPTIONS, PUT, DELETE, TRACE, CONNECT
    }

    @ToString
    public static final class RequestLine {
        public final Method method;
        public final String uri;
        public final String protocol;

        public RequestLine(Method method, String uri, String protocol) {
            this.method = method;
            this.uri = uri;
            this.protocol = protocol;
        }
    }

    @ToString(exclude = "payload")
    public static final class Meta {
        public final int payloadType;
        public final String uuid;
        public final Instant timestamp;

        public Meta(int payloadType, String uuid, Instant timestamp) {
            this.payloadType = payloadType;
            this.uuid = uuid;
            this.timestamp = timestamp;
        }
    }
}
