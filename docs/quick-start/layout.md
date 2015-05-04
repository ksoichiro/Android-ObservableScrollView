# Layout

After adding the dependency, let's write layout file such as `res/layout/activity_main.xml`.

This time, we'll use only one element `ObservableListView`.

```xml
<com.github.ksoichiro.android.observablescrollview.ObservableListView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/list"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Android-ObservableScrollView provides several types of views that are scroll-able such as `ObservableScrollView`, `ObservableGridView`, etc.  
And they extend the standard widget (`ScrollView`, `GridView`, ...), and they provide some callbacks to get scroll events.

After writing the above layout, you can write animation codes using these callbacks.

[Next: Animation codes &raquo;](../../docs/quick-start/animation.md)
