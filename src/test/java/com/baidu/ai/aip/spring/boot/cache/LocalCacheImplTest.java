package com.baidu.ai.aip.spring.boot.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LocalCacheImplTest {

    @Test
    void storesAndInvalidatesValues() throws Exception {
        LocalCacheImpl<String, String> cache = new LocalCacheImpl<>();

        assertThat(cache.get("key")).isNull();
        cache.put("key", "value");
        assertThat(cache.get("key")).isEqualTo("value");
        cache.remove("key");
        assertThat(cache.get("key")).isNull();
    }
}
