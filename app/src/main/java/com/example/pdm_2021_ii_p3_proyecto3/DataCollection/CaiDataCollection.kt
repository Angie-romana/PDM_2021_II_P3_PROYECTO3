package com.example.pdm_2021_ii_p3_proyecto3.DataCollection


import java.util.*
import kotlin.collections.ArrayList

class CaiDataCollection : ArrayList<CaiDataCollectionItem>()
data class CaiDataCollectionItem(
    val cai: Long,
    val rangoinicial: Long,
    val rangofinal: Long,
    val fechalimite: Date
)

