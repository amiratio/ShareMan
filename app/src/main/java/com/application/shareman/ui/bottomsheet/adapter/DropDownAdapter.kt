package com.application.shareman.ui.bottomsheet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.shareman.R
import com.application.shareman.remote.model.DropDown
import kotlinx.android.synthetic.main.item_dropdown.view.*


class DropDownAdapter(val itemList: ArrayList<DropDown>, private val isMultiSelect: Boolean, private val isFlexBoxStyle: Boolean) : RecyclerView.Adapter<DropDownAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropDownAdapter.ViewHolder {
        val view= if(isFlexBoxStyle) LayoutInflater.from(parent.context).inflate(R.layout.item_flexbox, parent, false)
        else LayoutInflater.from(parent.context).inflate(R.layout.item_flexbox, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: DropDownAdapter.ViewHolder, position: Int) {
        val item= itemList[position]
        with(holder.itemView){
            name.text= item.name
            if(item.selected){
                name.backgroundTintList= ContextCompat.getColorStateList(context, R.color.yellow)
            }else{
                name.backgroundTintList= ContextCompat.getColorStateList(context, R.color.color5)
            }

            setOnClickListener {
                if(isMultiSelect){
                    item.selected = !item.selected
                    notifyDataSetChanged()
                }else{
                    for(i in itemList) i.selected= false
                    item.selected= true
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}