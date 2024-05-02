package com.example.erp_system_

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class SelectedFilesAdapter : RecyclerView.Adapter<SelectedFilesAdapter.SelectedFilesViewHolder>() {

    private var pdfUris: MutableList<Uri> = mutableListOf()
    private lateinit var context: Context

    fun setData(pdfUris: MutableList<Uri>) {
        this.pdfUris = pdfUris
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedFilesViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.selected_file, parent, false)
        return SelectedFilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedFilesViewHolder, position: Int) {
        holder.bind(pdfUris[position], context)
    }

    override fun getItemCount(): Int = pdfUris.size

    class SelectedFilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewFileName: TextView = itemView.findViewById(R.id.textViewFileName)

        fun bind(uri: Uri, context: Context) {
            val fileName = getFileNameFromUri(uri, context)
            textViewFileName.text = fileName
        }

        private fun getFileNameFromUri(uri: Uri, context: Context): String {
            var fileName = ""
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex("_display_name")
                cursor.moveToFirst()
                fileName = cursor.getString(nameIndex)
            }
            return fileName
        }
    }
}
