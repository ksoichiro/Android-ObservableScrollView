# Dependencies

This library is published to the Maven Central repository, so you can use it through Gradle/Maven.  
You can use it in Eclipse, but Android Studio (or Gradle) is recommended.  
In Quick start guide, we assume you're using Android Studio.

## build.gradle

Write the following dependency configuration to your `build.gradle`.

```gradle
repositories {
    mavenCentral()
}

dependencies {
    // Other dependencies are omitted
    compile 'com.github.ksoichiro:android-observablescrollview:VERSION'
}
```

You should replace `VERSION` to the appropriate version number like `1.5.0`.

Then, click "sync" button to get the library using the configuration above.

To confirm the available versions, search [the Maven Central Repository](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.ksoichiro%22%20AND%20a%3A%22android-observablescrollview%22).

[Next: Layout &raquo;](../../docs/quick-start/layout.md)
