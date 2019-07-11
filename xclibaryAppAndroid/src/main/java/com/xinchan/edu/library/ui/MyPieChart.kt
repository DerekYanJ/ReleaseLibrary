package com.xinchan.edu.library.ui

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.utils.MPPointF

/**
 * @author derekyan
 * @desc
 * @date 2018/8/10
 */
class MyPieChart : PieChart {

    /**
     * rect object that represents the bounds of the piechart, needed for
     * drawing the circle
     */
    private val mCircleBox = RectF()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun getRequiredLegendOffset(): Float {
        return mLegendRenderer.labelPaint.textSize
    }


    override fun calculateOffsets() {
        super.calculateOffsets()

        // prevent nullpointer when no data set
        if (mData == null)
            return

        val diameter = diameter
        val radius = diameter / 2f

        val c = centerOffsets

        // create the circle box that will contain the pie-chart (the bounds of
        // the pie-chart)
        mCircleBox.set(0f,
                c.y - radius,
                radius * 2,
                c.y + radius)

        MPPointF.recycleInstance(c)
    }

    override fun getRadius(): Float {
        return if (mCircleBox == null)
            0f
        else
            Math.min(mCircleBox.width() / 2f, mCircleBox.height() / 2f)
    }

    /**
     * returns the circlebox, the boundingbox of the pie-chart slices
     *
     * @return
     */
    override fun getCircleBox(): RectF {
        return mCircleBox
    }

    /**
     * returns the center of the circlebox
     *
     * @return
     */
    override fun getCenterCircleBox(): MPPointF {
        return MPPointF.getInstance(mCircleBox.centerX(), mCircleBox.centerY())
    }
}
