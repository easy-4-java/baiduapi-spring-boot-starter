package com.baidu.ai.aip.spring.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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

class FaceRecognitionV3ApiContractTest {

    private final FaceRecognitionV3Template template = new FaceRecognitionV3Template(new FaceRecognitionV3Properties()) {
        @Override
        public String getAccessToken(String clientId, String clientSecret) {
            return "test-token";
        }
    };

    @Test
    void detectsMatchesAndSearchesImagesWithDocumentedOptions() {
        byte[] image = {1, 2, 3};
        try (MockedStatic<HttpUtil> http = successfulHttp()) {
            assertSuccess(template.detect(image));
            assertSuccess(template.detect(image, FaceType.IDCARD));
            assertSuccess(template.detect(image, FaceType.CERT, FaceLiveness.HIGH));
            assertSuccess(template.detect("image"));
            assertSuccess(template.detect("image", FaceType.WATERMARK));
            assertSuccess(template.detect("image", FaceType.LIVE, FaceLiveness.LOW));

            assertSuccess(template.match(image, image));
            assertSuccess(template.match(image, image, FaceType.IDCARD));
            assertSuccess(template.match(image, image, FaceType.CERT, FaceQuality.HIGH));
            assertSuccess(template.match(image, image, FaceType.LIVE, FaceQuality.LOW, FaceLiveness.HIGH));
            assertSuccess(template.match("one", "two"));
            assertSuccess(template.match("one", "two", FaceType.IDCARD));
            assertSuccess(template.match("one", "two", FaceType.CERT, FaceQuality.NORMAL));
            assertSuccess(template.match("one", "two", FaceType.LIVE, FaceQuality.HIGH, FaceLiveness.LOW));

            assertSuccess(template.search(image, "group"));
            assertSuccess(template.search(image, "group", FaceQuality.LOW));
            assertSuccess(template.search("image", "group"));
            assertSuccess(template.search("image", "group", FaceQuality.NORMAL));
            assertSuccess(template.search("image", "group", FaceQuality.HIGH, FaceLiveness.LOW));

            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV3Template.FACE_DETECT_URL),
                    eq("test-token"), eq(FaceRecognitionV3Template.CONTENT_TYPE),
                    contains("\"face_type\":\"CERT\"")), atLeastOnce());
            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV3Template.FACE_MATCH_URL),
                    eq("test-token"), eq(FaceRecognitionV3Template.CONTENT_TYPE), anyString()), atLeastOnce());
            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV3Template.FACE_SEARCH_URL),
                    eq("test-token"), eq(FaceRecognitionV3Template.CONTENT_TYPE), anyString()), atLeastOnce());
        }
    }

    @Test
    void managesUsersGroupsAndVerificationAtTheirExpectedEndpoints() {
        byte[] image = {1, 2, 3};
        try (MockedStatic<HttpUtil> http = successfulHttp()) {
            assertSuccess(template.faceNew("image", "group", "user", "info", FaceQuality.LOW, FaceLiveness.HIGH));
            assertSuccess(template.faceRenew("image", "group", "user", "info", FaceQuality.HIGH, FaceLiveness.LOW));
            assertSuccess(template.faceDelete("group", "user", "face-token"));
            assertSuccess(template.faceInfo("group", "user"));
            assertSuccess(template.faceList("group", "user"));
            assertSuccess(template.faceUsers("group", 0, 5));
            assertSuccess(template.userCopy("group", "user", "target"));
            assertSuccess(template.userDelete("group", "user"));
            assertSuccess(template.groupNew("group"));
            assertSuccess(template.groupDelete("group"));
            assertSuccess(template.groupList(0, 5));

            assertSuccess(template.personverify(image, "card", "name"));
            assertSuccess(template.personverify(image, "card", "name", FaceQuality.HIGH));
            assertSuccess(template.personverify("image", "card", "name"));
            assertSuccess(template.personverify("image", "card", "name", FaceQuality.LOW));
            assertSuccess(template.personverify("image", "card", "name", FaceQuality.HIGH, FaceLiveness.LOW));
            assertSuccess(template.faceVerify(image, FaceOption.COMMON));
            assertSuccess(template.faceVerify("image", FaceOption.GATE));
            assertSuccess(template.merge(image, image));
            assertSuccess(template.merge("one", "two"));

            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV3Template.FACESET_USER_ADD_URL),
                    eq("test-token"), eq(FaceRecognitionV3Template.CONTENT_TYPE), anyString()), atLeastOnce());
            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV3Template.FACE_PERSON_VERIFY_URL),
                    eq("test-token"), eq(FaceRecognitionV3Template.CONTENT_TYPE), anyString()), atLeastOnce());
            http.verify(() -> HttpUtil.post(eq(FaceRecognitionV3Template.FACE_MERGE_URL),
                    eq("test-token"), eq(FaceRecognitionV3Template.CONTENT_TYPE), anyString()), atLeastOnce());
        }
    }

    private static MockedStatic<HttpUtil> successfulHttp() {
        return mockStatic(HttpUtil.class, invocation -> "{\"error_code\":\"0\"}");
    }

    private static void assertSuccess(JSONObject result) {
        assertThat(result).isNotNull();
        assertThat(result.getInteger("error_code")).isZero();
    }

    @Test
    void returnsNoResultWhenRemoteFaceApiRejectsTheRequest() {
        List<Supplier<JSONObject>> operations = Arrays.asList(
                () -> template.detect("image", FaceType.LIVE, FaceLiveness.LOW),
                () -> template.match("one", "two", FaceType.LIVE, FaceQuality.LOW, FaceLiveness.HIGH),
                () -> template.search("image", "group", FaceQuality.LOW, FaceLiveness.HIGH),
                () -> template.faceNew("image", "group", "user", "info", FaceQuality.LOW, FaceLiveness.HIGH),
                () -> template.faceRenew("image", "group", "user", "info", FaceQuality.LOW, FaceLiveness.HIGH),
                () -> template.faceDelete("group", "user", "token"),
                () -> template.faceInfo("group", "user"),
                () -> template.faceList("group", "user"),
                () -> template.faceUsers("group", 0, 5),
                () -> template.userCopy("group", "user", "target"),
                () -> template.userDelete("group", "user"),
                () -> template.groupNew("group"),
                () -> template.groupDelete("group"),
                () -> template.groupList(0, 5),
                () -> template.personverify("image", "card", "name", FaceQuality.LOW, FaceLiveness.HIGH),
                () -> template.faceVerify("image", FaceOption.GATE),
                () -> template.merge("one", "two"));
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class,
                invocation -> { throw new IOException("remote request failed"); })) {
            for (Supplier<JSONObject> operation : operations) {
                assertThat(operation.get()).isNull();
            }
        }
    }
}
