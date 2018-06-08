package com.jeppeman.jetpackplayground.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NumberExtTest {
    @Test
    fun timeFormat_ShouldFormatProperly() {
        assertThat((1000 * 2).timeFormat()).isEqualTo("00:02")
        assertThat((1000 * 20).timeFormat()).isEqualTo("00:20")
        assertThat((1000 * 62).timeFormat()).isEqualTo("01:02")
        assertThat((1000 * 602).timeFormat()).isEqualTo("10:02")
        assertThat((1000 * 620).timeFormat()).isEqualTo("10:20")
        assertThat((1000 * 3602).timeFormat()).isEqualTo("01:00:02")
        assertThat((1000 * 36620).timeFormat()).isEqualTo("10:10:20")
    }
}