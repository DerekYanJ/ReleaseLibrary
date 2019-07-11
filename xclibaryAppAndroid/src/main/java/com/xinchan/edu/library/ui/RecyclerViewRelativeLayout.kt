package com.xinchan.edu.library.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.xinchan.edu.library.R

/**
 * 组合控件：recyclerview上下拉刷新和置顶
 * Created by DerekYan on 2017/5/10.
 */

class RecyclerViewRelativeLayout : RelativeLayout {
    lateinit var swiperefreshlayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    lateinit var recyclerview: RecyclerView
    lateinit var floatingactionbutton: FloatingActionButton
    lateinit var no_data_view: View
    lateinit var no_data_textview: TextView//没有数据描述
    lateinit var no_data_imageview: ImageView//没有数据图片

    lateinit var layoutmanager: LinearLayoutManager
    private var pageNum = 20 //每页显示条目数量
    var isLoadMore = true //是否可以加载更多
    var isRefresh = true//是否是刷新
    private var currentItemNum = 0//更新条目数量

    //是否可刷新
    private var mEnabled = true

    //能否进行上拉加载更多
    private var isCanLoadMore = true

    fun setRefreshEnable(enabled: Boolean): RecyclerViewRelativeLayout {
        this.mEnabled = enabled
        swiperefreshlayout.isEnabled = enabled
        return this
    }

    fun setPageNum(pageNum: Int): RecyclerViewRelativeLayout{
        this.pageNum = pageNum
        return this
    }

    fun getPageNum(): Int{
        return pageNum
    }

    fun getRecyclerView() = recyclerview

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    /**
     * 初始化View
     */
    private fun initView(context: Context) {
        View.inflate(context, R.layout.layout_recyclerview_relativelayout, this)

        swiperefreshlayout = findViewById(R.id.srl)
        //        swiperefreshlayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        //        swiperefreshlayout.setSize(SwipeRefreshLayout.LARGE);
        swiperefreshlayout.setProgressViewEndTarget(true, 150)

        recyclerview = findViewById(R.id.rv)
        layoutmanager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutmanager

        floatingactionbutton = findViewById(R.id.fab)
        no_data_view = findViewById(R.id.view_no_data)
        no_data_textview = findViewById(R.id.tv_no_data)
        no_data_imageview = findViewById(R.id.iv_no_data)
    }

    fun removeItemDecoration(index: Int): RecyclerViewRelativeLayout{
        recyclerview.removeItemDecorationAt(index)
        return this
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration): RecyclerViewRelativeLayout{
        recyclerview.addItemDecoration(decor)
        return this
    }

    fun setColorSchemeResources(colorResIds: Int): RecyclerViewRelativeLayout{
        swiperefreshlayout.setColorSchemeResources(colorResIds)
        return this
    }


    /**
     * 设置事件监听
     */
    fun setListener(refreshLisener:() -> Unit?, loadMoreLisener:() -> Unit?) : RecyclerViewRelativeLayout{
        //下拉刷新
        swiperefreshlayout.setOnRefreshListener {
            refresh()
            refreshLisener.invoke()
        }
        //没有数据
        no_data_view.setOnClickListener {
            refresh()
            refreshLisener.invoke()
        }
        //上拉加载更多
        recyclerview.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        //不可上拉加载更多
                        if (!isCanLoadMore) {
                            return
                        }
                        //是否能向上滚动，false表示已经滚动到底部
                        if (!recyclerView!!.canScrollVertically(1)) {
                            if (!swiperefreshlayout.isRefreshing) {
                                if (isLoadMore)
                                    loadMoreLisener.invoke()
                            }
                        }
                        if (layoutmanager.findLastVisibleItemPosition() > pageNum - 1) {
                            if (!floatingactionbutton.isShown)
                                floatingactionbutton.show()
                        } else {
                            if (floatingactionbutton.isShown)
                                floatingactionbutton.hide()
                        }
                    }
                }
        )
        //置顶
        floatingactionbutton.setOnClickListener {
            //滚动到第一个item
            recyclerview.smoothScrollToPosition(0)
        }
        return this
    }

    /**
     * 设置刷新状态
     * @param flag 是否刷新
     */
    fun setRefreshing(flag: Boolean) {
        try {
            swiperefreshlayout.post {
                if (flag && !swiperefreshlayout.isRefreshing)
                    swiperefreshlayout.isRefreshing = true
                else if (!flag && swiperefreshlayout.isRefreshing)
                    swiperefreshlayout.isRefreshing = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置更新的item的数量
     * 数据刷新后必须调用 用于确定是否可以加载更多和确定是不是要显示置顶fab
     * @param size 更新的条目数量
     * @param currentTotal 当前条目总数
     * @param total 列表后台数据总数
     */
    fun setUpdateSize(size: Int, currentTotal: Int, total: Int) {
        if (size < pageNum || size == 0) {
            isLoadMore = false
        }
        if (currentItemNum == 0 && isLoadMore) {
            currentItemNum = size
        }
        isRefresh = false

        if (currentTotal == total) {
            isLoadMore = false
        }

        if (currentTotal > 0) {
            no_data_view.visibility = View.GONE
            swiperefreshlayout.visibility = View.VISIBLE
        }
    }

    /**
     * 设置更新的item的数量
     * 数据刷新后必须调用 用于确定是否可以加载更多和确定是不是要显示置顶fab
     * @param size 更新的条目数量
     */
    fun setUpdateSize(size: Int): RecyclerViewRelativeLayout{
        if (size < pageNum || size == 0) {
            isLoadMore = false
        }
        if (currentItemNum == 0 && isLoadMore) {
            currentItemNum = size
        }
        isRefresh = false
        return this
    }

    /**
     * 当前显示的总数 用于显示没有数据时图片
     * @param total 当前显示的总数
     */
    fun setCurrentTotal(total: Int): RecyclerViewRelativeLayout {
        if (total == 0) {
            no_data_view.visibility = View.VISIBLE
            swiperefreshlayout.visibility = View.GONE
        } else {
            no_data_view.visibility = View.GONE
            swiperefreshlayout.visibility = View.VISIBLE
        }
        return this
    }

    /**
     * 设置无数据时 文字描述
     * @param desc 描述信息
     */
    fun setNoDataDesc(desc: String): RecyclerViewRelativeLayout {
        try {
            no_data_view.visibility = View.VISIBLE
            swiperefreshlayout.visibility = View.GONE
            no_data_textview.text = desc
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    /**
     * 设置无数据时显示
     * @param desc 文字描述
     * @param noDataResId
     */
    fun setNoData(desc: String, noDataResId: Int): RecyclerViewRelativeLayout {
        try {
            no_data_view.visibility = View.VISIBLE
            swiperefreshlayout.visibility = View.GONE
            no_data_textview.text = desc
            no_data_imageview.setImageResource(noDataResId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    /**
     * 刷新
     */
    fun refresh() {
        isLoadMore = true
        currentItemNum = 0
        isRefresh = true
    }

}
