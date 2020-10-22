package com.example.arpapractice.utils


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arpapractice.models.DummyData


class RecyclerViewGeneralAdapterDiffUtils<T>(private val layoutFiles: Map<Int, Int>, val onGetViewType: (position: Int, itemData: DummyData) -> Int, val onBindItem: (itemData: DummyData, viewHolder: RecyclerViewGeneralAdapterDiffUtils<T>.RecyclerGeneralViewHolder) -> Unit) : ListAdapter<DummyData, RecyclerViewGeneralAdapterDiffUtils<T>.RecyclerGeneralViewHolder>(DiffCallback()) {

    // TODO: EXPERIMENTAL CODE
    /*init {
        setHasStableIds(true)
    }*/

    constructor(layoutFile: Int, onBindItem: (itemData: DummyData, viewHolder: RecyclerViewGeneralAdapterDiffUtils<T>.RecyclerGeneralViewHolder) -> Unit) : this(mapOf(-10 to layoutFile), { _, _ -> -10 }, onBindItem)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerGeneralViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutFiles[viewType] ?: error(""),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerGeneralViewHolder, position: Int) = onBindItem(getItem(position), holder)
    override fun getItemViewType(position: Int): Int = onGetViewType(position, getItem(position))

    inner class RecyclerGeneralViewHolder(v: View) : RecyclerView.ViewHolder(v)

    class DiffCallback : DiffUtil.ItemCallback<DummyData>() {

        override fun areItemsTheSame(oldItem: DummyData, newItem: DummyData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DummyData, newItem: DummyData): Boolean {
            return oldItem == newItem
        }
    }

    fun updateAdapter(list:ArrayList<DummyData>){
        submitList(null)
        submitList(list)
    }

}