package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

import android.widget.EditText

class CasoDataCollection : ArrayList<CasoDataCollectionItem>()
data class CasoDataCollectionItem(
    val idcaso: EditText,
    val tipocaso: EditText,
    val sentenciacaso: EditText,
    val idcliente: EditText,
    val idservicio: EditText,
    val estadocaso: EditText
)
