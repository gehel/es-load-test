package org.wikimedia.search.load;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.wikimedia.search.load.GorRequest.Meta;
import org.wikimedia.search.load.GorRequest.Method;
import org.wikimedia.search.load.GorRequest.RequestLine;

import com.google.common.collect.ImmutableMap;

@NotThreadSafe
public class GorReader implements Iterator<GorRequest> {

    private final Scanner scanner;
    private GorRequest current;

    public GorReader(InputStream in) {
        scanner = new Scanner(in, UTF_8.toString());
        scanner.useDelimiter(GorRequest.DELIMITER);
        current = parse(scanner.next());
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public GorRequest next() {
        GorRequest result = current;
        current = parse(scanner.next());
        return result;
    }

    @Nullable
    private GorRequest parse(String next) {
        if (next.trim().isEmpty()) {
            return null;
        }
        Scanner scanner = new Scanner(next);

        Meta meta = parseMeta(scanner);
        scanner.nextLine();
        RequestLine requestLine = parseRequestLine(scanner);
        scanner.nextLine();
        Map<String, String> headers = parseHeaders(scanner);
        String payload = parsePayload(scanner);

        return new GorRequest(meta, requestLine, headers, payload);
    }

    private Meta parseMeta(Scanner scanner) {
        int payloadType = scanner.nextInt();
        String uuid = scanner.next();
        Instant timestamp = Instant.ofEpochMilli(scanner.nextLong());
        return new Meta(payloadType, uuid, timestamp);
    }

    private RequestLine parseRequestLine(Scanner scanner) {
        Method method = Method.valueOf(scanner.next());
        String uri = scanner.next();
        String protocol = scanner.next();
        return new RequestLine(method, uri, protocol);
    }

    @SuppressWarnings("InnerAssignment") // inner assignment in a while loop is OK
    private Map<String, String> parseHeaders(Scanner scanner) {
        String line;
        ImmutableMap.Builder<String, String> headers = ImmutableMap.builder();
        while (!(line = scanner.nextLine()).isEmpty()) {
            String[] split = line.split(":", 2);
            headers.put(split[0].trim(), split[1].trim());
        }
        return headers.build();
    }

    private String parsePayload(Scanner scanner) {
        StringJoiner joiner = new StringJoiner("\n");
        while (scanner.hasNextLine()) {
            joiner.add(scanner.nextLine());
        }
        return joiner.toString();
    }

}
