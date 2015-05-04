# Translating the Toolbar

This topic describes how to translate the Toolbar,
which are implemented in the following examples.

* ToolbarControlBaseActivity
* ToolbarControlGridViewActivity
* ToolbarControlListViewActivity
* ToolbarControlRecyclerViewActivity
* ToolbarControlScrollViewActivity
* ToolbarControlWebViewActivity

---

## About the Toolbar

In this section we learn how to translate the Toolbar.  
Toolbar was introduced on Android 5.0, and you can also use it on pre-Lollipop devices
by using [v7 appcompat library](http://developer.android.com/tools/support-library/features.html#v7-appcompat)
of the Android Support Library package.

## Design of the examples

The existing examples above, `ToolbarControlBaseActivity` has most of the codes to avoid writing duplicate codes.
If you use one of them, you don't have to use this structure: extending Activity is not required to achieve this effect.

## Create layout file

In this topic, we use `ObservableListView` and `Toolbar`, and wrap them with `FrameLayout`.  
`FrameLayout` and `RelativeLayout` are useful to translate views inside of it separately.

```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  android:layout_width="match_parent"
  android:layout_height="match_parent">

  <android.support.v7.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="?attr/colorPrimary"
    android:minHeight="?attr/actionBarSize"
    app:popupTheme="@style/Theme.AppCompat.Light.DarkActionBar"
    app:theme="@style/Toolbar" />

  <com.github.ksoichiro.android.observablescrollview.ObservableListView
    android:id="@+id/scrollable"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="?attr/actionBarSize" />
</FrameLayout>
```

## How to translate the Toolbar

The basic idea about showing/hiding the Toolbar is exactly the same as the ActionBar.
However, the Toolbar class does not provide any convinient methods like `show()` and `hide()` which the ActionBar class has.
Therefore we should implement such methods to translate the Toolbar.
Our goal is to make the following codes work:

```java
@Override
public void onUpOrCancelMotionEvent(ScrollState scrollState) {
  if (scrollState == ScrollState.UP) {
    if (toolbarIsShown()) { // TODO Not implemented
      hideToolbar(); // TODO Not implemented
    }
  } else if (scrollState == ScrollState.DOWN) {
    if (toolbarIsHidden()) { // TODO Not implemented
      showToolbar(); // TODO Not implemented
    }
  }
}
```

## Using NineOldAndroids

Before we begin, you should confirm whether you're going to support pre-Honeycomb devices.
To translate the Toolbar, we would like to use the [Property Animation APIs](http://developer.android.com/guide/topics/graphics/prop-animation.html)
which are introduced in API level 11, so if you are going to support pre-Honeycomb devices,
[JakeWharton/NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids/) might be useful (although it's marked as deprecated).

In this project, all the examples use NineOldAndroids.
So if you don't support pre-Honeycomb devices, please replace `ViewHelper.methodName(viewObject)` to `viewObject.methodName()`.

```
NineOldAndroids: ViewHelper.getTranslationY(mToolbar)
Platform API:    mToolbar.getTranslationY()
```

If you use NineOldAndroids, add an entry to the `dependencies` closure in your `build.gradle`:

```gradle
dependencies {
    compile 'com.nineoldandroids:library:2.4.0'
}
```

## toolbarIsShown()/toolbarIsHidden()

Now let's start from the easiest part.  
To avoid redundant translation, we need methods to check if the Toolbar is shown or hidden.  
With the property animation APIs (or NineOldAndroids), we just simply check the `translationY` property.

```java
private boolean toolbarIsShown() {
  // Toolbar is 0 in Y-axis, so we can say it's shown.
  return ViewHelper.getTranslationY(mToolbar) == 0;
}

private boolean toolbarIsHidden() {
  // Toolbar is outside of the screen and absolute Y matches the height of it.
  // So we can say it's hidden.
  return ViewHelper.getTranslationY(mToolbar) == -mToolbar.getHeight();
}
```

## Implement showToolbar()/hideToolbar()

Next, let's implement methods to animate the Toolbar.  
Before thinking about details, write some pseudocodes to simplify the problem.  
To show or hide the Toolbar, we just need one method to move the Toolbar.

```java
private void showToolbar() {
  moveToolbar(0);
}

private void hideToolbar() {
  moveToolbar(-mToolbar.getHeight());
}
```

This should work, if we implement the `moveToolbar` method correctly :)

Most of the animation codes are combination of property value calculations,
and I think it's very hard to keep these information in my brain or imagine correctly.
And this approach is useful to implement the complex animation.

## Implement moveToolbar()

Although we named the method `moveToolbar`, it's not everything we need to handle.
In ActionBar examples, not only the ActionBar is moved but also the height of the view (`Observable*View`) is changed.
And we need to implement this behavior for the Toolbar.

To use the changing property values, we can use `ValueAnimator`.  
`ValueAnimator` has a callback method `onAnimationUpdate`, and we can get the animation progress from it.  
`ValueAnimator` itself does not animate anything, we need to animate something using a parameter of the callback.

```java
ValueAnimator animator = ValueAnimator.ofFloat(0, 100).setDuration(200);
animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
  @Override
  public void onAnimationUpdate(ValueAnimator animation) {
    float value = (float) animation.getAnimatedValue();
    // You can do whatever you want with the `value`.
  }
});
```

In the example code above, the local variable `value` changes from `0f` to `100f` in 200ms.  
In this case, we should change the `translationY` property of the Toolbar,
and change the height of the `Observable*View` like this:

```java
private void moveToolbar(float toTranslationY) {
  ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mToolbar), toTranslationY).setDuration(200);
  animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      float translationY = (float) animation.getAnimatedValue();
      ViewHelper.setTranslationY(mToolbar, translationY);
      ViewHelper.setTranslationY((View) mScrollable, translationY);
      FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
      lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
      ((View) mScrollable).requestLayout();
    }
  });
  animator.start();
}
```

The `translationY` local variable changes from `ViewHelper.getTranslationY(mToolbar)`( == current translationY)
to `toTranslationY`.

To translate the Toolbar, we just call `ViewHelper.setTranslationY()`.  
And to change the height of the wrapper view (`FrameLayout`), set the height value of `FrameLayout.LayoutParams`
and update by calling `requestLayout()`.

## Avoid redundant animation

We'd better check the current `translationY` value
and if it's already equal to `toTranslationY`, stop the animation.

```java
private void moveToolbar(float toTranslationY) {
  // Check the current translationY
  if (ViewHelper.getTranslationY(mToolbar) == toTranslationY) {
    return;
  }
  // Codes after that are omitted
}
```

That's all.

[Next: Parallax image &raquo;](../../docs/basic/parallax-image.md)
