package com.example.pdm_2021_ii_p3_proyecto3.DataCollection



class CasoDataCollection : ArrayList<CasoDataCollectionItem>()
data class CasoDataCollectionItem(
    val idcaso: Long,
    val tipocaso: String,
    val sentenciacaso: String,
    val idcliente: Long,
    val idservicio: Long,
    val estadocaso: String
)
