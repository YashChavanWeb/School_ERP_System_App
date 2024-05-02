package com.example.erp_system_

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.erp_system_.R

class PdfAdapter(private val pdfList: List<PdfItem>) :
    RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pdf, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val pdfItem = pdfList[position]
        holder.bind(pdfItem)
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }

    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPdfIcon: ImageView = itemView.findViewById(R.id.imageViewPdfIcon)
        private val textViewPdfTitle: TextView = itemView.findViewById(R.id.textViewPdfTitle)

        fun bind(pdfItem: PdfItem) {
            textViewPdfTitle.text = pdfItem.title
            imageViewPdfIcon.setImageResource(R.drawable.pdf)
        }

        init {
            itemView.setOnClickListener {
                val pdfItem = pdfList[adapterPosition]
                val pdfUrl = pdfItem.pdfUrl
                showDownloadDialog(itemView.context, pdfUrl)
            }
        }

        private fun showDownloadDialog(context: Context, pdfUrl: String) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
            val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)
            val textViewProgress = dialogView.findViewById<TextView>(R.id.textViewProgress)

            val alertDialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            // Simulate progress update
            val progressMax = 100
            var progress = 0
            val updateInterval = 500 // milliseconds

            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    progress += 10
                    progressBar.progress = progress
                    textViewProgress.text = "Downloading... $progress%"

                    if (progress < progressMax) {
                        // Schedule the next update
                        handler.postDelayed(this, updateInterval.toLong())
                    } else {
                        alertDialog.dismiss()
                        Toast.makeText(context, "PDF download completed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }, updateInterval.toLong())
        }
    }
}
