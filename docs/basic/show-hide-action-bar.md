# Show and hide the ActionBar

This topic describes how to show and hide the ActionBar,
which are implemented in the following examples.

* ActionBarControlGridViewActivity
* ActionBarControlListViewActivity
* ActionBarControlRecyclerViewActivity
* ActionBarControlScrollViewActivity
* ActionBarControlWebViewActivity

---

## Using the basic callbacks

Suppose you've already checked the "[Quick start](../../docs/quick-start/index.md)" section,
you wouldn't know the meaning of the codes yet.  
So at first, let's see how those codes work.

### ObservableScrollViewCallbacks

In the quick start guide, you wrote the implementation of `ObservableScrollViewCallbacks` (following methods).

```java
@Override
public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
}
@Override
public void onDownMotionEvent() {
}
@Override
public void onUpOrCancelMotionEvent(ScrollState scrollState) {
}
```

These are the methods of `ObservableScrollViewCallbacks` interface and all the `Observable*View`s can handle this callbacks.

### onScrollChanged callback

This is called when the scroll change events occurred.  
This won't be called just after the view is laid out, so if you'd like to initialize the position of your views with this method, you should call this manually or invoke scroll as appropriate.

You would expect it to be called after `onCreate`,
but `ListView` (or other views) does not call back
scroll change event, so `Observable*Views` cannot
call this.
Therefore you should write like this:

```java
onScrollChanged(mListView.getCurrentScrollY(), false, false);
```

I know it's a bad pattern to call the "callback" methods from us, they should be called by the library when they should be. However, we cannot improve this because this behavior depends on the view classes in the Android SDK.

### onDownMotionEvent callback

This is called when the down motion events occur.  
This can be useful if you'd like to know when the touch (or dragging) has begun.

### onUpOrCancelMotionEvent callback

This is called when the dragging ended or canceled.
This is useful when you move some views when the scroll ends: showing/hiding a view, sliding a view to the anchor point, etc.

## How it works: ActionBar animation

As I explained in the quick start section, the main animation code is in the `onUpOrCancelMotionEvent`.

What we want to do is:

1. to hide the ActionBar when we swipe up the view, because we want to see the contents.
1. to show the ActionBar when we swipe down the view, because we want to tap a button on the ActionBar (it could be sharing the contents or going back to the former screen, for example).

Either way, we should get the direction of scrolling when the dragging ends.
`onUpOrCancelMotionEvent` callback has a `ScrollState` parameter. This parameter indicates the direction of the scroll, so we can write like this:

```java
public void onUpOrCancelMotionEvent(ScrollState scrollState) {
  if (scrollState == ScrollState.UP) {
    // TODO show or hide the ActionBar
  } else if (scrollState == ScrollState.DOWN) {
    // TODO show or hide the ActionBar
  }
}
```

When you move your finger from the bottom of the screen to the top (swiping up), the state will be `ScrollState.UP`. So we can write `ActionBar#hide()` in this condition.
And this event occurs every time you scroll the view, so if the `ActionBar` is already hidden, you don't have to hide it anymore.

Now you know how to handle the other direction.
Show the ActionBar when `ScrollState.DOWN` is passed to the callback.

```java
ActionBar ab = getSupportActionBar();
if (scrollState == ScrollState.UP) {
  if (ab.isShowing()) {
    ab.hide();
  }
} else if (scrollState == ScrollState.DOWN) {
  if (!ab.isShowing()) {
    ab.show();
  }
}
```

## Conclusion

If you'd like to animate the ActionBar, you should write animation codes in the `onUpOrCancelMotionEvent` callback.
Also, we used `ObservableListView` in the quick start, but in this pattern all types of `Observable*View`s have the same behavior, so you can write exactly the same.

[Next: Translating the Toolbar &raquo;](../../docs/basic/translating-toolbar.md)
