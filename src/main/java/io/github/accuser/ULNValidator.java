/*
	Copyright 2024 Matthew Gibbons

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package io.github.accuser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for validating a ULN (Unique Learner Number) value.
 */
public class ULNValidator {
    /**
     * A regular expression for validating the format of a ULN value. A ULN value
     * must be a 10-digit numeric string, padded with leading zeroes.
     */
    private static final String ULN_REGEX = "^(?<digits>[0-9]{9})(?<checkDigit>[0-9]{1})$";

    /**
     * Validates the specified ULN object.
     * 
     * @param uln the ULN object to validate
     * @return the specified ULN object
     * @throws NullPointerException if the specified ULN object is {@code null}
     */
    public static ULN requireValidULN(ULN uln) {
        return Objects.requireNonNull(uln, "ULN object cannot be null");
    }

    /**
     * Validates the specified ULN value.
     * 
     * @param value the ULN value to validate
     * @return the specified ULN value
     * @throws NullPointerException     if the specified ULN value is {@code null}
     * @throws IllegalArgumentException if the specified ULN value is invalid
     */
    public static String requireValidULN(String value) {
        Objects.requireNonNull(value, "ULN value cannot be null");

        if (!isValidULN(value)) {
            throw new IllegalArgumentException("Invalid ULN value");
        }

        return value;
    }

    /**
     * Validates the format of the specified ULN value.
     * 
     * @param value the ULN value to validate
     * @return {@code true} if the specified ULN value is valid; {@code false}
     *         otherwise
     * @throws IllegalArgumentException if the specified ULN value has an invalid
     *                                  format
     * 
     * @see <a href=
     *      "https://assets.publishing.service.gov.uk/media/5cb0e65ce5274a76c9b3299a/WSLP02_ULN_Validation_v3.pdf">Unique
     *      Learner Number (ULN) Validation</a>
     */
    public static boolean isValidULN(String value) {
        Pattern pattern = Pattern.compile(ULN_REGEX);
        Matcher matcher = pattern.matcher(value);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid ULN format");
        }

        String digits = matcher.group("digits");
        Character checkDigit = matcher.group("checkDigit").charAt(0);

        int remainder = calculateSum(digits) % 11;

        if (remainder == 0) {
            return false;
        }

        return (Character.forDigit(10 - remainder, 10) == checkDigit);
    }

    /**
     * Calculates the sum of the ULN digits based on the specified formula.
     * 
     * @param digits the digits of the ULN value
     * @return the sum of the ULN digits
     */
    private static int calculateSum(String digits) {
        int sum = 0;

        for (int i = 0; i < digits.length(); i++) {
            sum += (10 - i) * Character.getNumericValue(digits.charAt(i));
        }

        return sum;
    }

    private ULNValidator() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}