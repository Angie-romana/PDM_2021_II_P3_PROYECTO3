package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

import android.widget.EditText
import kotlin.collections.ArrayList

class CaiDataCollection : ArrayList<CaiDataCollectionItem>()
data class CaiDataCollectionItem(
    val cai: EditText,
    val rangoinicial: EditText,
    val rangofinal: EditText,
    val fechalimite: EditText
)

