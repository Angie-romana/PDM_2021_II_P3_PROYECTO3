package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

import kotlin.collections.ArrayList

class CasoEmpleadoDataCollection : ArrayList<CasoEmpleadoDataCollectionItem>()
data class CasoEmpleadoDataCollectionItem(
    val idcasoempleado: Long,
    val idempleado:Long,
    val idcaso: Long,
    val fechainiciotrabajoencaso: String,
    val fechafinaltrabajoencaso: String,
    val descripcioncasoempleado: String
)
//