package com.shefrengo.health.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.shefrengo.health.utils.extentions.inflate


class RecyclerViewAdapter<T,S>(private val layout: Int, val onBind: (view: View, item: T, position: Int,item2:S) -> Unit) : RecyclerView.Adapter<RecyclerViewAdapter<T,S>.ViewHolder<T,S>>() {
    private val items = ArrayList<T>()
    private val items2 = ArrayList<S>()

    var onItemClick: ((pos: Int, view: View, item: T,item2:S) -> Unit)? = null
    var size: Int = 0

    fun addItem(item: T) {
        this.items.clear()

        this.items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addItems(items: java.util.ArrayList<T>, items2: java.util.ArrayList<S>) {
        this.items.clear()
        this.items2.clear()
        this.items.addAll(items)

        this.items2.addAll(items2)
        setModelSize(items.size)
        notifyDataSetChanged()

    }

    fun addMoreItems(aList: java.util.ArrayList<T>,aList2: java.util.ArrayList<S>) {
        items.addAll(aList)
        items2.addAll(aList2)
        setModelSize(itemCount + items.size)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        this.items2.clear()
        notifyDataSetChanged()
    }

    fun setModelSize(aSize: Int) {
        size = aSize
        notifyDataSetChanged()
    }

    fun getModel(): ArrayList<T> {
        return items
    }

    inner class ViewHolder<T,S>(private val view: View, val onBind: (view: View, item: T, position: Int,item2:S) -> Unit) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(p0: View?) {
            onItemClick?.invoke(adapterPosition, p0!!, items[adapterPosition],items2[adapterPosition])
        }

        fun bind(item: T, position: Int,item2: S) {
            view.setOnClickListener(this)
            onBind(view, item, position,item2)
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder<T,S> {
        return ViewHolder(p0.inflate(layout), onBind)
    }

    override fun getItemCount(): Int = if (size != 0) items.size else size

    override fun onBindViewHolder(holder: ViewHolder<T,S>, pos: Int) {
        holder.bind(items[pos], pos,items2[pos])

    }

}