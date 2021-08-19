package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class PrecioHistoricoDataCollection : ArrayList<PrecioHistoricoDataCollectionItem>()

data class PrecioHistoricoDataCollectionItem(
    val idpreciohistorico: Long,
    val fechainicialpreciohistorico:String,
    val fechafinalpreciohistorico:String,
    val idservicio:Long,
    val precio:Double
)