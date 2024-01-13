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

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a 10-digit Unique Learner Number (ULN).
 * 
 * @see <a href=
 *      "https://www.gov.uk/education/learning-records-service-lrs">Learning
 *      Records Service</a>
 */
public final class ULN implements Comparable<ULN>, Serializable {
	@java.io.Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new ULN object from the specified String value.
	 * 
	 * @param value the ULN value as a String
	 * @return a ULN object
	 */
	public static ULN fromString(String value) {
		return new ULN(ULNValidator.requireValidULN(value));
	}

	/**
	 * The ULN value as a String.
	 * 
	 * @serial
	 */
	private final String value;

	/**
	 * Constructs a ULN object with the specified String value.
	 * 
	 * @param value the ULN value as a String
	 */
	private ULN(String value) {
		this.value = value;
	}

	/**
	 * Checks if this ULN is equal to the specified object.
	 *
	 * @param other the object to compare this ULN against
	 * @return {@code true} if the given object represents a ULN equivalent to this
	 *         ULN, {@code false} otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		return (other instanceof ULN uln) && (this.value.equals(uln.value));
	}

	/**
	 * Returns a hash code for this ULN.
	 *
	 * @return a hash code value for this ULN
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}

	/**
	 * Returns a String representation of this ULN.
	 *
	 * @return a string representation of this ULN
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.value + ")";
	}

	/**
	 * Compares this ULN with the specified ULN for order.
	 *
	 * @param other the ULN to compare with
	 * @return a negative integer, zero, or a positive integer as this ULN is less
	 *         than, equal to, or greater than the specified ULN
	 */
	@Override
	public int compareTo(ULN other) {
		return this.value.compareTo(other.value);
	}
}
