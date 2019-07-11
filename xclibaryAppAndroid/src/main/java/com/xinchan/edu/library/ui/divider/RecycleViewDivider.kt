package com.xinchan.edu.library.ui.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.dip

/**
 * @author derekyan
 * @desc
 * @date 2018/4/26
 */
class RecycleViewDivider
/**
 * 构造方法传入布局方向，不可不传
 *
 * @param context
 * @param orientation
 */
(context: Context, orientation: Int = RecyclerView.VERTICAL, itemSize: Int = 1) : RecyclerView.ItemDecoration() {
    /**
     * RecyclerView的布局方向，默认先赋值
     * 为纵向布局
     * RecyclerView 布局可横向，也可纵向
     * 横向和纵向对应的分割想画法不一样
     */
    private var mOrientation = RecyclerView.VERTICAL

    /**
     * item之间分割线的size，默认为1
     */
    private var mItemSize = 1

    /**
     * 绘制item分割线的画笔，和设置其属性
     * 来绘制个性分割线
     */
    private val mPaint: Paint

    init {
        //样式的方向
        this.mOrientation = orientation
        if (orientation != RecyclerView.VERTICAL && orientation != RecyclerView.HORIZONTAL) {
            throw IllegalArgumentException("请传入正确的参数")
        }
        //转换
        mItemSize = context.dip(itemSize)
//        mItemSize = TypedValue.applyDimension(itemSize, TypedValue.COMPLEX_UNIT_DIP.toFloat(), context.resources.displayMetrics).toInt()
        //创建画笔
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画笔颜色
        mPaint.color = Color.parseColor("#F8F8F8")

        /**
         * 设置画笔模式
         */
        mPaint.style = Paint.Style.FILL
    }

    //绘制

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == RecyclerView.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    /**
     * 绘制纵向 item 分割线
     * @param canvas
     * @param parent
     */
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + mItemSize
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

    /**
     * 绘制横向 item 分割线
     * @param canvas
     * @param parent
     */
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.measuredHeight - parent.paddingBottom
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + layoutParams.rightMargin
            val right = left + mItemSize
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }


    //设置大小

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == RecyclerView.VERTICAL) {
            outRect.set(0, 0, 0, mItemSize)
        } else {
            outRect.set(0, 0, mItemSize, 0)
        }
    }
}
