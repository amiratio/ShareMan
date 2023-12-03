package com.application.shareman.ui.bottomsheet

import android.content.Context
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.shareman.R
import com.application.shareman.service.SingleLiveEvent
import com.application.shareman.ui.bottomsheet.adapter.DropDownAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottomsheet_dropdown.*

class DropDown(private val context: Context, private var list: ArrayList<com.application.shareman.remote.model.DropDown>, private val isMultiSelect: Boolean?= false, private val isFlexBoxStyle: Boolean?= false) {

    val result: SingleLiveEvent<ArrayList<com.application.shareman.remote.model.DropDown>> by lazy { SingleLiveEvent<ArrayList<com.application.shareman.remote.model.DropDown>>() }

    init {
        val dialog= BottomSheetDialog(context, R.style.TransparentBottomSheet)
        dialog.setContentView(R.layout.bottomsheet_dropdown)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        with(dialog){
            if(isFlexBoxStyle!!) recyclerView.layoutManager= FlexboxLayoutManager(context)
            else recyclerView.layoutManager= LinearLayoutManager(context)
            val adapter= DropDownAdapter(list, isMultiSelect!!, isFlexBoxStyle)
            recyclerView.adapter= adapter

            select.setOnClickListener {
                var isOrNot= false
                for(i in adapter.itemList){
                    if(i.selected){
                        result.postValue(adapter.itemList)
                        isOrNot= true
                        dismiss()
                        break
                    }
                }
                if(!isOrNot) select.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            }
        }

        dialog.show()
    }

}