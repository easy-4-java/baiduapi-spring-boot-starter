package com.baidu.ai.aip.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HttpUtilTest {

    @Test
    void postsEncodedBodyAndReadsCompleteResponse() throws Exception {
        RecordingConnection connection = new RecordingConnection();
        URL url = new URL(null, "fixture://face", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL ignored) {
                return connection;
            }
        });

        String response = HttpUtil.postGeneralUrl(url, "application/json", "café", "UTF-8");

        assertThat(response).isEqualTo("firstsecond");
        assertThat(connection.getRequestMethod()).isEqualTo("POST");
        assertThat(connection.getRequestProperty("Content-Type")).isEqualTo("application/json");
        assertThat(connection.body.toByteArray()).isEqualTo("café".getBytes(StandardCharsets.UTF_8));
        assertThat(connection.connected).isTrue();
    }

    private static class RecordingConnection extends HttpURLConnection {
        private final ByteArrayOutputStream body = new ByteArrayOutputStream();
        private boolean connected;

        RecordingConnection() throws Exception {
            super(new URL("http://localhost/fixture"));
        }

        @Override
        public void connect() {
            connected = true;
        }

        @Override
        public void disconnect() {
            connected = false;
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public OutputStream getOutputStream() {
            return body;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream("first\nsecond\n".getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public Map<String, List<String>> getHeaderFields() {
            return Collections.singletonMap("x-test", Collections.singletonList("ok"));
        }
    }
}
