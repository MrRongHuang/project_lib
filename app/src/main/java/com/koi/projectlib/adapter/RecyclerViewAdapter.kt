package com.koi.projectlib.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.koi.projectlib.R

/**
 * RecyclerViewAdapter class
 *
 * @author LX created on 2021/4/9
 *
 */
class RecyclerViewAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_recycler_view) {
    override fun convert(holder: BaseViewHolder, item: Int) {
        holder.setImageResource(R.id.ivImage, item)
    }
}