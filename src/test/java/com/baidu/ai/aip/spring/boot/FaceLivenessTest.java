/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baidu.ai.aip.spring.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {{ @link FaceLiveness }}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("FaceLiveness Tests")
class FaceLivenessTest {

    @Test
    @DisplayName("Exposes supported values")
    void exposesSupportedValues() {
        assertThat(FaceLiveness.values()).containsExactly(FaceLiveness.NONE, FaceLiveness.LOW, FaceLiveness.NORMAL, FaceLiveness.HIGH);
        assertThat(FaceLiveness.valueOf("NONE")).isSameAs(FaceLiveness.NONE);
    }
}
