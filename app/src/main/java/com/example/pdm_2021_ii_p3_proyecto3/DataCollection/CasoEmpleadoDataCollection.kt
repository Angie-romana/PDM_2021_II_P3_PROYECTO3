package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

import kotlin.collections.ArrayList

class CasoEmpleadoDataCollection : ArrayList<CasoEmpleadoDataCollectionItem>()
data class CasoEmpleadoDataCollectionItem(
  //  val idcasoempleado : EditText,
   // val idempleado:EditText,
    val idcaso:Long,
    val fechainiciotrabajoencaso: String,
    val fechafinaltrabajoencaso: String,
  //  val descripcioncasoempleado:EditText
)
