package com.shefrengo.health.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

import androidx.recyclerview.widget.RecyclerView



import java.util.*

abstract class BaseRecyclerAdapter<T, VB> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mModelList = ArrayList<T>()

    abstract val layoutResId: Int

    var size: Int? = null


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(mModelList[position], position, (holder as BaseRecyclerAdapter<*, *>.DataViewHolder).mDataBinding as VB)
      //  (holder.mDataBinding as ViewDataBinding).setVariable(BR.model, mModelList[position])
    //  (holder.mDataBinding as ViewDataBinding).setVariable(BR.callback, holder.listener)
    }

    override fun getItemCount(): Int = if (size == null || mModelList.size <= size!!) mModelList.size else size!!

    abstract fun onBindData(model: T, position: Int, dataBinding: VB)

    abstract fun onItemClick(view: View, model: T, position: Int, dataBinding: VB)

    abstract fun onItemLongClick(view: View, model: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = DataViewHolder(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutResId, parent, false))

    fun setModelSize(aSize: Int) {
        size = aSize
    }

    fun addItems(aList: ArrayList<T>) {
        mModelList.clear()
        mModelList.addAll(aList)
        notifyDataSetChanged()
    }

    fun getModel(): ArrayList<T> {
        return mModelList
    }

    fun sortByHighPrice() {
        notifyDataSetChanged()
    }

    fun addMoreItems(aList: ArrayList<T>) {
        aList.forEach {
            mModelList.add(it)
            notifyItemInserted(mModelList.size - 1)
        }
    }

    fun addNewItem(item: T) {
        mModelList.add(item)
        notifyItemInserted(mModelList.size - 1)
    }

    fun removeItem(pos: Int) {
        mModelList.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun getItem(position: Int): T = mModelList[position]

    fun clearData() {
        mModelList.clear()
        notifyDataSetChanged()
    }

    internal inner class DataViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root), OnClickListener<T> {
        override fun onClick(view: View, t: T) {
            onItemClick(view, t, adapterPosition, mDataBinding)
        }

        var mDataBinding: VB = binding as VB

        val listener: OnClickListener<T>? = this
    }

    interface OnClickListener<T> {
        fun onClick(view: View, t: T)
    }
}