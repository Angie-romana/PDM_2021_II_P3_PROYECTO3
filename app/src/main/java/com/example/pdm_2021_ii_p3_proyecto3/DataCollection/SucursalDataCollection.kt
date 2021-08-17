package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class SucursalDataCollection:ArrayList<SucursalDataCollectionItem>()

data class SucursalDataCollectionItem (
    val idsucursal:Long?,
    val nombresucursal:String,
    val dirreccionsucursal:String,
    val telefonosucursal:Long=0,
    val emailsucursal:String
)