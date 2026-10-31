package com.baidu.ai.aip.spring.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.alibaba.fastjson2.JSONObject;
import com.baidu.ai.aip.utils.HttpUtil;

class FaceRecognitionV2ApiContractTest {

    private final FaceRecognitionV2Template template = new FaceRecognitionV2Template(new FaceRecognitionV2Properties()) {
        @Override
        public String getAccessToken(String clientId, String clientSecret) {
            return "test-token";
        }
    };

    @Test
    void detectSendsEncodedImageToExpectedEndpoint() {
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class, invocation -> "{\"error_code\":\"0\"}")) {
            JSONObject result = template.detect("image+bytes");

            assertThat(result.getInteger("error_code")).isZero();
            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV2Template.FACE_DETECT_URL),
                    eq("test-token"), contains("image=image%2Bbytes")), atLeastOnce());
        }
    }

    @Test
    void routesFaceAndGroupOperationsToTheirEndpoints() {
        byte[] image = {1, 2, 3};
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class, invocation -> "{\"error_code\":\"0\"}")) {
            assertSuccess(template.detect(image));
            assertSuccess(template.match(image, image));
            assertSuccess(template.match(image, image, "live"));
            assertSuccess(template.match(image, image, "live", "7,13"));
            assertSuccess(template.match("one", "two"));
            assertSuccess(template.match("one", "two", "live"));
            assertSuccess(template.match("one", "two", "live", "7,13"));
            assertSuccess(template.search(image, "group"));
            assertSuccess(template.search("image", "group"));
            assertSuccess(template.faceNew("image", "group", "user", "info"));
            assertSuccess(template.faceRenew("image", "group", "user", "info"));
            assertSuccess(template.faceDelete("group", "user"));
            assertSuccess(template.faceInfo("group", "user"));
            assertSuccess(template.faceUsers("group", 0, 5));
            assertSuccess(template.groupList(0, 5));
            assertSuccess(template.userCopy("group", "user", "target"));
            assertSuccess(template.userDelete("group", "user"));
            assertSuccess(template.personverify("image", "card", "name"));
            assertSuccess(template.faceVerify("image"));

            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV2Template.FACE_MATCH_URL),
                    eq("test-token"), anyString()), atLeastOnce());
            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV2Template.FACESET_USER_ADD_URL),
                    eq("test-token"), anyString()), atLeastOnce());
            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV2Template.FACE_PERSON_VERIFY_URL),
                    eq("test-token"), anyString()), atLeastOnce());
        }
    }

    private static void assertSuccess(JSONObject result) {
        assertThat(result).isNotNull();
        assertThat(result.getInteger("error_code")).isZero();
    }

    @Test
    void returnsNoResultWhenRemoteFaceApiRejectsTheRequest() {
        List<Supplier<JSONObject>> operations = Arrays.asList(
                () -> template.detect("image"),
                () -> template.match("one", "two", "live", "7,13"),
                () -> template.search("image", "group"),
                () -> template.faceNew("image", "group", "user", "info"),
                () -> template.faceRenew("image", "group", "user", "info"),
                () -> template.faceDelete("group", "user"),
                () -> template.faceInfo("group", "user"),
                () -> template.faceUsers("group", 0, 5),
                () -> template.groupList(0, 5),
                () -> template.userCopy("group", "user", "target"),
                () -> template.userDelete("group", "user"),
                () -> template.personverify("image", "card", "name"),
                () -> template.faceVerify("image"));
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class,
                invocation -> { throw new IOException("remote request failed"); })) {
            for (Supplier<JSONObject> operation : operations) {
                assertThat(operation.get()).isNull();
            }
        }
    }
}
