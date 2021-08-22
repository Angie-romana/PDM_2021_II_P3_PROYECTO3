package com.example.pdm_2021_ii_p3_proyecto3.DataCollection


import kotlin.collections.ArrayList

class AudienciaDataCollection : ArrayList<AudienciaDataCollectionItem>()
data class AudienciaDataCollectionItem(
    val idcaso: Long,
    val fechaaudiencia: String,
    val idjuzgado: Long,
    val descripcionaudiencia: String
)

