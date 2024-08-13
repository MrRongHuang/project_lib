package com.koi.highlightpro.parameter

import android.graphics.RectF
import android.view.View
import android.view.animation.Animation
import com.koi.highlightpro.shape.HighlightShape

/**
 * Create by hyy on 2021/2/6
 *
 * [HighlightParameter] is a build mode for user to set
 * parameters to build the [GuideView]
 */
class HighlightParameter {


    internal var nextTipsViewId: Int = -1

    internal var nextTipsView: View? = null

    internal var highLightViewId: Int = -1

    internal var highLightView: View? = null

    internal var tipsViewId: Int = -1

    internal var tipsView: View? = null

    internal var highlightShape: HighlightShape? = null

    internal var rect: RectF = RectF()

    internal var horizontalPadding: Float = 0f

    internal var verticalPadding: Float = 0f

    internal var topPadding: Float = 0f

    internal var bottomPadding: Float = 0f

    internal var leftPadding: Float = 0f

    internal var rightPadding: Float = 0f

    internal var marginOffset: MarginOffset = MarginOffset()

    internal var offsetX = 0
    internal var offsetY = 0

    internal var setOnlyButtonClick = false

    internal val constraints =
        mutableListOf(Constraints.TopToBottomOfHighlight, Constraints.StartToStartOfHighlight)

    internal var tipViewDisplayAnimation: Animation? = null

    fun setHighLightViewId(viewId: Int) {
        this.highLightViewId = viewId
    }

    fun setHighLightView(highLightView: View) {
        this.highLightView = highLightView
    }

    fun setNextTipsViewId(viewId: Int) {
        this.nextTipsViewId = viewId
    }

    fun setNextTipsView(nextTipsView: View) {
        this.nextTipsView = nextTipsView
    }


    operator fun plus(highlightParameter: HighlightParameter): List<HighlightParameter> {
        return listOf(this, highlightParameter)
    }

    class Builder {
        private val highlightParameter: HighlightParameter = HighlightParameter()

        /**
         * [viewId] is the highLightView's id
         * it will be override by [setHighlightView].
         */
        fun setHighlightViewId(viewId: Int): Builder {
            highlightParameter.highLightViewId = viewId
            return this
        }

        /**
         * [highLightView] is the view which you want to make it highLight
         * And it will make [setHighlightViewId] useless.
         */
        fun setHighlightView(highLightView: View): Builder {
            highlightParameter.highLightView = highLightView
            return this
        }

        /**
         * [viewId] is the nextTipsView's id
         * it will be override by [setNextTipsView].
         */
        fun setNextTipsViewId(viewId: Int): Builder {
            highlightParameter.nextTipsViewId = viewId
            return this
        }

        /**
         * [nextTipsView] is the view which you want to make it highLight
         * And it will make [setNextTipsViewId] useless.
         */
        fun setNextTipsView(nextTipsView: View): Builder {
            highlightParameter.nextTipsView = nextTipsView
            return this
        }

        /**
         * [viewId] is the tipsView's id
         * it will be override by [setTipsView].
         */
        fun setTipsViewId(viewId: Int): Builder {
            highlightParameter.tipsViewId = viewId
            return this
        }

        /**
         * [tipsView] is the view which you want to add it on [GuideViewContainer]
         * And it will make [setTipsViewId] useless.
         */
        fun setTipsView(tipsView: View): Builder {
            highlightParameter.tipsView = tipsView
            return this
        }

        /**
         * [highlightShape] is the highlight out rect shape and you can
         * custom your shape reference our shapes exit.
         */
        fun setHighlightShape(highlightShape: HighlightShape): Builder {
            highlightParameter.highlightShape = highlightShape
            return this
        }

        /**
         * [padding] is the vertical dimension padding use by expand the rect area of
         * highlight.
         */
        fun setHighlightVerticalPadding(padding: Float): Builder {
            highlightParameter.verticalPadding = padding
            return this
        }

        /**
         * [topPadding] is the top dimension padding use by expand the rect area of
         * highlight.
         */
        fun setHighlightTopPadding(topPadding: Float): Builder {
            highlightParameter.topPadding = topPadding
            return this
        }

        /**
         * [bottomPadding] is the bottom dimension padding use by expand the rect area of
         * highlight.
         */
        fun setHighlightBottomPadding(bottomPadding: Float): Builder {
            highlightParameter.bottomPadding = bottomPadding
            return this
        }

        /**
         * [leftPadding] is the left dimension padding use by expand the rect area of
         * highlight.
         */
        fun setHighlightLeftPadding(leftPadding: Float): Builder {
            highlightParameter.leftPadding = leftPadding
            return this
        }

        /**
         * [rightPadding] is the right dimension padding use by expand the rect area of
         * highlight.
         */
        fun setHighlightRightPadding(rightPadding: Float): Builder {
            highlightParameter.rightPadding = rightPadding
            return this
        }

        /**
         * [marginOffset] is the extra dimension use by position highlight rect.
         */
        fun setMarginOffset(marginOffset: MarginOffset): Builder {
            highlightParameter.marginOffset = marginOffset
            return this
        }

        /**
         * [constraints] is a list of constraints provides some constraints of highlight
         * rect position.
         */
        fun setConstraints(constraints: List<Constraints>): Builder {
            highlightParameter.constraints.clear()
            highlightParameter.constraints.addAll(constraints)
            return this
        }

        /**
         * [padding] is the horizontal dimension padding use by expand the rect area of
         * highlight.
         */
        fun setHighlightHorizontalPadding(padding: Float): Builder {
            highlightParameter.horizontalPadding = padding
            return this
        }

        /**
         * [tipViewDisplayAnimation] is the animation to display for Highlight tips view
         */
        fun setTipViewDisplayAnimation(tipViewDisplayAnimation: Animation?): Builder {
            highlightParameter.tipViewDisplayAnimation = tipViewDisplayAnimation
            return this
        }

        fun offsetX(offsetX: Int): Builder {
            highlightParameter.offsetX = offsetX
            return this
        }

        fun offsetY(offsetY: Int): Builder {
            highlightParameter.offsetY = offsetY
            return this
        }

        /**
         * [onlyButtonClick]
         *
         */
        fun setOnlyButtonClick(onlyButtonClick: Boolean): Builder {
            highlightParameter.setOnlyButtonClick = onlyButtonClick
            return this
        }

        fun build(): HighlightParameter = highlightParameter
    }
}