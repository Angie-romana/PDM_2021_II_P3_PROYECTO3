package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class EmpleadoDataCollection : ArrayList<EmpleadoDataCollectionItem>()

data class EmpleadoDataCollectionItem(
    val idempleado:Long?,
    val nombreempleado:String,
    val apellidoempleado:String,
    val dniempleado:String,
    val telefonoempleado:Long,
    val direccionempleado:String,
    val salarioempleado:Double,
    val tipoempleado:String,
    val nombreusuario:String,
    val claveusuario:String
)