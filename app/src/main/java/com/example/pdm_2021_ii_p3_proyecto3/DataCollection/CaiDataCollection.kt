package com.example.pdm_2021_ii_p3_proyecto3.DataCollection


import java.util.*
import kotlin.collections.ArrayList

class CaiDataCollection : ArrayList<CaiDataCollectionItem>()
data class CaiDataCollectionItem(
    val idcai :Long,
    val cai: String,
    val rangoinicial: String,
    val rangofinal: String,
    val fechalimite: String
)

///