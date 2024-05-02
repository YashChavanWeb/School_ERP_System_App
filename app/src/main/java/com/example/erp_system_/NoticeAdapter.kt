package com.example.erp_system_

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class NoticeAdapter(context: Context, resource: Int, objects: List<Notice>) :
    ArrayAdapter<Notice>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.notice_show_kayout, parent, false)
        }

        val textViewNoticeTitle = itemView!!.findViewById<TextView>(R.id.textViewNoticeTitle)
        val textViewNoticeDate = itemView.findViewById<TextView>(R.id.textViewNoticeDate)
        val imagePreview = itemView.findViewById<ImageView>(R.id.imagePreview)
        val textViewNoticeDescription = itemView.findViewById<TextView>(R.id.textViewNoticeDescription)

        val notice = getItem(position)

        textViewNoticeTitle.text = notice?.title
        textViewNoticeDate.text = notice?.date
        textViewNoticeDescription.text = notice?.description

        // Set image using Glide
        Glide.with(context).load(notice?.imageUrl).into(imagePreview)

        return itemView
    }
}
