# Build on Eclipse

This library and samples basically support Android Studio and Gradle.  
Because they have strong power to handle dependencies and ability to configure flexibly,  
and this library and sample app depend on them.

However, some of you might still want to build or debug the project on Eclipse.
If you'd like to do that, please try the following instructions.

Please note that with these instructions you could bulid project on Eclipse, but test codes, build types ('debug' or 'release') and product flavors are still not supported.

## Prerequisites

* [Eclipse IDE for Java Developers 4.4 (Luna) SR1](https://eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr1a)
* [Eclipse ADT Plugin](http://developer.android.com/sdk/installing/installing-adt.html)
* Oracle JDK 7
* Android 5.0 SDK Platform (Rev.1+)
* Android Support Repository (Rev.9+)
* Android Support Library (Rev.21.0.2+)

## Instructions

### Get the source codes

Get the source code of the library and example app, by cloning git repository or downloading archives.

If you use git, execute the following command in your workspace directory.

```
$ git clone https://github.com/ksoichiro/Android-ObservableScrollView.git
```

If you are using Windows, try it on GitBash or Cygwin or something that supports git.

### Define ANDROID_HOME environment variable

If you haven't define the environment variable `ANDROID_HOME` yet, define it to indicate Android SDK root directory.

### Generate dependency codes for Eclipse

Before trying to import projects to Eclipse,
execute these command:

```
$ ./gradlew clean generateVersionInfoDebug generateEclipseDependencies
```

This will generate dependency codes from AAR files using Gradle wrapper and some metadata files (`.classpath`, `.project`, `project.properties`).

### Import projects to Eclipse and build app

1. Launch Eclipse.
1. Select `File` > `Import`.
1. Select `General` > `Existing Projects into Workspace` and click `Next`.
    * Warning: DO NOT `Android` > `Existing Android Code into Workspace`.
1. Click `Browse` and select project root directory (`Android-ObservableScrollView`).
1. Check `Search for nested projects`.
1. Select all projects and click next.
1. Some warning messages will be generated, but ignore them and wait until build finishes.

### Run the app

1. Confirm your device is connected.
1. Right click `observablescrollview-samples` and select `Run As` > `Android Application`.

That's all!
 
[Next: Basic techniques &raquo;](../../docs/basic/index.md)
