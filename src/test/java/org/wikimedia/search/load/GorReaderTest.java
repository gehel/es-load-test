package org.wikimedia.search.load;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Test;

import com.google.common.io.CharStreams;

public class GorReaderTest {

    @Test
    public void readInput() throws IOException {

        StringWriter out = new StringWriter();

        try (
                InputStream in = getResource();
                GorWriter writer = new GorWriter(out)
        ) {

            GorReader reader = new GorReader(in);

            while (reader.hasNext()) {
                writer.write(reader.next());
            }
        }
        String result = out.toString();
        assertThat(result).isEqualTo(getResourceContent());
    }

    private String getResourceContent() throws IOException {
        return CharStreams.toString(new InputStreamReader(getResource(), UTF_8));
    }

    private InputStream getResource() {
        return GorReader.class.getResourceAsStream("/requests.gor");
    }

}
