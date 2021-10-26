package com.shefrengo.health.adapters
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shefrengo.health.utils.extentions.inflate


class RecyclerView2Adapter<T>(private val layout: Int, val onBind: (view: View, item: T, position: Int) -> Unit) : RecyclerView.Adapter<RecyclerView2Adapter<T>.ViewHolder<T>>() {
    private val items = ArrayList<T>()
    var onItemClick: ((pos: Int, view: View, item: T) -> Unit)? = null
    var size: Int = 0

    fun addItem(item: T) {
        this.items.clear()
        this.items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addItems(items: ArrayList<T>) {
        this.items.clear()
        this.items.addAll(items)
        setModelSize(items.size)
        notifyDataSetChanged()

    }

    fun addMoreItems(aList: java.util.ArrayList<T>) {
        items.addAll(aList)
        setModelSize(itemCount + items.size)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

    fun setModelSize(aSize: Int) {
        size = aSize
        notifyDataSetChanged()
    }

    fun getModel(): ArrayList<T> {
        return items
    }

    inner class ViewHolder<T>(private val view: View, val onBind: (view: View, item: T, position: Int) -> Unit) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(p0: View?) {
            onItemClick?.invoke(adapterPosition, p0!!, items[adapterPosition])
        }

        fun bind(item: T, position: Int) {
            view.setOnClickListener(this)
            onBind(view, item, position)
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder<T> {
        return ViewHolder(p0.inflate(layout), onBind)
    }

    override fun getItemCount(): Int = if (size != 0) items.size else size

    override fun onBindViewHolder(holder: ViewHolder<T>, pos: Int) {
        holder.bind(items[pos], pos)
    }

}