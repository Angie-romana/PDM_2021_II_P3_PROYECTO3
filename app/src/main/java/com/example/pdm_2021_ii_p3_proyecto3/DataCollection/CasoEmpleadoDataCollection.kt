package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

import android.widget.EditText
import java.util.*
import kotlin.collections.ArrayList

class CasoEmpleadoDataCollection : ArrayList<CasoEmpleadoDataCollectionItem>()
data class CasoEmpleadoDataCollectionItem(
  //  val idcasoempleado : EditText,
   // val idempleado:EditText,
    val idcaso:Long,
    val fechainiciotrabajoencaso: Date,
    val fechafinaltrabajoencaso:Date,
  //  val descripcioncasoempleado:EditText
)
