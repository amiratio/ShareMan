package com.application.shareman.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.shareman.R
import com.application.shareman.remote.model.Subject
import com.application.shareman.service.LocalStorage
import kotlinx.android.synthetic.main.item_flexbox.view.*


class SubjectsAdapter(private val itemList: List<Subject>) : RecyclerView.Adapter<SubjectsAdapter.ViewHolder>() {

    private lateinit var context: Context
    val selected= ArrayList<Subject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_flexbox, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectsAdapter.ViewHolder, position: Int) {
        val item= itemList[position]
        with(holder.itemView){
            name.text= item.name
            setOnClickListener {
                if(!selected.contains(item)){
                    if(selected.size < LocalStorage(context).getMaxSubjectSelection().toInt()){
                        selected.add(item)
                        name.backgroundTintList= ContextCompat.getColorStateList(context, R.color.color2)
                        name.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                        name.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                }else{
                    selected.remove(item)
                    name.backgroundTintList= ContextCompat.getColorStateList(context, R.color.color1)
                    name.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                    name.setTextColor(ContextCompat.getColor(context, R.color.darkGray))
                }
            }
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}