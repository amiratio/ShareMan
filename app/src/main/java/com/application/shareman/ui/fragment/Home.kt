package com.application.shareman.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.shareman.R
import com.application.shareman.remote.model.Post
import com.application.shareman.service.AddPostToList
import com.application.shareman.service.DatabaseSingleRead
import com.application.shareman.service.LocalStorage
import com.application.shareman.ui.adapter.PostAdapter
import kotlinx.android.synthetic.main.fragment_home.*

class Home : Fragment() {

    private lateinit var adapter: PostAdapter
    private val posts= ArrayList<Post>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        getPosts()
    }

    private fun setup(){
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        adapter= PostAdapter(posts)
        recyclerView.adapter= adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPosts(){
        posts.clear()
        var readSubjectsCount= 0
        for(i in LocalStorage(requireContext()).getMySubjects()){
            DatabaseSingleRead("subjects/${i.name}").result.observe(viewLifecycleOwner){
                AddPostToList(posts, it)
                readSubjectsCount++
                if(readSubjectsCount == LocalStorage(requireContext()).getMySubjects().size) updateUI()
            }
        }
    }

    private fun updateUI(){
        posts.sortWith { o1, o2 -> o1.key.toLong().compareTo(o2.key.toLong()) }
        posts.reverse()
        adapter.notifyDataSetChanged()
    }

}