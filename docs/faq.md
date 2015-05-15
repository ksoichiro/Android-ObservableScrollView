# FAQ

These are frequently asked questions from GitHub issues, emails I received from users, etc.

## Q. When do you implement the new sample? I'm waiting for it so long.

### A. Sorry and please help me if you could.

First of all, I'm so grateful to all of you that you're interested in this project.  
And of course I'd like to respond to all of your request!  
But unfortunately, I don't have enough time to do that...  
If you're interested in implementing new samples or fixing bugs, please help me. (Pull requests are welcome!)

## Q. Does this library support Eclipse?

### A. Yes, it does partially.

Please see [here](../docs/example/eclipse.md) for details.

## Q. Doesn't work!

### A. Please describe your issue as much as possible.

As I wrote in [the contribution guideline](https://github.com/ksoichiro/Android-ObservableScrollView/blob/master/CONTRIBUTING.md),
the library itself only provides the scroll information,
and creating awesome scrolling effects depends deeply on how you use it: layout, offset calculation to animate views, etc.

Therefore, if you find an issue, please describe not only the issue itself but also the related information like Activity name that has problems, operation to produce the issue, modified code (if any), etc.

## Q. Paths are too long to build!

### A. Move your projects upper to shorten the paths.

On Windows environment, if you locate the project directory like `C:\Documents and Settings\user\workspace\Android-ObservableScrollView\`,
Android Studio causes errors and fail to build the project because some of the paths in the output files are too long.

If you see this kind of problem, please move the project directory to upper directory to shorten the paths.

## Q. Sample codes are too complex!

### A. Sorry, please help me refactor them...

Yes, I know they're too complex as samples.

I just aimed to show you that you can realize these kind of effects when you use this library.
I'd very appreciate it if you help me refactor them.

## Q. Updates of the library in master branch seems not be synced to Maven Central.

### A. Sorry, please wait for the next release.

I need to do following tasks to release the new version to the Maven Central, and it takes time, so please wait for the next release.  
If you're in a hurry, please send me an email. I'll release it as soon as possible.

1. Test the library, at least the tests on Travis CI should pass.
1. Check the compatibility for the past versions.
1. Release SNAPSHOT version to the Sonatype snapshot repository.
1. Release to the Sonatype repository. If it's successfully released, it will be synced to Maven Central in a couple of hours.
1. Update README to prompt to use the latest version.

## Q. Can I use this library with API level 8?

### A. It's not supported, but you can.

By adding `tools:overrideLibrary` to `<uses-sdk>` tag,
you can build this library with `android:minSdkVersion="8"`.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example.app">

    <uses-sdk
        android:minSdkVersion="8"
        android:targetSdkVersion="22"
        tools:overrideLibrary="com.github.ksoichiro.android.observablescrollview" />
```

If you have other libraries to override, separate them with comma.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example.app">

    <uses-sdk
        android:minSdkVersion="8"
        android:targetSdkVersion="22"
        tools:overrideLibrary="com.melnykov.fab,com.github.ksoichiro.android.observablescrollview" />
```

