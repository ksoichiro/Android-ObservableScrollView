# Building on Eclipse

This library only supports Android Studio and Gradle.  
Because they have strong power to handle dependencies and ability to configure flexibly,  
and this library and sample app depend on them.

However, if you really want to build it on Eclipse, here are some hints.  
These are not the complete and precise instructions but it might help you.

If you feel these complicated instructions annoying, then maybe it's time to use the Android Studio.

## Import library to your project

1. Install the following components on SDK manager.
    * Android 5.0 SDK Platform (Rev.1+)
    * Android Support Repository (Rev.9+)
    * Android Support Library (Rev.21.0.2+)
1. Import library project of this lib (`observablescrollview` directory) to your workspace.
    * Import with "Import Android Code Into Workspace".
    * Add `android-support-v7-recyclerview.jar` to build path.  
      Jar file is located here:  
      `/path/to/sdk/extra/android/support/v7/recyclerview/libs/android-support-v7-recyclerview.jar`

## Build the sample app

After importing library to your workspace, import other dependencies and the sample app.

1. Import "android-support-v7-appcompat" project to your workspace.
    * This project is located in the following path:  
      `/path/to/sdk/extra/android/support/v7/appcompat`
    * See [here](https://developer.android.com/tools/support-library/setup.html#libs-with-res) for details:
    * Note that you must modify `target=android-19` to `target=android-21` on `project.properties`.  
    * Also add `android-support-v4.jar` and `android-support-v7-appcompat.jar` to build path.  
      They also should be exported. (On "Java Build Path > Order and Export" setting)
1. Import [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids/) library to your workspace.
    * Import "library" directory with "Import Android Code Into Workspace".
1. Import [FloatingActionButton](https://github.com/makovkastar/FloatingActionButton) library to your workspace.
    * This lib also uses Android Studio(Gradle),
      so import `library` directory with "Import Android Code Into Workspace".
    * This depends on "NineOldAndroids" library, so add it as a library.
    * Also add `android-support-v4.jar` and `android-support-v7-recyclerview.jar` to build path.  
      They also should be exported. (On "Java Build Path > Order and Export" setting)
1. Import sample project of this lib (`observablescrollview-samples` directory) to your workspace.
    * Import with `Import Android Code Into Workspace`.
    * Add `android-support-v4.jar` and `android-support-v7-recyclerview.jar` to build path.
    * This depends on "android-support-v7-appcompat",
      "FloatingActionButton" and "observablescrollview" libraries,
      so add them as a library.
    * Sample codes depends on Gradle build mechaninsm, and some codes should be modified:
          * On `AndroidManifest.xml`,
              * Add `android:versionName` and `android:versionCode` attributes to `<manifest>` element.
              * Replace all `<category android:name="${applicationId}" />` to `<category android:name="com.github.ksoichiro.android.observablescrollview.samples"
/>`.
              * Add `android:targetSdkVersion="19"` to `<uses-sdk>` element.
          * On `MainActivity`, replace value of `CATEGORY_SAMPLES` to `"com.github.ksoichiro.android.observablescrollview.samples"`.
          * On `AboutActivity`, remove the following lines.
              * `((TextView)
findViewById(R.id.app_version)).setText(getString(R.string.msg_app_version,
BuildConfig.VERSION_NAME, BuildConfig.GIT_HASH));`
              * `((TextView)
findViewById(R.id.lib_version)).setText(getString(R.string.msg_lib_version,
BuildConfig.LIB_VERSION));`
