package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

import android.widget.EditText
import kotlin.collections.ArrayList

class AudienciaDataCollection : ArrayList<AudienciaDataCollectionItem>()
data class AudienciaDataCollectionItem(
    val idcaso: EditText,
    val fechaaudiencia: EditText,
    val idjuzgado: EditText,
    val descripcionaudiencia: EditText
)

