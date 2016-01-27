# Animation codes

This time, we implement ActionBar animation using `AppCompatActivity` in the support library.

## Apply layout to the activity

At first, let `Activity` extend the `AppCompatActivity` and set [the layout we wrote](../../docs/quick-start/layout.md) to it.

```java
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
```

## Initialize ObservableListView

Add some initialization codes to `onCreate()`:

```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    ObservableListView listView = (ObservableListView) findViewById(R.id.list);
    listView.setScrollViewCallbacks(this);
  }
```

You will see an error around `setScrollViewCallbacks(this)` because the Activity does not implement the required interface yet.
So add `implements ObservableScrollViewCallbacks` to the Activity definition:

```java
public class MainActivity extends AppCompatActivity
  implements ObservableScrollViewCallbacks {
```

Then implement required methods:

```java
  @Override
  public void onScrollChanged(int scrollY, boolean firstScroll,
    boolean dragging) {
  }

  @Override
  public void onDownMotionEvent() {
  }

  @Override
  public void onUpOrCancelMotionEvent(ScrollState scrollState) {
  }
}
```

Now we can handle the scroll events.

## Populate list data

Before write codes to animate views, set data to ListView.

```java
    // Add these codes after ListView initialization
    ArrayList<String> items = new ArrayList<String>();
    for (int i = 1; i <= 100; i++) {
      items.add("Item " + i);
    }
    listView.setAdapter(new ArrayAdapter<String>(
      this, android.R.layout.simple_list_item_1, items));
```

## Animate with scroll events

Finally, we can write the main code now.
Add some code to show/hide the ActionBar in `onUpOrCancelMotionEvent` method for example.

```java
  @Override
  public void onUpOrCancelMotionEvent(ScrollState scrollState) {
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
  }
```

`ScrollState` parameter indicates the direction of swiping, and this event will occur when you touch up (or cancel) the ListView.
This is just an introduction so we don't use other events like `onScrollChanged`.

Now let's build and launch the app.

You can see the ActionBar gets hidden or shown when you swipe the ListView.

As you can see, the most important codes are the animation codes in the callbacks.
You can learn how to write these code in this tutorial.

In the [next section](../../docs/example/index.md), we'll check the existing examples to see what we can do with this library.

## Program list

Following codes are the entire Activity, just for your reference.

```java
import android.support.v7.app.AppCompatActivity;
// other imports and package statement are omitted

public class MainActivity extends AppCompatActivity
  implements ObservableScrollViewCallbacks {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ObservableListView listView = (ObservableListView) findViewById(R.id.list);
    listView.setScrollViewCallbacks(this);

    // TODO These are dummy. Populate your data here.
    ArrayList<String> items = new ArrayList<String>();
    for (int i = 1; i <= 100; i++) {
      items.add("Item " + i);
    }
    listView.setAdapter(new ArrayAdapter<String>(
      this, android.R.layout.simple_list_item_1, items));
  }

  @Override
  public void onScrollChanged(int scrollY, boolean firstScroll,
    boolean dragging) {
  }

  @Override
  public void onDownMotionEvent() {
  }

  @Override
  public void onUpOrCancelMotionEvent(ScrollState scrollState) {
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
  }
}
```

[Next: Try the example app &raquo;](../../docs/example/index.md)
