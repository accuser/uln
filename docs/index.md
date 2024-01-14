# Managing Unique Learner Numbers (ULNs)

Imagine we're building an app that needs to track students using their Unique Learner Numbers (ULNs). In the UK, a ULN is a special number given to students over 13 by the Education & Skills Funding Agency. Each ULN is a 10-digit number.

Now, how do we handle these numbers in our Java program? We can use a `long` data type, which is just a way to store large numbers. Here's a simple example:

```java
long uln = 42;
```

Since a ULN is 10 digits, it can range from 0 to 9,999,999,999. But the `long` type in Java can actually hold much bigger numbers, both positive and negative. So, we need to make sure the ULN numbers stay within our required range.

Here's how we can do that:

```java
long requireValidULN(long value) {
    if (value < 0 || value > 9_999_999_999) {
        throw new IllegalArgumentException("Invalid ULN value");
    }

    return value;
}

long uln1 = requireValidULN(42); // This is fine
long uln2 = requireValidULN(-42); // This will cause an error
long uln3 = requireValidULN(10_000_000_042); // This too will cause an error
```

The `requireValidULN()` function checks if the number is within our required range. If not, it stops the program with an error.

To make things even better, let's create a ULN class:

```java
public class ULN {
    private final long value;

    public ULN(long value) {
        this.value = value;
    }
}
```

Here, we've made a new class called `ULN`. It has one field, `value`, to store the ULN. We use clear names to make our code easy to understand.

Notice two things about our `value` field:
- It's `private`, meaning it can't be seen or changed by other parts of our program.
- It's `final`, meaning once we set it, we can't change it.

Now, let's add our validation check to this class:

```java
public class ULN {
    private static long requireValidULN(long value) {
        if (value < 0 || value > 9_999_999_999) {
            throw new IllegalArgumentException("Invalid ULN value");
        }

        return value;
    }

    private final long value;

    public ULN(long value) {
        this.value = requireValidULN(value);
    }
}

ULN uln1 = new ULN(42); // This works fine
ULN uln2 = new ULN(-42); // This will throw an error
ULN uln3 = new ULN(10_000_000_042); // This will also throw an error
```

With this code, our `ULN` class not only stores the ULN value but also ensures it's valid right when we create a new `ULN` object. If we try to create a `ULN` with a number outside our acceptable range, our program will tell us there's a problem by throwing an error.

We now have a neat and efficient way to handle ULN values in our Java application. We've created a special `ULN` class that takes care of storing the ULN and making sure it's a valid number. This approach makes our code more organized, easier to read, and helps prevent mistakes.

## Handling Zero-Padded ULN Values as Strings

We've just realized something important about ULNs: they are written with leading zeros to make them exactly 10 digits long. For example, a ULN might look like `"0000000042"` instead of just `42`. This means our `ULN` class should also handle ULN values given as Strings, not just as long numbers.

To do this, we can add a way to turn a String like `"0000000042"` into a long number. Here's how we can update our `ULN` class:

```java
public class ULN {
    private static long requireValidULN(long value) {
        if (value < 0 || value > 9_999_999_999) {
            throw new IllegalArgumentException("Invalid ULN value");
        }
        return value;
    }

    private final long value;

    public ULN(long value) {
        this.value = requireValidULN(value);
    }

    public ULN(String value) {
        Objects.requireNonNull(value);
        this.value = requireValidULN(Long.parseLong(value));
    }
}

ULN uln1 = new ULN("0000000042"); // This works fine
ULN uln2 = new ULN(42); // This also works fine
```

We've added a new way to create a `ULN` using a String. The program can tell which method to use (String or long) based on the type of data you give it. We also make sure the String is not `null`, because unlike a `long`, a String is an object that can be `null`.

But, we're not quite there yet.

Remember, a ULN is a 10-digit number, usually filled with zeros to reach that length. While we could keep using a `long` to represent it, this doesn't fully capture the idea of a zero-padded 10-digit number. It makes more sense to update our class so that it accepts only valid String representations of a ULN.

Here's how we can modify the `ULN` class:

```java
public class ULN {
    private static String requireValidULN(String value) {
        Objects.requireNonNull(value);

        if (value.length() != 10) {
            throw new IllegalArgumentException("Invalid ULN length");
        }

        // Ensure all characters are digits
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                throw new IllegalArgumentException("Invalid ULN character");
            }
        }

        return value;
    }

    private final String value;

    public ULN(String value) {
        this.value = requireValidULN(value);
    }
}

ULN uln1 = new ULN("0000000042"); // OK
ULN uln2 = new ULN("42"); // This will fail
ULN uln3 = new ULN("chimpanzee"); // This will also fail
```

In this version, we've changed the `ULN` class to only accept Strings. We added a new method `requireValidULN` that checks if the String is exactly 10 characters long and if each character is a digit. This ensures that only proper ULN Strings, like "0000000042", are accepted. 

If the String isn't 10 digits or contains non-digit characters, our method throws an error, telling us something is wrong. This way, we can be confident that any `ULN` object we create will have a valid ULN value.

By switching to using Strings in our `ULN` class, we better capture the real-world format of ULNs. We make sure each ULN is exactly 10 digits long and filled with zeros as needed, just like actual ULNs. This makes our code more accurate and aligns it more closely with how ULNs are used in the real world.

## Improving Validation with Regular Expressions

After considering the format of ULNs more carefully, we realize better way to do this is by using a regular expression. Here's a suitable pattern:

```java
String ULN_REGEX = "^[0-9]{10}$";
```

Let's break down what this pattern means:
- `^` ensures the matching starts at the beginning of the text.
- `[0-9]` checks for a single digit between 0 and 9.
- `{10}` means we expect exactly 10 of the previous pattern (so, 10 digits).
- `$` ensures the matching ends at the end of the text.

This regular expression helps us confirm that a given String is exactly 10 digits long with no other characters. Here's how we use it in our `ULN` class:

```java
public class ULN {
    private static final String ULN_REGEX = "^[0-9]{10}$";

    private static String requireValidULN(String value) {
        Objects.requireNonNull(value);

        if (!value.matches(ULN_REGEX)) {
            throw new IllegalArgumentException("Invalid ULN value");
        }

        return value;
    }

    private final String value;
    
    public ULN(String value) {
        this.value = requireValidULN(value);
    }
}

ULN uln1 = new ULN("0000000042"); // Works correctly
ULN uln2 = new ULN("42"); // Fails, as it should
ULN uln3 = new ULN("chimpanzee"); // Also fails, correctly
```

By integrating this regular expression, we've significantly enhanced the validation in our `ULN` class. Now, it not only checks for the correct length but also ensures that every character is a digit. This approach makes our ULN representation robust and more reliable.

Considering how our `ULN` class is shaping up, it might be a good time to start thinking about adding this to the main codebase. It's always a good idea to commit code in small, manageable pieces, and our `ULN` class seems ready for that step.

## Enhanced ULN Validation with a Check Digit

Upon further review, we've discovered that the 10-digit ULN has an additional complexity: the 10th digit is a check digit, based on a specific [validation algorithm](https://assets.publishing.service.gov.uk/media/5cb0e65ce5274a76c9b3299a/WSLP02_ULN_Validation_v3.pdf). Here's how it works:

1. Extract the first 9 digits of the ULN.
2. Perform a weighted sum: multiply each digit by a decreasing factor (starting from 10 for the first digit to 2 for the ninth), and add these products.
3. Divide this sum by 11 and obtain the remainder. If the remainder is 0, the ULN is invalid.
4. Subtract the remainder from 10. The result should match the tenth (check) digit for a valid ULN.

Let's implement this algorithm. A `for` loop seems appropriate to iterate over the first 9 digits. Assuming we've isolated these digits:

```java
int calculateSum(String digits) {
    int sum = 0;

    for (int i = 0; i < digits.length(); i++) {
        sum += (10 - i) * Character.getNumericValue(digits.charAt(i));
    }

    return sum;
}
```

The `Character.getNumericValue` method is useful here, converting a character to its numeric value.

Next, we need to extract the 9 digits and the check digit from the ULN. Since we're already using a regex for basic validation, let's refine it to capture these parts separately:

```java
String ULN_REGEX = "^(?<digits>[0-9]{9})(?<checkDigit>[0-9])$";
```

By naming the capturing groups (`digits` and `checkDigit`), we can extract them easily after matching:

```java
boolean isValidULN(String value) {
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
```

Here's a breakdown of what happens:

1. **Compile and match the regular expression:**
   ```java
   Pattern pattern = Pattern.compile(ULN_REGEX);
   Matcher matcher = pattern.matcher(value);
   ```
   The regex is compiled for pattern matching. Using this pattern, a matcher checks if `value` conforms to the specified format.

2. **Check for a match and throw en exception if needed:**
   ```java
   if (!matcher.find()) {
       throw new IllegalArgumentException("Invalid ULN format");
   }
   ```
   If there's no match, the ULN format is incorrect, and an exception is thrown. This ensures all following operations are performed on a properly formatted ULN.

3. **Extract digits and check digit:**
   ```java
   String digits = matcher.group("digits");
   Character checkDigit = matcher.group("checkDigit").charAt(0);
   ```
   The first 9 digits and the check digit are extracted using the named capturing groups from the regex.

4. **Calculate sum and validate check digit:**
   ```java
   int remainder = calculateSum(digits) % 11;

   if (remainder == 0) {
      return false;
   }

   return (Character.forDigit(10 - remainder, 10) == checkDigit);
   ```
   The sum of the first 9 digits is calculated, and the modulo operation determines the remainder. If the remainder is 0, the ULN is invalid. Otherwise, the method checks if the calculated check digit matches the actual check digit.

This logic can now be integrated into the `ULN` class:

```java
public class ULN {
    private static final String ULN_REGEX = "^(?<digits>[0-9]{9})(?<checkDigit>[0-9])$";

    // ...truncated

    private static int calculateSum(String digits) {
        int sum = 0;

        for (int i = 0; i < digits.length(); i++) {
            sum += (10 - i) * Character.getNumericValue(digits.charAt(i));
        }

        return sum;
    }

    private static boolean isValidULN(String value) {
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

    private static String requireValidULN(String value) {
        Objects.requireNonNull(value);

        if (!isValidULN(value)) {
            throw new IllegalArgumentException("Invalid ULN value");
        }

        return value;
    }

    private final String value;

    public ULN(String value) {
        this.value = requireValidULN(value);
    }
}

ULN uln1 = new ULN("0000000042"); // OK
ULN uln2 = new ULN("0000000043"); // fails, which is the expected behavior
```

By implementing these changes, the `ULN` class now not only checks the format but also validates the ULN against the specified check digit algorithm, ensuring a robust validation process.

## Introducing a Factory Method for ULN Construction

To enhance the robustness of the `ULN` class, we've decided to implement a factory method for object creation. This approach ensures that a `ULN` instance is only created if it passes the validation checks. The constructor of the class will be made private, so the object instantiation is controlled through the factory method. Here's how this is implemented:

```java
public class ULN {
    // ...truncated

    public static ULN fromString(String value) {
        return new ULN(requireValidULN(value));
    }

    private ULN(String value) {
        this.value = value;
    }
}

ULN uln1 = ULN.fromString("0000000042"); // OK
ULN uln2 = ULN.fromString("0000000043"); // Fails - expected behavior
```

1. **Factory method `fromString`:**
   - The `fromString` method acts as the public interface for creating `ULN` instances. It calls `requireValidULN` to perform all necessary validations.
   - If the validation succeeds, a new `ULN` object is created and returned.

2. **Private constructor:**
   - By making the constructor private, we ensure that `ULN` objects can only be instantiated through the `fromString` method.
   - This encapsulation guarantees that every `ULN` object is valid at the time of creation, as it has passed through the rigorous validation process.

3. **Usage:**
   - The usage of the `ULN` class is now through the static `fromString` method. This method returns a new `ULN` object if the input String is valid, otherwise, it throws an exception.
   - The examples `uln1` and `uln2` demonstrate the method in action. `uln1` is created successfully with a valid ULN string, while `uln2` fails due to an invalid ULN, which is the expected behavior.

There are some real benefits to adopting this approach:
- **Consistency and Safety:** Since the validation is centralized in the factory method, every `ULN` object created is guaranteed to be valid. This adds an extra layer of consistency and safety in the use of the `ULN` class.
- **Better Control:** The private constructor limits object creation to the factory method, providing better control over how `ULN` objects are instantiated.
- **Clear Intentions:** The use of a named method like `fromString` makes it clear that a `ULN` instance is being created from a string representation, enhancing code readability.

By adopting this pattern, the `ULN` class becomes more robust and its usage more intuitive, while still enforcing strict validation rules for creating valid ULN objects.

## Enhancing the ULN Class with Essential Java Features

As we near completion of the `ULN` class, our goal is to integrate key Java functionalities that will make it fully compatible with Java's ecosystem. This involves making the `ULN` class behave like a true Java `Object`, enabling serialization, and ensuring it can be used in sorting operations. Here's a look at the methods we've added:

**Overriding Standard** `Object` **Methods:** `equals`**,** `hashCode`**,** **and** `toString`

- `equals` method:

    ```java
    public class ULN {
        // ...truncated

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof ULN uln) && (this.value.equals(uln.value));
        }
    }
    ```

    This method checks if two `ULN` objects are equal. The first check (`this == other`) confirms if both references point to the same object. The second part uses the `instanceof` operator with a pattern variable `uln`, which both checks the type and casts `other` to `ULN` if they are of the same type. It then compares the `value` fields of both objects.

- `hashCode` method:

    ```java
    public class ULN {
        // ...truncated

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }
    }
    ```

    The `hashCode()` implementation uses the `value` field to calculate the hash code, ensuring consistency with the `equals` method.

- `toString` Method:

    ```java
    public class ULN {
        // ...truncated

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.value + ")";
        }
    }
    ```

    The `toString()` method formats the `ULN` object as a readable string by wrapping the `value` in a format like "ULN(0000000042)" if the `value` is `"0000000042"`.

### Implementing Serialization

   Serialization allows a `ULN` object to be easily saved, transferred, or persisted. To implement this, the `ULN` class is made serializable:

   ```java
   public class ULN implements Serializable {   
       // ...truncated

       @Serial
       private static final long serialVersionUID = 1L;

       @Serial
       private final String value;
   }
   ```

   Here, the class is marked with `Serializable`, and we've included a `serialVersionUID` for version control of the object's serialized form. The `value` field is also serialized as part of the object.

### Adding Comparison Capabilities for Sorting

   By implementing the `Comparable<T>` interface, `ULN` objects can be sorted based on their `value`:

   ```java
   public class ULN implements Comparable<ULN>, Serializable {
       // ...truncated

       @Override
       public int compareTo(ULN other) {
           return this.value.compareTo(other.value);
       }
   }
   ```

   The `compareTo` method delegates the comparison to the `value` field, allowing `ULN` objects to be ordered in a natural sorting order based on their `value`.


With these enhancements, the `ULN` class is now fully equipped for robust use in Java applications. It adheres to Java's standard practices for object comparison, hashing, string representation, serialization, and sorting, making it a well-integrated and functional component. 

The complete implementation of this class is available in the [`io.github.accuser.uln`](https://github.com/accuser/uln) repository.