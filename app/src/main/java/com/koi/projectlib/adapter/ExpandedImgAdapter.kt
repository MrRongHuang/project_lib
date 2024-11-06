package com.koi.projectlib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.koi.projectlib.R

class ExpandedImgAdapter(private val images: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isExpanded = false
    private val MAX_VISIBLE_ITEMS = 8

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            ImageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_show_more, parent, false)
            ShowMoreViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_IMAGE) {
            val imageHolder = holder as ImageViewHolder
            // 加载图片，可以使用 Glide 或 Picasso
            Glide.with(holder.itemView.context).load(images[position]).into(imageHolder.imageView)
        } else {
            val showMoreHolder = holder as ShowMoreViewHolder
            // 加载图片，可以使用 Glide 或 Picasso
            Glide.with(holder.itemView.context).load(images[position]).into(showMoreHolder.imageView)
            showMoreHolder.showMoreText.text = "+ ${images.size - MAX_VISIBLE_ITEMS -1 }\n 展开 ∨" //+5 展开
            showMoreHolder.showMoreText.setOnClickListener {
                isExpanded = true
                notifyDataSetChanged()  // 刷新数据
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isExpanded) {
            images.size
        } else {
            if (images.size > MAX_VISIBLE_ITEMS) {
                MAX_VISIBLE_ITEMS + 1  // 多加一个用于显示"展开更多"
            } else {
                images.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!isExpanded && position == MAX_VISIBLE_ITEMS) {
            ITEM_TYPE_SHOW_MORE
        } else {
            ITEM_TYPE_IMAGE
        }
    }

    companion object {
        private const val ITEM_TYPE_IMAGE = 1
        private const val ITEM_TYPE_SHOW_MORE = 2
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    class ShowMoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val showMoreText: TextView = view.findViewById(R.id.showMoreText)
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}
