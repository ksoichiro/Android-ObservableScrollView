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

package com.github.ksoichiro.android.observablescrollview.samples;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * A layout that delegates interception of touch motion events.
 */
public class TouchInterceptionFrameLayout extends FrameLayout {

    /**
     * Callbacks for TouchInterceptionFrameLayout.
     */
    public interface TouchInterceptionListener {
        /**
         * Determines whether the layout should intercept this event.
         *
         * @param ev     motion event
         * @param moving true if this event is ACTION_MOVE type
         * @param diffY  difference between previous Y and current Y, if moving is true
         * @return true if the layout should intercept
         */
        boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffY);

        /**
         * Called if the down motion event is intercepted by this layout.
         *
         * @param ev motion event
         */
        void onDownMotionEvent(MotionEvent ev);

        /**
         * Called if the move motion event is intercepted by this layout.
         *
         * @param ev    motion event
         * @param diffY difference between previous Y and current Y
         */
        void onMoveMotionEvent(MotionEvent ev, float diffY);

        /**
         * Called if the up (or cancel) motion event is intercepted by this layout.
         *
         * @param ev motion event
         */
        void onUpOrCancelMotionEvent(MotionEvent ev);
    }

    private boolean mIntercepting;
    private boolean mDownMotionEventPended;
    private boolean mBeganFromDownMotionEvent;
    private float mInitialY;
    private MotionEvent mPendingDownMotionEvent;
    private TouchInterceptionListener mTouchInterceptionListener;

    public TouchInterceptionFrameLayout(Context context) {
        super(context);
    }

    public TouchInterceptionFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchInterceptionFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TouchInterceptionFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollInterceptionListener(TouchInterceptionListener listener) {
        mTouchInterceptionListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mTouchInterceptionListener == null) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mInitialY = ev.getY();
                mPendingDownMotionEvent = MotionEvent.obtainNoHistory(ev);
                mBeganFromDownMotionEvent = true;
                mDownMotionEventPended = true;
                mIntercepting = mTouchInterceptionListener.shouldInterceptTouchEvent(ev, false, 0);
                return mIntercepting;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mTouchInterceptionListener != null) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchInterceptionListener.onDownMotionEvent(ev);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float diffY = ev.getY() - mInitialY;
                    mIntercepting = mTouchInterceptionListener.shouldInterceptTouchEvent(ev, true, diffY);
                    if (mIntercepting) {
                        if (!mBeganFromDownMotionEvent) {
                            mBeganFromDownMotionEvent = true;

                            // Layout didn't receive ACTION_DOWN motion event,
                            // so generate down motion event with current position.
                            MotionEvent event = MotionEvent.obtainNoHistory(mPendingDownMotionEvent);
                            event.setLocation(ev.getX(), ev.getY());
                            mInitialY = ev.getY();
                            diffY = 0;
                            mTouchInterceptionListener.onDownMotionEvent(event);
                        }
                        mTouchInterceptionListener.onMoveMotionEvent(ev, diffY);

                        // If next mIntercepting become false,
                        // then we should generate fake ACTION_DOWN event.
                        // Therefore we set pending flag to true as if this is a down motion event.
                        mDownMotionEventPended = true;
                    } else {
                        final boolean downMotionEventPended = mDownMotionEventPended;
                        if (mDownMotionEventPended) {
                            mDownMotionEventPended = false;
                        }

                        // We want to dispatch a down motion event and this ev event to
                        // child views, but calling dispatchTouchEvent() causes StackOverflowError.
                        // Therefore we do it manually.
                        for (int i = getChildCount() - 1; 0 <= i; i--) {
                            View childView = getChildAt(i);
                            if (childView != null) {
                                Rect childRect = new Rect();
                                childView.getHitRect(childRect);
                                if (!childRect.contains((int) ev.getX(), (int) ev.getY())) {
                                    continue;
                                }
                                boolean consumed = false;
                                if (downMotionEventPended) {
                                    // Update location to prevent the point jumping
                                    consumed = childView.onTouchEvent(mPendingDownMotionEvent);
                                }
                                consumed |= childView.onTouchEvent(ev);
                                if (consumed) {
                                    break;
                                }
                            }
                        }

                        // If next mIntercepting become true,
                        // then we should generate fake ACTION_DOWN event.
                        // Therefore we set beganFromDownMotionEvent flag to false
                        // as if we haven't received a down motion event.
                        mBeganFromDownMotionEvent = false;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mBeganFromDownMotionEvent = false;
                    if (mIntercepting) {
                        mTouchInterceptionListener.onUpOrCancelMotionEvent(ev);
                    } else {
                        for (int i = 0; i < getChildCount(); i++) {
                            View childView = getChildAt(i);
                            if (childView != null) {
                                if (mDownMotionEventPended) {
                                    mDownMotionEventPended = false;
                                    childView.onTouchEvent(mPendingDownMotionEvent);
                                }
                                childView.onTouchEvent(ev);
                            }
                        }
                    }
                    return true;
            }
        }
        return super.onTouchEvent(ev);
    }
}
