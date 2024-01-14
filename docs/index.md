I want to build an application that will use ULNs to identify learners. A Unique Learner Number (ULN) is an identifier issued by the UK's Education & Skills Funding Agency (ESFA) to learners over the age of 13. An initial search explained that a ULN was 10 digits long. 

I can use the `long` primative data type in Java to represent a ULN:

```java
long uln = 42;
```

As a ULN is only 10 digits long it can have values ranging from 0 to 9,999,999,999. The `long` primative in Java can actually have a value that ranges from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807. 

I'll need to do something about that:

```java
long requireValidULN(long value) {
    if (value < 0 || value > 9_999_999_999) {
        throw new IllegalArgumentException("Invalid ULN value");
    }

    return value;
}

long uln1 = requireValidULN(42); // OK
long uln2 = requireValidULN(-42); // fails
long uln3 = requireValidULN(10_000_000_042); // fails
```

This is reassuring but I still need to remember to use `requireValidULN()` when working with ULN values. Although, I can do better:

```java
public class ULN {
    private final long value;

    public ULN(long value) {
        this.value = value;
    }
}
```

I've defined a new class called `ULN` which has a single field called `value` to store the underlying value of the ULN. It is worth stopping for a moment to recognise how important clear naming is.

I've marked the field as both `private` and `final`:
 - `private` becuase the field is hidden from code outside the class.
 - `final` because the field is read-only &mdash; although the intial value does need to be set in the constructor.

I can now include the earlier validation as a class method:

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

ULN uln1 = new ULN(42); // OK
ULN uln2 = new ULN(-42); // fails
ULN uln3 = new ULN(10_000_000_042); // fails
```

I now have a useful class to represent ULN values. 

Except that I noticed in the description of ULNs that they are padded with zeroes. This implies that I might need to expect `String` values like `"0000000042"` to be used to initialise a `ULN` object. I can reasonably expect that a valid ULN `String` value is going to be 10 characters long.

There are a few ways to deal with this, but I think that parsing the `String` value to convert it into a `long` value might be the simplest:

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
                
        this(Long.parseLong(value));
    }
}

ULN uln1 = new ULN("0000000042"); // OK
ULN uln2 = new ULN(42); // OK
```

I've added a second constructor that takes a single `String` parameter. The Java compiler knows which constructor I want to use based on the type of the parameter.

I've also added a check in the new constructor to ensure that I don't call it with a `null` value. This is important because a `String` is an object not a primative like a `long`. 

This doesn't feel right though.

A ULN is supposed to be a 10-digit value, padded with zeroes. Whilst that could be represented as a `long` it is not really a `long`. I should really change our class so that only a valid `String` value &mdash; 10 digits, zero-padded &mdash; can be used to construct it. 

```java
public class ULN {
    private static String requireValidULN(String value) {
        Objects.requireNonNull(value);

        if (value.length != 10) {
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
ULN uln2 = new ULN("42"); // fails
ULN uln3 = new ULN("chimpanzee"); // OK?!
```

So, just checking the length the `value` might not be enough. I can use a regular expression to check that all the characters are digits (0-9) and that there are 10 of them:

```java
String ULN_REGEX = "^[0-9]{10}$";
```

Reading from left to right:
- `^` asserts position at start of the line
- `[0-9]` matches a single character in the range between 0 and 9
- `{10}` matches the previous token exactly 10 times
- `$` asserts position at the end of a line

I can use this regular expression in a couple of ways, but for now:

```java
public class ULN {
    private static final String ULN_REGEX = "^[0-9]{10}$";

    private static String requireValidULN(String value) {
        Objects.requireNonNull(value);

        if (!value.match(ULN_REGEX)) {
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
ULN uln2 = new ULN("42"); // fails
ULN uln3 = new ULN("chimpanzee"); // fails
```

This is starting to look good, maybe I should be commiting this to main?

Actually, there is something else: not all of those 10-digits are equal. I've just seen the specification for validating ULNs and I've realised the the 10th digit (the rightmost) is a check digit, and there is an [algorithm](https://assets.publishing.service.gov.uk/media/5cb0e65ce5274a76c9b3299a/WSLP02_ULN_Validation_v3.pdf) that I can apply to calculate it:
    
1. Take the first 9 digits of the ULN.
2. Sum 10 × first digit + 9 × second digit + 8 × third digit + 7 × fourth digit + 6 × fifth digit + 5 × sixth digit + 4 × seventh digit + 3 × eighth digit + 2 × ninth digit
3. Divide this number by 11 and find the remainder (modulo function). If the remainder is 0, the ULN is invalid.
4. Subtract the remainder from 10. If it matches the tenth digit from the entered ULN, the ULN format is valid.

There are a couple of things to note: the multiplication factor in step 2 is a sequence (10, 9, 8, ...) and I need to use a modulo operator to calculate a remainder.

I want to iterate over the first 9 digits of the ULN to calculate the sum, so a `for` loop is probably in order.  I can use the current index of the loop to work out the multiplication factor too. 

Let's assume I can isolate the first 9 digits for now. 

```java
int calculateSum(String digits) {
    int sum = 0;

    for (int i = 0; i < digits.length(); i++) {
        sum += (10 - i) * Character.getNumericValue(digits.charAt(i));
    }

    return sum;
}
```

The `Character` class is very useful here. The static method `getNumericValue` converts a single numeric character into the corresponding numeric value: a `"1"` is converted into `1`. 

I just need to get those 9 digits and the check digit from the `String` value. I could do some `String` manipulation, but I'm already using a regular express to validate the format of the ULN. I can rewite the regular expression to be more explicit about the format:

```java
String ULN_REGEX = "^[0-9]{9}[0-9]$";
```

This will match a `String` value that has 9 digits and then another digit. It is essentially the same as the previous regular expression, but I've split the expression into two parts. I can group those parts.

```java
String ULN_REGEX = "^([0-9]{9})([0-9])$";
```

The `(` and `)` in the regular expression define a capturing group. The regular expression doesn't just match, it captures the sequence of characters in each capturing group. This is really useful, but I can do better.

```java
String ULN_REGEX = "^(?<digits>[0-9]{9})(?<checkDigit>[0-9]{1})$";
```

I've named the capturing groups. If there is a match, I can refer to those captured groups by name. This is how:

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

Let me explain:

```
Pattern pattern = Pattern.compile(ULN_REGEX);
Matcher matcher = pattern.matcher(value);
```

The regular expression is compiled so that it can be used for pattern matching, and than I use that pattern to build a matcher that will match the `value`. This is a slightly more involved used of regular expression than before, but we can assumed that `value.match(ULN_REGEX)` does a lot of this itself.

```java
if (!matcher.find()) {
    throw new IllegalArgumentException("Invalid ULN format");
}
```

This is pretty straightforward: if the `matcher` can't find a match, the `String` value is not the correct format, so throw an expception. What this also means is that all the statements that follow this `if` statement are going to be executing with the assurity that there is a match.

```java
String digits = matcher.group("digits");
Character checkDigit = matcher.group("checkDigit").charAt(0);
```

`matcher.group("digits")` returns the capatured group named `digits`, as `matcher.group("checkDigit")` return the capture group named `checkDigit`. I've now isolated those first 9 digits, and I've got the check digit too.

```java
int remainder = calculateSum(digits) % 11;

if (remainder == 0) {
   return false;
}
```

I can use the `digits` to calculate the sum and then apply the modulo operator (`%`) to find the remainder. If the remainder is 0, the ULN is not a valid value. I return `false`, because the answer to the question `isValidULN` is no. I don't throw an exception: the format of the ULN value is correct, it is just that the value is not valid. 

```
return (Character.forDigit(10 - remainder, 10) == checkDigit);
```

If the checksum for the first 9 digits is equal to the check digit, this is a valid ULN value.

Adding this all to the `ULN` class:

```java
public class ULN {
    private static final String ULN_REGEX = "^(?<digits>[0-9]{9})(?<checkDigit>[0-9]{1})$";

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

        if (!value.match(ULN_REGEX)) {
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
ULN uln2 = new ULN("0000000043"); // fails - but this is expected behavior
```

I think that it is a good idea for a constructor to not fail. I'm going to add a factory method to the class that will do the validation before constructing the `ULN` object. I'll make the class constructor private so that only the factory method can be used.

```java
public class ULN {
    // ...truncated

    public static ULN fromString(String value) {
        new ULN(requireValidULN(value));
    }

    private ULN(String value) {
        this.value = value;
    }
}

ULN uln1 = ULN.fromString("0000000042"); // OK
ULN uln2 = ULN.fromString("0000000043"); // fails - but this is expected behavior
```

The constructor is only called with a valid value, so if we have a `ULN` object we know that it is valid.

I'm nearly finished, but I want to add a few more methods. To make the `ULN` class work well in any Java program it would be helpful if it acted like a real `Object`, it could be serialized, and it could be compared for sorting. 

I'll start with the `Object` methods that I want to override: `equals`, `hashCode`, and `toString`.

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

This is quite straightforward: I want to be able to see if two `ULN` objects are equal. The first `if` statement will return `true` if `this` and `other` are the same, i.e., if I had done something like:

```java
ULN uln1 = ULN.fromString("0000000042");

if (uln1.equals(uln1)) {
    // ...
}
```

I'd never knowingly do that, but it is worth checking. 

The `return` statement evaluates the expression `other instanceof ULN uln` by first checking if `other` is an instance of `ULN`, and if it is, creates a new variable `uln` of type `ULN` that I can use in rest of the statement. This is really useful, and avoid me having to cast `other` as `ULN`. It also means that if the first expression is `true` (i.e., `other` is an instance of `ULN`) then the return statement will evaluate the second expression `this.value.equals(uln.value)`, which is `true` if the `value` field of `this` is equal to the `value` field of `uln`. Notice that I defer to the `equals()` of the `value` field here.

```java
public class ULN {
    // ...truncated

    @Override
    public int hashCode() {
		return Objects.hash(this.value);
	}
}
```

`hashCode()` is quite straightforward: I'm using `this.value` for the `equals()` comparrison, and so I should use `this.value` to calculate the hash code of the object.

```java
public class ULN {
    // ...truncated

    @Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.value + ")";
	}
}
```

`toString()` wraps `value` in with `ULN(` and `)`. If value was `"0000000042"` then `toString()` would return `"ULN(0000000042)"`.

Serialization is a feature that allows objects to be written to and read from streams, which enables them to be persisted (saved) or transferred (sent or recevied). For such a simple object, it is quite straightforward to implement:

```java
public class ULN implements Serializable {   
    // ...truncated

    @Serial
	private static final long serialVersionUID = 1L;

    @Serial
	private final String value;
}
```

Finaly, I want to implent `Comparable<T>` that adds an interface for comparing two objects for sorting. As with `equals()` and `hashCode()`, I can delegate to the underlying `value` field.

```java
public class ULN implements Comparable<ULN>, Serializable {
    // ...truncated

    @Override
    public int compareTo(ULN other) {
        return this.value.compareTo(other.value);
    }
}
```

I think that is everything. The finished class can be found in the [`io.github.accuser.uln`](https://github.com/accuser/uln) repo.