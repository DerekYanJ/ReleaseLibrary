package com.xinchan.edu.library.base

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * 基本Recycler适配器
 * Created by yanqy on 2017/3/29.
 */

@Suppress("UNCHECKED_CAST")
abstract class BaseRecyclerViewAdapter<D, VH : BaseViewHolder>(layoutResId: Int, data: List<D>?) : androidx.recyclerview.widget.RecyclerView.Adapter<VH>() {
    /**
     * 数据源
     */
    private val data: List<D> = data ?: ArrayList()
    /**
     * 布局资源id
     */
    var layoutResId: Int = 0
    lateinit var view: View

    init {
        if (layoutResId != 0) {
            this.layoutResId = layoutResId
        } else {
            throw NullPointerException("请设置Item资源id")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return BaseViewHolder(view) as VH
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        bindTheData(holder, data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * 绑定数据
     *
     * @param holder 视图管理者
     * @param data   数据源
     */
    protected abstract fun bindTheData(holder: VH, data: D, position: Int)

}
