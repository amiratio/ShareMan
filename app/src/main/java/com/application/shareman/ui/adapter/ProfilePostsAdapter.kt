package com.application.shareman.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.shareman.R
import com.application.shareman.remote.model.Post
import com.application.shareman.remote.model.Subject
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.PicassoPicture
import kotlinx.android.synthetic.main.item_flexbox.view.*
import kotlinx.android.synthetic.main.item_profile_post.view.*


class ProfilePostsAdapter(private val itemList: List<Post>) : RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder>() {

    private lateinit var context: Context
    val selected= ArrayList<Subject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostsAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_profile_post, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfilePostsAdapter.ViewHolder, position: Int) {
        val item= itemList[position]
        with(holder.itemView){
            if(item.type == "text"){
                image.setPadding(30,30,30,30)
                image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.im_text))
            }else{
                image.setPadding(0,0,0,0)
                PicassoPicture(context, image,item.media)
            }
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}