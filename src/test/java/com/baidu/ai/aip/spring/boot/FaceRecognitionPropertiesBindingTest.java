package com.baidu.ai.aip.spring.boot;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

class FaceRecognitionPropertiesBindingTest {

    @Test
    void bindsAllV2FaceOptionsFromConfiguration() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
                "baidu.face.v2.enabled", "true",
                "baidu.face.v2.client-id", "client",
                "baidu.face.v2.client-secret", "secret",
                "baidu.face.v2.max-face-num", "3",
                "baidu.face.v2.face-fields", "age,gender",
                "baidu.face.v2.user-top-num", "4",
                "baidu.face.v2.faceliveness", "0.75")));

        FaceRecognitionV2Properties props = binder.bind(FaceRecognitionV2Properties.PREFIX,
                Bindable.of(FaceRecognitionV2Properties.class)).get();
        assertThat(props.isEnabled()).isTrue();
        assertThat(props.getClientId()).isEqualTo("client");
        assertThat(props.getClientSecret()).isEqualTo("secret");
        assertThat(props.getMaxFaceNum()).isEqualTo(3);
        assertThat(props.getFaceFields()).isEqualTo("age,gender");
        assertThat(props.getUserTopNum()).isEqualTo(4);
        assertThat(props.getFaceliveness()).isEqualTo(0.75);
    }

    @Test
    void bindsAllV3FaceOptionsFromConfiguration() {
        Binder binder = new Binder(new MapConfigurationPropertySource(Map.of(
                "baidu.face.v3.enabled", "true",
                "baidu.face.v3.client-id", "client",
                "baidu.face.v3.client-secret", "secret",
                "baidu.face.v3.max-face-num", "3",
                "baidu.face.v3.face-fields", "age,gender",
                "baidu.face.v3.max-user-num", "4")));

        FaceRecognitionV3Properties props = binder.bind(FaceRecognitionV3Properties.PREFIX,
                Bindable.of(FaceRecognitionV3Properties.class)).get();
        assertThat(props.isEnabled()).isTrue();
        assertThat(props.getClientId()).isEqualTo("client");
        assertThat(props.getClientSecret()).isEqualTo("secret");
        assertThat(props.getMaxFaceNum()).isEqualTo(3);
        assertThat(props.getFaceFields()).isEqualTo("age,gender");
        assertThat(props.getMaxUserNum()).isEqualTo(4);
    }
}
