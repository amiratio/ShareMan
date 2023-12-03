package com.application.shareman.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.application.shareman.R
import com.application.shareman.service.SingleLiveEvent

class AlertDialog(private val text: String, private val desc: String?= "", private val buttonText: String?= "") : DialogFragment(){

    val result: SingleLiveEvent<Boolean> by lazy { SingleLiveEvent<Boolean>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view= inflater.inflate(R.layout.dialog_alert_dialog, container, false)
        val window: Window? = dialog!!.window
        val wlp: WindowManager.LayoutParams = window!!.attributes
        //dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        wlp.gravity = Gravity.CENTER
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(false)
        window.attributes = wlp

        uiSetup(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(resources.displayMetrics.widthPixels, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun uiSetup(view: View){
        val title= view.findViewById<TextView>(R.id.titleText)
        val description= view.findViewById<TextView>(R.id.descText)
        val ok= view.findViewById<Button>(R.id.ok)
        title.text= text
        description.text= desc
        if(desc!!.isBlank()) description.visibility= View.GONE
        if(buttonText!!.isNotBlank()) ok.text= buttonText
        ok.setOnClickListener{ result.value= true; dialog!!.dismiss()}
    }
}