package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

import android.widget.EditText

class CasoEmpleadoDataCollection : ArrayList<CasoEmpleadoDataCollectionItem>()
data class CasoEmpleadoDataCollectionItem(
  //  val idcasoempleado : EditText,
   // val idempleado:EditText,
    val idcaso:EditText,
    val fechainiciotrabajoencaso:EditText,
    val fechafinaltrabajoencaso:EditText,
  //  val descripcioncasoempleado:EditText
)
