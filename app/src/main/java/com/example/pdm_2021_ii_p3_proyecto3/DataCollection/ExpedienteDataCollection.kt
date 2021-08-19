package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class ExpedienteDataCollection : ArrayList<ExpedienteDataCollectionItem>()

data class ExpedienteDataCollectionItem(
    val idexpediente:Long,
    val entidad:String,
    val numexpediente:String,
    val idcaso:Long
)