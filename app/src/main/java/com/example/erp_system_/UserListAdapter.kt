package com.example.erp_system_

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView

class UserListAdapter(context: Context, userList: List<User>) :
    ArrayAdapter<User>(context, 0, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val user = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false)
        }

        val rollTextView = view?.findViewById<TextView>(R.id.rollTextView)
        val nameTextView = view?.findViewById<TextView>(R.id.nameTextView)
        val presentRadioButton = view?.findViewById<RadioButton>(R.id.presentRadioButton)
        val absentRadioButton = view?.findViewById<RadioButton>(R.id.absentRadioButton)

        rollTextView?.text = user?.generateRollNumber()
        nameTextView?.text = user?.fullName

        // Set OnClickListener for the present radio button
        presentRadioButton?.setOnClickListener {
            presentRadioButton.isChecked = true
            absentRadioButton?.isChecked = false
        }

        // Set OnClickListener for the absent radio button
        absentRadioButton?.setOnClickListener {
            absentRadioButton.isChecked = true
            presentRadioButton?.isChecked = false
        }

        return view!!
    }
}
