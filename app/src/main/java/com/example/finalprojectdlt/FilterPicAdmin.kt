package com.example.finalprojectdlt

import android.widget.Filter


class FilterPicAdmin : Filter {
    var filterList: ArrayList<ModelShoesInfo>

    var adapterShoesInfoAdmin: AdapterShoesInfoAdmins

    constructor(
        filterList: ArrayList<ModelShoesInfo>,
        adapterShoesInfoAdmin: AdapterShoesInfoAdmins
    ) {
        this.filterList = filterList
        this.adapterShoesInfoAdmin = adapterShoesInfoAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint
        val results = FilterResults()

        if(constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelShoesInfo>()
            for (x in filterList.indices){
                if (filterList[x].name.lowercase().contains(constraint)){
                    filteredModels.add(filterList[x])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapterShoesInfoAdmin.shoesInfoArrayList = results.values as ArrayList<ModelShoesInfo>

        adapterShoesInfoAdmin.notifyDataSetChanged()

    }
}