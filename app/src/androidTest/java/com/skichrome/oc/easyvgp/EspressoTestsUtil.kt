package com.skichrome.oc.easyvgp

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import com.skichrome.oc.easyvgp.util.getColorCompat
import com.skichrome.oc.easyvgp.util.getDrawableCompat
import org.hamcrest.Description
import org.hamcrest.Matcher


fun recyclerViewHasItem(@NonNull matcher: Matcher<View>): Matcher<View> = object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java)
{
    override fun describeTo(description: Description)
    {
        description.appendText("RecyclerView has item $description")
        matcher.describeTo(description)
    }

    override fun matchesSafely(view: RecyclerView): Boolean
    {
        val adapter = view.adapter

        for (position in 0 until adapter!!.itemCount)
        {
            val viewType = adapter.getItemViewType(position)
            val holder = adapter.createViewHolder(view, viewType)
            adapter.onBindViewHolder(holder, position)

            if (matcher.matches(holder.itemView))
                return true
        }
        return false
    }
}

fun atRecyclerViewPosition(position: Int, @NonNull itemMatcher: Matcher<View>, @NonNull targetViewId: Int): Matcher<View> =
    object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java)
    {
        override fun describeTo(description: Description)
        {
            description.appendText("has item at position $position and view id $targetViewId ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: RecyclerView): Boolean
        {
            val targetView = view.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<View>(targetViewId)
                ?: return false // has no item on such position
            return itemMatcher.matches(targetView)
        }
    }

fun hasColor(@ColorRes color: Int): Matcher<View> = object : BoundedMatcher<View, View>(View::class.java)
{
    private var expectedColorResource: Drawable.ConstantState? = null
    private var actualColorResource: Drawable.ConstantState? = null

    override fun describeTo(description: Description)
    {
        description.appendText("View background color ID expected is $color")
            .appendText("Actual constant state is $actualColorResource and expected is $expectedColorResource")
    }

    override fun matchesSafely(view: View): Boolean
    {
        expectedColorResource = view.getDrawableCompat(color)?.constantState
        actualColorResource = view.background.constantState

        return expectedColorResource == actualColorResource
    }
}

fun hasDrawable(@DrawableRes drawable: Int, @ColorRes tint: Int, tintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN): Matcher<View> =
    object : BoundedMatcher<View, ImageView>(ImageView::class.java)
    {
        private var expectedDrawableResource: VectorDrawable? = null
        private var actualDrawableResource: VectorDrawable? = null

        override fun describeTo(description: Description)
        {
            description.appendText("Drawable ID expected is $drawable and tint is $tint\n")
                .appendText("Actual constant state is $actualDrawableResource and expected is $expectedDrawableResource")
        }

        override fun matchesSafely(view: ImageView): Boolean
        {
            expectedDrawableResource = view.getDrawableCompat(drawable) as? VectorDrawable ?: return false
            actualDrawableResource = view.drawable as? VectorDrawable ?: return false

            val tintColor = view.getColorCompat(tint)

            val expectedBitmap = expectedDrawableResource?.tinted(tintColor, tintMode)?.toBitmap()
            val actualBitmap = actualDrawableResource?.toBitmap()

            return expectedBitmap?.sameAs(actualBitmap) ?: false
        }
    }

private fun Drawable.tinted(@ColorInt tintColor: Int? = null, tintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN) =
    apply {
        setTintList(tintColor?.toColorStateList())
        setTintMode(tintMode)
    }

private fun Int.toColorStateList() = ColorStateList.valueOf(this)