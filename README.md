# Android-ObservableScrollView

[![Build Status](http://img.shields.io/travis/ksoichiro/Android-ObservableScrollView.svg?style=flat)](https://travis-ci.org/ksoichiro/Android-ObservableScrollView)
[![Coverage Status](https://img.shields.io/coveralls/ksoichiro/Android-ObservableScrollView/master.svg?style=flat)](https://coveralls.io/r/ksoichiro/Android-ObservableScrollView?branch=master)
[![Maven Central](http://img.shields.io/maven-central/v/com.github.ksoichiro/android-observablescrollview.svg?style=flat)](https://github.com/ksoichiro/Android-ObservableScrollView/releases/latest)
[![API](https://img.shields.io/badge/API-9%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=9)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--ObservableScrollView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1136)

Android library to observe scroll events on scrollable views.  
It's easy to interact with the Toolbar introduced in Android 5.0 Lollipop  and may be helpful to implement look and feel of Material Design apps.

![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo12.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo10.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo11.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo13.gif)

![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo1.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo2.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo3.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo4.gif)

![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo5.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo6.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo7.gif)
![](https://raw.githubusercontent.com/ksoichiro/Android-ObservableScrollView/master/samples/images/demo8.gif)


## Examples

### Download from Google Play

<a href="https://play.google.com/store/apps/details?id=com.github.ksoichiro.android.observablescrollview.samples2"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height="50px"/></a>

Please note that the app on the Play store is not always the latest version.

### Download from wercker

If you are a wercker user, you can download the latest build artifact.  
[See here for details](docs/example/wercker.md).

[![wercker status](https://app.wercker.com/status/8d1e27d9f4a662b25dbe70402733582b/m/master "wercker status")](https://app.wercker.com/project/bykey/8d1e27d9f4a662b25dbe70402733582b)

### Install manually

Just clone and execute `installDevDebug` task with Gradle.  
[See here for details](docs/example/android-studio.md).

## Usage

1. Add `com.github.ksoichiro:android-observablescrollview` to your `dependencies` in `build.gradle`.
1. Add `ObservableListView` or other views you'd like to use.
1. Write some animation codes to the callbacks such as `onScrollChanged`, `onUpOrCancelMotionEvent`, etc.

See [the quick start guide for details](docs/quick-start/index.md),
and [the documentation](docs/overview.md) for further more.

## Reference

* [Supported widgets](docs/reference/supported-widgets.md)
* [Environment](docs/reference/environment.md)
* [Release notes](docs/reference/release-notes.md)
* [FAQ](docs/faq.md)

## Apps that use this library
[![Badge](http://www.libtastic.com/static/osbadges/4.png)](http://www.libtastic.com/technology/4/)

* [Jair Player](https://play.google.com/store/apps/details?id=aj.jair.music) by Akshay Chordiya
* [My Gradle](https://play.google.com/store/apps/details?id=se.project.generic.mygradle) by Erick Chavez Alcarraz
* [ThemeDIY](https://play.google.com/store/apps/details?id=net.darkion.theme.maker) by Darkion Avey
* [{Soft} Skills](https://play.google.com/store/apps/details?id=com.fanaticdevs.androider) by Fanatic Devs

If you're using this library in your app and you'd like to list it here,  
please let me know via [email](mailto:soichiro.kashima@gmail.com) or [pull requests](https://github.com/ksoichiro/Android-ObservableScrollView/pulls) or [issues](https://github.com/ksoichiro/Android-ObservableScrollView/issues).


## Contributions

Any contributions are welcome!  
Please check the [FAQ](docs/faq.md) and [contributing guideline](https://github.com/ksoichiro/Android-ObservableScrollView/tree/master/CONTRIBUTING.md) before submitting a new issue.


## Developed By

* Soichiro Kashima - [soichiro.kashima@gmail.com](mailto:soichiro.kashima@gmail.com)


## Thanks

* Inspired by `ObservableScrollView` in [romannurik-code](https://code.google.com/p/romannurik-code/).


## License

```license
Copyright 2014 Soichiro Kashima

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
