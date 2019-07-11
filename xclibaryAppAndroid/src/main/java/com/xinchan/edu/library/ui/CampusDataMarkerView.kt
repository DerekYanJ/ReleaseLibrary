package com.xinchan.edu.library.ui

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.layout_campus_data_marker_view.view.*

@SuppressLint("ViewConstructor")
/**
 * @desc
 * @author derekyan
 * @date 2018/8/9
 */
class CampusDataMarkerView(context: Context, layoutResource: Int): MarkerView(context, layoutResource) {

    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry, highlight: Highlight) {
        if (e is CandleEntry) {
            tv_content.text = "" + Utils.formatNumber(e.high, 0, true) + "%"
        } else {
            tv_content.text = "" + Utils.formatNumber(e.y, 0, true) + "%"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return  MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}