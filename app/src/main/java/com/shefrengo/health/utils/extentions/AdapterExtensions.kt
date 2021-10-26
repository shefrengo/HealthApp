package com.shefrengo.health.utils.extentions

import android.annotation.TargetApi
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView

internal class RecyclerAdapter<T> : RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    @LayoutRes
    private val itemLayout: Int
    private var itemSize: Int = 0
    private var items: Array<T>? = null
    private var itemsList: MutableList<T>? = null
    private val onBindView: View.(T, Int) -> Unit
    private val itemClick: (T, Int) -> Unit
    private val itemLongClick: (T, Int) -> Unit

    constructor(itemLayout: Int, items: Array<T>, itemSize: Int = 0, onBindView: View.(T, Int) -> Unit, itemClick: (T, Int) -> Unit = { _, _ -> }, itemLongClick: (T, Int) -> Unit = { _, _ -> }) {
        this.itemLayout = itemLayout
        this.items = items
        this.itemSize = itemSize
        this.onBindView = onBindView
        this.itemClick = itemClick
        this.itemLongClick = itemLongClick
    }

    constructor(itemSize: Int = 0, itemLayout: Int, items: MutableList<T>, onBindView: View.(T, Int) -> Unit, itemClick: (T, Int) -> Unit = { _, _ -> }, itemLongClick: (T, Int) -> Unit = { _, _ -> }) {
        this.itemLayout = itemLayout
        this.itemsList = items
        this.itemSize = itemSize
        this.onBindView = onBindView
        this.itemClick = itemClick
        this.itemLongClick = itemLongClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent inflate itemLayout)

    override fun getItemCount(): Int {
        /*return when {
            itemSize != 0 -> itemSize!!
            items == null -> itemsList!!.size
            else -> items!!.size
        }*/
        return itemSize
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = if (items == null) {
            itemsList!![position]
        } else {
            items!![position]
        }

        holder.itemView.setOnClickListener { itemClick(item, position) }
        holder.itemView.setOnLongClickListener { itemLongClick(item, position);true }
        onBindView(holder.itemView, item, position)
    }

    fun addItem(item: T) {
        if (itemsList == null) {
            throw NullPointerException("Your data is Array, change to List to add items dynamically")
        }

        itemsList?.add(item)
        notifyItemInserted(itemsList?.size!!)
    }

    fun updateItem(item: T, position: Int) = if (itemsList == null) {
        items?.set(position, item)
        notifyItemChanged(position)
    } else {
        itemsList?.set(position, item)
        notifyItemChanged(position)
    }

    fun removeItem(position: Int): T {
        if (itemsList == null) {
            throw NullPointerException("Your data is Array, change to List to remove items dynamically")
        }

        if (position < 0) {
            throw IllegalStateException("Position must be >= 0")
        }

        if (position >= itemsList?.size!!) {
            throw IllegalStateException("Position is too big")
        }

        val item = itemsList?.removeAt(position)
        notifyItemRemoved(position)

        return item!!
    }

    internal class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}

internal class TypedRecyclerAdapter<T> : RecyclerView.Adapter<TypedRecyclerAdapter.TypedViewHolder> {
    private val layoutForType: Map<Int, Int>
    private var items: Array<T>? = null
    private var itemsList: MutableList<T>? = null
    private var itemTypes: (position: Int) -> Int
    private val creator: View.(T, Int, Int) -> Unit
    private val itemClick: (T, Int) -> Unit
    private val itemLongClick: (T, Int) -> Unit

    private var currentType = -1

    constructor(layoutForType: Map<Int, Int>, items: Array<T>,
                itemTypes: (position: Int) -> Int,
                creator: View.(T, Int, Int) -> Unit,
                itemClick: (T, Int) -> Unit = { _, _ -> },
                itemLongClick: (T, Int) -> Unit = { _, _ -> }) {
        this.layoutForType = layoutForType
        this.items = items
        this.itemTypes = itemTypes
        this.creator = creator
        this.itemClick = itemClick
        this.itemLongClick = itemLongClick
    }

    constructor(layoutForType: Map<Int, Int>, items: MutableList<T>,
                itemTypes: (position: Int) -> Int,
                creator: View.(T, Int, Int) -> Unit,
                itemClick: (T, Int) -> Unit = { _, _ -> },
                itemLongClick: (T, Int) -> Unit = { _, _ -> }) {
        this.layoutForType = layoutForType
        this.itemsList = items
        this.itemTypes = itemTypes
        this.creator = creator
        this.itemClick = itemClick
        this.itemLongClick = itemLongClick
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypedViewHolder {
        var v: View? = null
        layoutForType.forEach { type, layout ->
            if (type == viewType) {
                currentType = type
                v = parent inflate layout
                return@forEach
            }
        }
        return TypedViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (items == null) {
            itemsList!!.size
        } else {
            items!!.size
        }
    }

    override fun onBindViewHolder(holder: TypedViewHolder, position: Int) {
        val item = if (items == null) {
            itemsList!![position]
        } else {
            items!![position]
        }

        holder.itemView.setOnClickListener { itemClick(item, position) }
        holder.itemView.setOnLongClickListener { itemLongClick(item, position);true }

        creator(holder.itemView!!, item, position, currentType)
    }

    override fun getItemViewType(position: Int): Int {
        return itemTypes(position)
    }

    fun addItem(item: T) {
        if (itemsList == null) {
            throw NullPointerException("Your data is Array, change to List to add items dynamically")
        }

        itemsList?.add(item)
        notifyItemInserted(itemsList?.size!!)
    }

    fun updateItem(item: T, position: Int) = if (itemsList == null) {
        items?.set(position, item)
        notifyItemChanged(position)
    } else {
        itemsList?.set(position, item)
        notifyItemChanged(position)
    }

    fun removeItem(position: Int): T {
        if (itemsList == null) {
            throw NullPointerException("Your data is Array, change to List to remove items dynamically")
        }

        if (position < 0) {
            throw IllegalStateException("Position must be >= 0")
        }

        if (position >= itemsList?.size!!) {
            throw IllegalStateException("Position is too big")
        }

        val item = itemsList?.removeAt(position)
        notifyItemRemoved(position)

        return item!!
    }

    internal class TypedViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}

internal  class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    var fragmentsList = ArrayList<Fragment>()
    var fragmentsArray: Array<Fragment>? = null

    override fun getItem(position: Int): Fragment {
        return if (fragmentsArray == null) {
            fragmentsList[position]
        } else {
            fragmentsArray!![position]
        }
    }

    override fun getCount(): Int {
        return if (fragmentsArray == null) {
            fragmentsList.size
        } else {
            fragmentsArray?.size!!
        }
    }
}