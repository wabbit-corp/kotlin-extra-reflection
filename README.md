# kotlin-extra-reflection

Utility functions to enhance Kotlin class reflection capabilities, making it easier for developers to work with class hierarchies and parent relationships within their Kotlin applications.

## Installation

Add the following dependency to your project:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.wabbit-corp:kotlin-extra-reflection:1.0.0")
}
```

## Usage

1. **`superclasses` Function Usage Example:**
   ```kotlin
   val stringSuperclasses = String::class.java.superclasses()
   println(stringSuperclasses) // Example output: [java.lang.Object]
   ```
   This function returns a list of all superclasses of a class, and could be used to examine the inheritance hierarchy of a class.

2. **`parents` Function Usage Example:**
   ```kotlin
   val stringParents = String::class.java.parents()
   println(stringParents) 
   // Example output: [class java.lang.String, class java.lang.Object, interface java.io.Serializable, interface java.lang.Comparable, interface java.lang.CharSequence]
   ```
   This function returns a list of all parent classes and interfaces that the current class inherits from or implements.

## Licensing

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0) for open source use.

For commercial use, please contact Wabbit Consulting Corporation (at wabbit@wabbit.one) for licensing terms.

## Contributing

Before we can accept your contributions, we kindly ask you to agree to our Contributor License Agreement (CLA).
