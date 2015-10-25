/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ksoichiro.android.observablescrollview;

/**
 * Callbacks for Scrollable widgets.
 */
public interface ObservableScrollViewCallbacks {
    /**
     * Called when the scroll change events occurred.
     * <p>This won't be called just after the view is laid out, so if you'd like to
     * initialize the position of your views with this method, you should call this manually
     * or invoke scroll as appropriate.</p>
     *
     * @param scrollY     Scroll position in Y axis.
     * @param firstScroll True when this is called for the first time in the consecutive motion events.
     * @param dragging    True when the view is dragged and false when the view is scrolled in the inertia.
     */
    void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging);

    /**
     * Called when the down motion event occurred.
     */
    void onDownMotionEvent();

    /**
     * Called when the dragging ended or canceled.
     *
     * @param scrollState State to indicate the scroll direction.
     */
    void onUpOrCancelMotionEvent(ScrollState scrollState);
}
