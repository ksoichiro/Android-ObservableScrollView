# Release notes

* v1.6.0
    * Added header view feature to `ObservableGridView` (#148).
    * Added footer view feature to `ObservableGridView` (#183).
    * Updated `recyclerview-v7` library version to 22.2.0.
    * Fixed ViewPager swiping bug in `ObservableListView` (#185).
    * Fixed NPE in `ObservableRecyclerView` (#149).
* v1.5.2
    * Fix `ObservableGridView` to use first child of line in height calculation.
* v1.5.1
    * Fix `scrollY` of `onScrollChanged` in `ObservableGridView` jumps
      when the first visible item changes.
* v1.5.0
    * Add a helper class `CacheFragmentStatePagerAdapter` to implement ViewPager pattern.
    * Fix that swipe down (over-scroll) causes item click.
* v1.4.0
    * Add a custom view named `TouchInterceptionFrameLayout` and a new API `setTouchInterceptionViewGroup()` for `Scrollable`.  
      With these class and API, you can move `Scrollable` itself using its scrolling events.
    * Add a helper class `ScrollUtils` for implementing scrolling effects.
* v1.3.2
    * Fix that `ObservableRecyclerView` causes `BadParcelableException` on `onRestoreInstanceState`.
* v1.3.1
    * Fix that `onDownMotionEvent` not called and parameters of `onScrollChanged` are incorrect
      when children views handle touch events.
* v1.3.0
    * Add new interface `Scrollable` to provide common API for scrollable widgets. 
* v1.2.1
    * Fix that the scroll states and other internal information are lost after `onSaveInstanceState()`.
    * Fix that the scrollY is incorrect if the ListView/RecyclerView don't scroll from the top.
      (It's just approximating the scroll offset and not the complete solution but better than before.)
* v1.2.0
    * Add GridView support.
    * Fix ObservableListView cannot detect onScrollChanged on Android 2.3.
    * Fix ObservableScrollView cannot detect UP and DOWN state in onUpOrCancelMotionEvent before Android 4.4.
* v1.1.0
    * Add RecyclerView support.
* v1.0.0
    * Initial release.
