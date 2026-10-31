package com.baidu.ai.aip.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUtilTest {

    @TempDir
    Path directory;

    @Test
    void readsTextAndBytesFromAnExistingFile() throws Exception {
        Path file = directory.resolve("message.txt");
        byte[] bytes = "hello\nworld".getBytes(StandardCharsets.UTF_8);
        Files.write(file, bytes);

        assertThat(FileUtil.readFileAsString(file.toString())).isEqualTo("hello\nworld");
        assertThat(FileUtil.readFileByBytes(file.toString())).containsExactly(bytes);
    }

    @Test
    void reportsMissingFilesForBothReadModes() {
        String absent = directory.resolve("absent.txt").toString();

        assertThatThrownBy(() -> FileUtil.readFileAsString(absent)).isInstanceOf(FileNotFoundException.class);
        assertThatThrownBy(() -> FileUtil.readFileByBytes(absent)).isInstanceOf(FileNotFoundException.class);
    }
}
