package com.baidu.ai.aip.spring.boot;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class AuthClientHttpTest {

    @Test
    void readsAccessTokenFromSuccessfulHttpResponse() throws Exception {
        RecordingConnection connection = new RecordingConnection();
        URL url = new URL(null, "fixture://token", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL ignored) {
                return connection;
            }
        });

        assertThat(AuthClient.getAuth(url)).isEqualTo("token-value");
        assertThat(connection.getRequestMethod()).isEqualTo("GET");
        assertThat(connection.connected).isTrue();
    }

    private static class RecordingConnection extends HttpURLConnection {
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
        public InputStream getInputStream() {
            return new ByteArrayInputStream("{\"access_token\":\"token-value\"}\n".getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public Map<String, List<String>> getHeaderFields() {
            return Collections.singletonMap("x-test", Collections.singletonList("ok"));
        }
    }
}
