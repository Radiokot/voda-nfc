package ua.com.radiokot.voda.extensions

import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * Does smooth scroll at moderate speed, relies on reflection.
 */
fun ViewPager2.moderateSmoothScrollToPosition(position: Int) {
    val accelerateInterpolator = AccelerateDecelerateInterpolator()
    val linearSmoothScroller: LinearSmoothScroller =
        object : LinearSmoothScroller(context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 60f / displayMetrics.densityDpi
            }

            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                super.onTargetFound(targetView, state, action)
                if (action.duration > 0) {
                    action.interpolator = accelerateInterpolator
                }
            }
        }

    linearSmoothScroller.targetPosition = position

    also { pager ->
        pager::class.java.getDeclaredField("mLayoutManager").apply {
            isAccessible = true
            (get(pager) as RecyclerView.LayoutManager).startSmoothScroll(linearSmoothScroller)
            isAccessible = false
        }
    }
}