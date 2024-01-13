# Universal Learner Number

A Universal Learner Number (ULN) is a unique identifier assigned to learners in the United Kingdom. It is used to track and record an individual's learning and qualifications across different educational institutions and training providers.

## Installation

```xml
<dependency>
    <groupId>io.github.accuser</groupId>
    <artifactId>uln</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Examples

Create a new ULN object from a String value:

```java
ULN uln = ULN.fromString("0000000042");
```

Validate a ULN:

```java
if (!ULNValidator.isValid(uln)) {
    // ...grumble
};
```

Require a valid ULN:

```java
public record LearnerRegistrationRequest(ULN uln) {
    Learner {
        // `uln` is not `null`
        ULNValidator.requireValidULN(uln);
    }

    Learner(String value) {
        // `ulnValue` is a not `null`
        // `ulnValue` is a valid ULN value
        ULNValidator.requireValidULN(value);
    }
}
```

## Testing

This package is built with [Maven](https://maven.apache.org). 

To clone the package repo and perform unit tests:

```sh
git clone https://github.com/accuser/uln.git
cd uln
mvn test
```

## Contributors

The original author of [`io.github.accuser.uln`](https://github.com/accuser/uln) is [Matthew Gibbons](https://github.com/accuser).

## License

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
