package com.ramawidi.ghoresepmakanan.data.utils

import androidx.recyclerview.widget.DiffUtil

// This class compares the Data that we send to the Adapter
class AdapterDiffUtil<T>(private val oldList: List<T>,
                         private val newList: List<T>): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}