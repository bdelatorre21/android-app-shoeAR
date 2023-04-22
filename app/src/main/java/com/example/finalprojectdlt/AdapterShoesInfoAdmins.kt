package com.example.finalprojectdlt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectdlt.databinding.RowShoesinfoAdminBinding

class AdapterShoesInfoAdmins : RecyclerView.Adapter<AdapterShoesInfoAdmins.HolderShoesInfoAdmin> , Filterable {

    private var context: Context
    public var shoesInfoArrayList: ArrayList<ModelShoesInfo>
    private val filterList: ArrayList<ModelShoesInfo>


    private lateinit var binding:RowShoesinfoAdminBinding

    var filter : FilterPicAdmin? = null

    constructor(context: Context, shoesInfoArrayList: ArrayList<ModelShoesInfo>) : super() {
        this.context = context
        this.shoesInfoArrayList = shoesInfoArrayList
        this.filterList = shoesInfoArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderShoesInfoAdmin {
        binding = RowShoesinfoAdminBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderShoesInfoAdmin(binding.root)
    }

    override fun getItemCount(): Int {
        return shoesInfoArrayList.size

    }

    override fun onBindViewHolder(holder: HolderShoesInfoAdmin, position: Int) {
        val model = shoesInfoArrayList[position]
        val picId = model.id
        val categoryID = model.categoryID
        val name = model.name
        val description = model.description
        val picUrl = model.url
        val timestamp = model.timestamp
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        holder.titleTv.text = name
        holder.descriptionTv.text= description
        holder.dateTv.text = formattedDate


        MyApplication.loadCategory(categoryID, holder.categoryTv)

        MyApplication.loadPicFromUrlSinglePage(picUrl, name, holder.pdfView , holder.progressBar, null)

        MyApplication.loadShoeInfoSize(picUrl, name, holder.sizeTv)

    }


    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterPicAdmin(filterList,this)

        }
        return filter as FilterPicAdmin
    }

    inner class HolderShoesInfoAdmin(itemView: View): RecyclerView.ViewHolder(itemView){

        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }

}