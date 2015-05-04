# Supported widgets

Widgets are named with `Observable` prefix.  
(e.g. `ListView` → `ObservableListView`)  
You can handle these widgets with `Scrollable` interface.

| Widget | Since | Note |
|:------:|:-----:| ---- |
| ListView | v1.0.0 | - |
| ScrollView | v1.0.0 | - |
| WebView | v1.0.0 | - |
| RecyclerView | v1.1.0 | It's supported but RecyclerView provides scroll states and position with [OnScrollListener](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.OnScrollListener.html). You should use it if you don't have any reason. |
| GridView | v1.2.0 | - |
