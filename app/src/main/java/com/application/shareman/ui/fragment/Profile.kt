package com.application.shareman.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.application.shareman.R
import com.application.shareman.remote.model.Post
import com.application.shareman.service.AddPostToList
import com.application.shareman.service.DatabaseSingleRead
import com.application.shareman.service.LocalStorage
import com.application.shareman.ui.activity.Host
import com.application.shareman.ui.adapter.ProfilePostsAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.username

class Profile : Fragment() {

    private lateinit var adapter: ProfilePostsAdapter
    private val posts= ArrayList<Post>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        userInfo()
        posts()
        buttons()
    }

    private fun setup(){
        recyclerView.layoutManager= GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        adapter= ProfilePostsAdapter(posts)
        recyclerView.adapter= adapter
    }

    @SuppressLint("SetTextI18n")
    private fun userInfo(){
        name.text= LocalStorage(requireContext()).getName()
        username.text= "@${LocalStorage(requireContext()).getUsername()}"
        if(LocalStorage(requireContext()).getAvatar().isNotBlank()) Glide.with(requireActivity()).load(LocalStorage(requireContext()).getAvatar()).into(avatar)
        else Glide.with(requireActivity()).load(R.drawable.im_avatar).into(avatar)
        if(LocalStorage(requireContext()).getBio().isNotBlank()){
            bioLayout.visibility= View.VISIBLE
            bio.text= LocalStorage(requireContext()).getBio()
        }else bioLayout.visibility= View.GONE
        if(LocalStorage(requireContext()).getJob().isNotBlank()){
            jobLayout.visibility= View.VISIBLE
            job.text= LocalStorage(requireContext()).getJob()
        }else jobLayout.visibility= View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun posts(){
        DatabaseSingleRead("users/${LocalStorage(requireContext()).getUsername()}/posts").result.observe(viewLifecycleOwner){
            posts.clear()
            AddPostToList(posts, it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun buttons(){
        addNewPost.setOnClickListener { (activity as Host).addNewPost() }
    }

}