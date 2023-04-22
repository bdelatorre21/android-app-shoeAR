 package com.example.finalprojectdlt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.finalprojectdlt.databinding.ActivityShoeListAdBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

 class ShoeListAdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoeListAdBinding

    private companion object{
        const val TAG = "PDF_LIST_ADMIN"
    }

    private var categoryID = ""
    private var category = ""

     private lateinit var picArrayList: ArrayList<ModelShoesInfo>

     private lateinit var adapterShoesInfoAdmins: AdapterShoesInfoAdmins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoeListAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        categoryID = intent.getStringExtra("categoryID")!!
        category = intent.getStringExtra("category")!!

        binding.subTitleTv.text = category

        loadShoesList()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                try{
                    adapterShoesInfoAdmins.filter!!.filter(s)

                }
                catch (e: Exception){
                    Log.d(TAG, "onTextChanged: ${e.message}")

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }


     private fun loadShoesList() {
        picArrayList = ArrayList()

         val ref = FirebaseDatabase.getInstance().getReference("Shoes")
         ref.orderByChild("categoryID").equalTo(categoryID)
             .addListenerForSingleValueEvent(object: ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     for (ds in snapshot.children){
                         val model = ds.getValue(ModelShoesInfo::class.java)
                         if (model != null) {
                             picArrayList.add(model)
                             Log.d(TAG, "onDataChange: ${model.name} ${model.categoryID} ")
                         }
                     }
                     adapterShoesInfoAdmins = AdapterShoesInfoAdmins(this@ShoeListAdActivity, picArrayList)
                     binding.shoesRv.adapter = adapterShoesInfoAdmins
                 }

                 override fun onCancelled(error: DatabaseError) {
                 }
             })
     }
 }