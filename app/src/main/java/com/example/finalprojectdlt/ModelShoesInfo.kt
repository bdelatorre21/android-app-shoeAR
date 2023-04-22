package com.example.finalprojectdlt

class ModelShoesInfo {

    var uid: String= ""
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var categoryID: String = ""
    var url: String = ""
    var timestamp: Long = 0


    constructor()

    constructor(
        uid: String,
        id: String,
        name: String,
        description: String,
        categoryID: String,
        url: String,
        timestamp: Long
    ) {
        this.uid = uid
        this.id = id
        this.name = name
        this.description = description
        this.categoryID = categoryID
        this.url = url
        this.timestamp = timestamp
    }
}