package com.xinchan.edu.library.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.xinchan.edu.library.R
import org.jetbrains.anko.dip

/**
 * Created by weicxu on 2017/12/14
 *
 *
 * 2018.3.26 update . add a state of only image
 */
class BottomRadioView : View {

    private lateinit var text: String
    private lateinit var defaultDrawable: Drawable
    private lateinit var selectDrawable: Drawable
    private var itemSelected = false

    private lateinit var mPaint: Paint
    private lateinit var mCirclePaint: Paint
    private lateinit var mTextPaint: Paint

    private lateinit var mMatrix: Matrix

    private var colorSelect: Int = 0x666666
    private var colorDefault: Int = 0x666666

    private var mTop: Float = 6f

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomRadioView)

        (0..array.indexCount).forEach { i ->
            val attr = array.getIndex(i)
            when (attr) {
                R.styleable.BottomRadioView_text -> text = array.getString(attr)
                R.styleable.BottomRadioView_defaultDrawable -> defaultDrawable = array.getDrawable(attr)
                R.styleable.BottomRadioView_selectDrawable -> selectDrawable = array.getDrawable(attr)
                R.styleable.BottomRadioView_itemSelected -> itemSelected = array.getBoolean(attr, false)
                R.styleable.BottomRadioView_colorSelect -> colorSelect = array.getColor(attr, colorDefault)
                R.styleable.BottomRadioView_colorDefault -> colorDefault = array.getColor(attr, colorDefault)
                else -> {

                }
            }
        }
        array.recycle()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.YELLOW
        mPaint.strokeWidth = (1f)

        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint.style = Paint.Style.FILL
        mCirclePaint.strokeWidth = 1f


        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.color = Color.TRANSPARENT
        mTextPaint.strokeWidth = (2f)
        mTextPaint.textSize = dip(12f).toFloat()
        mMatrix = Matrix()

        mTop = dip(6f).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        if (text.isEmpty()){
//            setMeasuredDimension(50,50)
//        }


    }

    fun setItemSelected(b: Boolean) {
        if (itemSelected == b && itemSelected) {
            invalidate()
        } else {
            itemSelected = b
            if (b) {
                val llparent = this.parent as LinearLayout
                val childCount = llparent.childCount
                for (i in 0..childCount) {
                    val item = llparent.getChildAt(i)
                    if ((item is BottomRadioView)) {
                        if (item != this) {
                            item.setItemSelected(false)
                        }
                        item.invalidate()
                    }
                }
            }
        }
    }

    private var textWidth: Float = 0f

    private var first = true

    private var animStarting = false

    private var desBitmap: Bitmap? = null

    private val cEvaluator = TypeEvaluator<Float> { fraction, startValue, endValue ->
        startValue + fraction * (endValue - startValue)
    }
    private var animRadius = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (text.isNotEmpty()) {
            val drawable = if (itemSelected) selectDrawable as BitmapDrawable else defaultDrawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val itemWidth = bitmap.width
            val left = (measuredWidth - itemWidth) / 2
            val top = measuredHeight / 5

            canvas.drawBitmap(bitmap, left.toFloat(), mTop, mPaint)
            if (textWidth == 0f) {
                mTextPaint.color = Color.TRANSPARENT
                canvas.drawText(text, measuredWidth / 2.toFloat(), measuredHeight.toFloat() - dip(5f), mTextPaint)
                textWidth = mTextPaint.measureText(text)
            }
            mTextPaint.color = if (itemSelected) colorSelect else colorDefault
            canvas.drawText(text, (measuredWidth - textWidth) / 2.toFloat(), measuredHeight.toFloat() - dip(5f), mTextPaint)
        } else {
            val desWidth = measuredHeight - (mTop - 5) * 2
            val left = (measuredWidth - desWidth) / 2
            if (desBitmap == null) {
                val bitmapDrawable = if (itemSelected) selectDrawable as BitmapDrawable else defaultDrawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val scale = desWidth / bitmap.width
                mMatrix.setScale(scale, scale)
                desBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mMatrix, true)
            } else {
            }
            canvas.drawBitmap(desBitmap, left, mTop - 5, mPaint)
            if (itemSelected) {
                //做个动画
                if (animStarting) {
                    if (animRadius == desBitmap!!.width / 2f) {//当达到最大溜
                        animStarting = false
                        mCirclePaint.color= Color.TRANSPARENT
                        animRadius = 0f
                    }
                    canvas.drawCircle(left + desBitmap!!.width / 2, measuredHeight.toFloat() / 2, animRadius, mCirclePaint)
                } else {
                    animStarting = true
                    val delta = 180f
                    val obj = ValueAnimator.ofObject(cEvaluator, 0f, delta)
                    obj.duration = 500
                    obj.repeatCount = 0
                    //插值器效果不太明显。
//                    obj.interpolator = DecelerateInterpolator(2f)
                    obj.addUpdateListener {
                        val value = it.animatedValue as Float//动画变化率
                        //通过0到255的区间，计算白色透明度，以及圆的范围大小
                        mCirclePaint.color = Color.argb(255 - value.toInt(), 255, 255, 255)
                        animRadius = value * desBitmap!!.width / 2 / delta
                        invalidate()
                    }
                    obj.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
//                            loge("animation.isRunning = ${animation?.isRunning}")
                        }
                    })
                    obj.start()
                }
            }
        }
    }

    private fun drawImage(canvas: Canvas, blt: Bitmap, x: Int, y: Int,
                          w: Int, h: Int) {
        var dst: Rect? = Rect()// 屏幕 >>目标矩形
        dst!!.left = x
        dst.top = y
        dst.right = x + w
        dst.bottom = y + h
        canvas.drawBitmap(blt, null, dst, null)
        dst = null
    }

}