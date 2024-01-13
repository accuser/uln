package io.github.accuser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ULNValidatorTest {
    @Test
    public void testRequireValidULNWithValidULN() {
        ULN uln = ULN.fromString("0000000042");
        ULN result = ULNValidator.requireValidULN(uln);
        assertEquals(uln, result);
    }

    @Test
    public void testRequireValidULNWithNullULN() {
        assertThrows(NullPointerException.class, () -> {
            ULNValidator.requireValidULN((ULN) null);
        });
    }

    @Test
    public void testRequireValidULNWithValidString() {
        String result = ULNValidator.requireValidULN("0000000042");
        assertEquals("0000000042", result);
    }

    @Test
    public void testRequireValidULNWithNullString() {
        assertThrows(NullPointerException.class, () -> {
            ULNValidator.requireValidULN((String) null);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "0000000000", "1111111111", "2222222222", "3333333333", "4444444444", "5555555555",
            "6666666666", "7777777777", "8888888888", "9999999999" })
    public void testRequireValidULNWithInvalidString(String value) {
        assertThrows(IllegalArgumentException.class, () -> {
            ULNValidator.requireValidULN(value);
        });
    }

    public void testIsValidULNWithValidString() {
        boolean result = ULNValidator.isValidULN("0000000042");
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = { "0000000000", "1111111111", "2222222222", "3333333333", "4444444444", "5555555555",
            "6666666666", "7777777777", "8888888888", "9999999999" })
    public void testIsValidULNWithInvalidString(String value) {
        boolean result = ULNValidator.isValidULN(value);
        assertFalse(result);
    }
}