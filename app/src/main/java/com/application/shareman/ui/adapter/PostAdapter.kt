package com.application.shareman.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.shareman.R
import com.application.shareman.remote.model.Post
import com.application.shareman.service.DateToTimeAgoConverter
import com.application.shareman.service.PicassoPicture
import com.application.shareman.service.Tools
import kotlinx.android.synthetic.main.item_flexbox.view.*
import kotlinx.android.synthetic.main.item_post_picture.view.*
import kotlinx.android.synthetic.main.item_profile_post.view.*
import kotlinx.android.synthetic.main.item_profile_post.view.image


class PostAdapter(val itemList: ArrayList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
        context= parent.context
        return when (viewType) {
            R.layout.item_post_picture -> PicturePost(view.inflate(viewType, parent, false))
            R.layout.item_post_text -> TextPost(view.inflate(viewType, parent, false))
            else -> PicturePost(view.inflate(viewType, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].type) {
            "picture" -> R.layout.item_post_picture
            "video" -> R.layout.item_post_picture
            "text" -> R.layout.item_post_text
            else -> R.layout.item_post_picture
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item= itemList[position]
        with(holder.itemView){
            username.text= item.username
            date.text= Tools().timeStampToTimeAgo(context, item.key)
            content.text= item.content

            when(item.type){
                "picture" -> {
                    PicassoPicture(context, image, item.media)
                }
                "text" -> {

                }
                else -> {}
            }
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class PicturePost(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class VideoPost(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class TextPost(itemView: View) : RecyclerView.ViewHolder(itemView)
}