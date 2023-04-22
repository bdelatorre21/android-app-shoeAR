package com.example.finalprojectdlt

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.finalprojectdlt.databinding.ActivityShoeAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ShoeAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoeAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryArrayList:ArrayList<ModelCategory>

    private var shoeUri: Uri? = null

    private val TAG = "SHOE_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoeAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadShoeCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.attachBtn.setOnClickListener{
            shoePickIntent()
        }
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private var description = ""
    private var category = ""
    private fun validateData() {
        Log.d(TAG, "validateData: validing data checkpoint")
        name = binding.nameEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()

        if (name.isEmpty()){
            Toast.makeText(this, "No Name Given", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty()){
            Toast.makeText(this, "No Desc Given", Toast.LENGTH_SHORT).show()
        }
        else if(category.isEmpty()){
            Toast.makeText(this, "No Category Given", Toast.LENGTH_SHORT).show()
        }
        else if(shoeUri == null){
            Toast.makeText(this, "Pick a File...", Toast.LENGTH_SHORT).show()
        }
        else {
            uploadShoeDataToStorage()
        }

    }

    private fun uploadShoeDataToStorage() {
        Log.d(TAG, "uploadShoeDataToStorage: Uploading Shoe Data. Hold on!")

        progressDialog.setMessage("Finalizing Shoe Data Package...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Shoes/$timestamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(shoeUri!!)
            .addOnSuccessListener {taskSnapshot->
                Log.d(TAG, "uploadShoeDataToStorage: Success! New Data Accepted!")

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadShoeUrl = "${uriTask.result}"

                uploadShoeInfoToFirebase(uploadShoeUrl,timestamp)

            }
            .addOnFailureListener{e->
                Log.d(TAG, "uploadShoeDataToStorage: Failed to Upload. Error Message: ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Upload. Error Message: ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun uploadShoeInfoToFirebase(uploadShoeUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadShoeInfoToFirebase: Uploading to servers")
        progressDialog.setMessage("Uploading Data...")

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["name"] = "$name"
        hashMap["description"] = "$description"
        hashMap["categoryID"] = "$selectedCategoryID"
        hashMap["url"] = "$uploadShoeUrl"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Shoes")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadShoeInfoToFirebase: Upload Completed!")
                progressDialog.dismiss()
                Toast.makeText(this, "Upload Complete...", Toast.LENGTH_SHORT).show()
                shoeUri = null

            }
            .addOnFailureListener {e->
                Log.d(TAG, "uploadShoeDataToStorage: Failed to Upload. Error Message: ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Upload. Error Message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadShoeCategories() {
        Log.d(TAG,"loadShoeCategories: Loading Shoe Categories")
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG,"onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private var selectedCategoryID = ""
    private var selectedCategoryName = ""

    private fun categoryPickDialog(){
        Log.d(TAG,"categoryPickDialog: Showing shoe category picked dialog")


        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (x in categoryArrayList.indices){
            categoriesArray[x] = categoryArrayList[x].category
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){  dialog, which ->
                selectedCategoryID = categoryArrayList[which].id
                selectedCategoryName = categoryArrayList[which].category

                binding.categoryTv.text = selectedCategoryName

                Log.d(TAG,"categoryPickDialog: Selected Category ID: $selectedCategoryID")
                Log.d(TAG,"categoryPickDialog: Selected Category Name: $selectedCategoryName")
            }
            .show()
    }

    private fun shoePickIntent(){
        Log.d(TAG, "shoePickIntent: starting shoe pick intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        shoeActivityResultLauncher.launch(intent)
    }

    val shoeActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if(result.resultCode == RESULT_OK){
                Log.d(TAG, "Item Picked: ")
                shoeUri = result.data!!.data
            }
            else{
                Log.d(TAG, "Failed to Gather Items")
                Toast.makeText(this, "Canceled Add On", Toast.LENGTH_SHORT).show()
            }
        }
    )
}