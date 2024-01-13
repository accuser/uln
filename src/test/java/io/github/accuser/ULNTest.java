package io.github.accuser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ULNTest {
    @Test
    void testULNFromString() {
        ULN uln = ULN.fromString("0000000042");
        assertInstanceOf(ULN.class, uln);
    }

    @Test
    void testULNFromStringWithNullValue() {
        assertThrows(NullPointerException.class, () -> {
            ULN.fromString(null);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "0000000000", "1111111111", "2222222222", "3333333333", "4444444444", "5555555555",
            "6666666666", "7777777777", "8888888888", "9999999999" })
    void testULNFromStringWithInvalidValue(String value) {
        assertThrows(IllegalArgumentException.class, () -> {
            ULN.fromString(value);
        });
    }

    @Test
    void testEqualsWithSameObject() {
        ULN uln = ULN.fromString("0000000042");
        assertEquals(uln, uln);
    }

    @Test
    void testEqualsWithDifferentULN() {
        ULN uln = ULN.fromString("0000000042");
        ULN other = ULN.fromString("0000000042");
        assertEquals(uln, other);
    }

    @Test
    void testEqualWithNullObject() {
        ULN uln = ULN.fromString("0000000042");
        assertNotEquals(uln, null);
    }

    @Test
    void testToString() {
        ULN uln = ULN.fromString("0000000042");
        assertEquals("ULN(0000000042)", uln.toString());
    }

    @Test
    void testCompareToLessThan() {
        ULN uln1 = ULN.fromString("0000000042");
        ULN uln2 = ULN.fromString("0000000050");
        assertEquals(-1, uln1.compareTo(uln2));
    }

    @Test
    void testCompareToEqualTo() {
        ULN uln1 = ULN.fromString("0000000042");
        ULN uln2 = ULN.fromString("0000000042");
        assertEquals(0, uln1.compareTo(uln2));
    }

    @Test
    void testCompareToGreaterThan() {
        ULN uln1 = ULN.fromString("0000000042");
        ULN uln2 = ULN.fromString("0000000034");
        assertEquals(1, uln1.compareTo(uln2));
    }

    @Test
    void testHashCodeEqual() {
        ULN uln1 = ULN.fromString("0000000042");
        ULN uln2 = ULN.fromString("0000000042");
        assertEquals(uln1.hashCode(), uln2.hashCode());
    }

    @Test
    void testSerializable() throws IOException, ClassNotFoundException {
        ULN uln = ULN.fromString("0000000042");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(uln);
        objectOutputStream.flush();
        objectOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        ULN serializedUln = (ULN) objectInputStream.readObject();

        objectInputStream.close();

        assertEquals(uln, serializedUln);
    }
}