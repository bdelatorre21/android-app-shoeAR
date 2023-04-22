package com.example.finalprojectdlt

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata

import java.util.*
import kotlin.math.log

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object{
        fun formatTimeStamp(timestamp: Long) : String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }
        fun loadShoeInfoSize(picUrl: String, picTitle: String, sizeTv: TextView){
            val TAG = "PDF_SIZE_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(picUrl)
            ref.metadata
                .addOnSuccessListener {
                    storageMetadata ->
                    Log.d(TAG, "loadShoeInfoSize: ")
                    val bytes = storageMetadata.sizeBytes.toDouble()
                    Log.d(TAG, "loadShoeInfoSize: Size bytes $bytes")

                    val kb = bytes/1024
                    val mb = kb/1024
                    if(mb>=1){
                        sizeTv.text = "${String.format("%.2f", mb)} MB"
                    }
                    else if (kb>=1){
                        sizeTv.text = "${String.format("%.2f", kb)} KB"
                    }
                    else{
                        sizeTv.text = "${String.format("%.2f", bytes)} BYTES"
                    }
                }
                .addOnFailureListener {e ->
                    Log.d(TAG, "loadShoeInfoSize: Failed to get MetaData: ${e.message}")
                }

        }
        fun loadPicFromUrlSinglePage(picUrl: String, picTitle: String, pdfView: PDFView, progressBar: ProgressBar,pagesTv: TextView?){

            val TAG = "PDF_THUMBNAIL_TAG"
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(picUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener {
                        bytes ->
                    Log.d(TAG, "loadShoeInfoSize: Size bytes $bytes")

                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError{ t->
                            progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPicFromUrlSinglePage: ${t.message}")
                        }
                        .onPageError{page, t->
                            progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPicFromUrlSinglePage: ${t.message}")
                        }
                        .onLoad {nbPages ->
                            Log.d(TAG, "loadPicFromUrlSinglePage: Pages: ${nbPages}")
                            progressBar.visibility = View.INVISIBLE

                            if(pagesTv != null){
                                pagesTv.text = "$nbPages"
                            }
                        }
                        .load()


                }
                .addOnFailureListener {e ->
                    Log.d(TAG, "loadShoeInfoSize: Failed to get MetaData: ${e.message}")
                }
        }
        fun loadCategory(categoryID: String, categoryTv: TextView){
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryID)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category = "${snapshot.child("category").value}"

                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

        }
    }


}