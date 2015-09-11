# Flexible space with image

This topic describes how to create flexible space with image,
which are implemented in the following examples.

* FlexibleSpaceWithImageListViewActivity
* FlexibleSpaceWithImageRecyclerViewActivity
* FlexibleSpaceWithImageScrollViewActivity

First, please check the "[Flexible space on the Toolbar](../../docs/basic/flexible-space-toolbar.md)"
tutorial, if you haven't.  

---

## Using ScrollView

### Layout with ScrollView

#### Basic structure

```xml
<FrameLayout>
    <ImageView android:id="@+id/image"/>
    <View android:id="@+id/overlay"/>
    <ObservableScrollView android:id="@+id/scroll">
        <LinearLayout android:orientation="vertical">
            <View/>
            <TextView/>
        </LinearLayout>
    </ObservableScrollView>
    <LinearLayout android:orientation="vertical">
        <TextView android:id="@+id/title"/>
        <View/>
    </LinearLayout>
    <FloatingActionButton android:id="@+id/fab"/>
</FrameLayout>
```

The root `FrameLayout` is used for moving children separately.

`ImageView`(`@id/image`) is the image that will be translated with parallax effect.

`View`(`@id/overlay`) is a overlay view as the name suggests.  
If you try this Activity in the demo app, you can see the image is fading in and out.
This view overlaps with the image and its opacity is changed by scroll position.

`LinearLayout` and its chlidren are the real title views.  
You would have read the former tutorial, so I will not explain it so much.

`FloatingActionButton` is a widget from the simple and awesome [FloatingActionButton](https://github.com/makovkastar/FloatingActionButton) library.  
But this is optional, so you can remove it if you are not going to place any buttons.
I added it just because I think it's a very symbolic widget of the Material Design
and some of you might be interested in it.

To confirm other attributes,
please see `res/layout/activity_flexiblespacewithimagescrollview.xml` in the example app.

### Initialization

Most of the codes are easy and not related to this pattern.  
Just write the following initialization codes:

Copy the title to the title view (`TextView`) and set null to the original title:

```java
mTitleView.setText(getTitle());
setTitle(null);
```

Get the dimension values and save them to fields (to simplify animation codes):

```Java
mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
mActionBarSize = getActionBarSize();
```

Get the views which has ID to fields (to simplify animation codes), and initialize them if necessary:

```java
mImageView = findViewById(R.id.image);
mOverlayView = findViewById(R.id.overlay);
mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
mScrollView.setScrollViewCallbacks(this);
mTitleView = (TextView) findViewById(R.id.title);
mFab = findViewById(R.id.fab);
```

Although this is not so related to the scroll animation,
you should scale the floating action button (FAB) to 0 in `onCreate()`,
because we'd like to hide it at the beginning and gradually show (scale) it
by scrolling.

```java
ViewHelper.setScaleX(mFab, 0);
ViewHelper.setScaleY(mFab, 0);
```

You should also add `implements ObservableScrollViewCallbacks` to the Activity
and implement those methods as always.

### Animation

We use `onScrollChanged()` to create animation.  
We'll write the following codes:

* Translate the overlay view and the image view
* Change the alpha of the overlay view
* Translate and scale the title view
* Translate the FAB
* Show/hide the FAB

Let's see one by one.

#### Translate the overlay view and the image view

As we implemented in the former tutorials,
to move `ImageView` which is outside the ScrollView,
we must use `-scrollY` and divide it by 2 to create "parallax" effect.

```java
@Override
public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
  float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
  int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
  ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
  ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
```

Although we want to move the overlay view with the image,
we don't have to make the scroll speed of the overlay to the same as the image view.
So translate `mOverlayView` to `-scrollY` (not `-scrollY / 2`).

#### Change the alpha of the overlay view

Calculating the alpha value is easy, just convert the `scrollY` to range between 0 and 1.
To do this, we divide `scrollY` by `flexibleRange` (which we assigned above),
and limit the value range from 0 to 1 by using `ScrollUtils.getFloat()`.

```java
  ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));
```

#### Translate and scale the title view

This is almost the same as the "Flexible space on the Toolbar" pattern.
The differences are how to calculate the scale and the translationY.

```java
@Override
public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
  // Codes that are already explained above are omitted

  float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
  ViewHelper.setPivotX(mTitleView, 0);
  ViewHelper.setPivotY(mTitleView, 0);
  ViewHelper.setScaleX(mTitleView, scale);
  ViewHelper.setScaleY(mTitleView, scale);

  int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
  int titleTranslationY = maxTitleTranslationY - scrollY;
  ViewHelper.setTranslationY(mTitleView, titleTranslationY);
```

#### Translate the FAB

Translating the FAB is actually not related to this topic, but I'll explain for your reference.

The basic idea is to change the `translationY` property of the FAB,
but on pre-Honeycomb devices, this doesn't work when you use `setOnClickListener`.
To fix the issue, we'll set the margins of the FrameLayout and lay it out again by calling `requestLayout()`.

```java
@Override
public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
  // Codes that are already explained above are omitted

  int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
  float fabTranslationY = ScrollUtils.getFloat(
      -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
      mActionBarSize - mFab.getHeight() / 2,
      maxFabTranslationY);
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
    // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
    // which causes FAB's OnClickListener not working.
    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
    lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
    lp.topMargin = (int) fabTranslationY;
    mFab.requestLayout();
  } else {
    ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
    ViewHelper.setTranslationY(mFab, fabTranslationY);
  }
```

The expression `- mFab.getHeight() / 2` in the calculation of `maxFabTranslationY` means
that the half of the FAB overlaps to the image.

And about the `fabTranslationY` calculation,
you might think that the expression `mActionBarSize - mFab.getHeight() / 2` for the min value
is meaningless, but this is required when you scroll the view fast.
Because if it scrolls faster than the FAB scaling to 0, it looks as if it just moved away.

#### Show/hide the FAB

Showing or hiding the FAB is easy.
If the translationY of the FAB exceeds the threshold, then hide it.
Otherwise, show it.

```java
@Override
public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
  // Codes that are already explained above are omitted

  if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
    hideFab();
  } else {
    showFab();
  }
}
```

`hideFab()` and `showFab()` methods can be implemented like this:

```java
  private boolean mFabIsShown;

  private void showFab() {
    if (!mFabIsShown) {
      ViewPropertyAnimator.animate(mFab).cancel();
      ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
      mFabIsShown = true;
    }
  }

  private void hideFab() {
    if (mFabIsShown) {
      ViewPropertyAnimator.animate(mFab).cancel();
      ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
      mFabIsShown = false;
    }
  }
```

We need a state variable to indicate whether the FAB is shown or not.

That's all.

[Next: Filling gap on top of the Toolbar &raquo;](../../docs/basic/filling-gap.md)
