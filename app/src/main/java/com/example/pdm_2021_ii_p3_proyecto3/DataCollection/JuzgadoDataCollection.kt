package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class JuzgadoDataCollection: ArrayList<JuzgadoDataCollectionItem>()

data class JuzgadoDataCollectionItem (
    var idjuzgado:Long,
    val nombrejuzgado:String,
    val direccionjuzgado:String
)
