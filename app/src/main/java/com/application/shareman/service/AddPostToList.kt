package com.application.shareman.service

import com.application.shareman.remote.model.Post
import com.google.firebase.database.DataSnapshot

class AddPostToList(posts: ArrayList<Post>, dataSnapshot: DataSnapshot) {

    init {
        if(dataSnapshot.value != null){
            for(i in dataSnapshot.children){
                posts.add(Post(
                    i.child("type").value.toString(),
                    if(i.hasChild("media")) i.child("media").value.toString() else "",
                    if(i.hasChild("content")) i.child("content").value.toString() else "",
                    if(i.hasChild("geo")) i.child("geo").value.toString().split(",")[0] else "",
                    if(i.hasChild("geo")) i.child("geo").value.toString().split(",")[1] else "",
                    i.child("comments").value.toString().toBoolean(),
                    i.child("name").value.toString(),
                    i.child("username").value.toString(),
                    i.key.toString(),
                ))
            }
        }
    }
    
}