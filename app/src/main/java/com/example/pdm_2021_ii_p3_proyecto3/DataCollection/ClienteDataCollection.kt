package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class ClienteDataCollection : ArrayList<ClienteDataCollectionItem>()

data class ClienteDataCollectionItem(
    val idcliente:Long,
    val nombrecliente:String,
    val apellidocliente:String,
    val dnicliente:String,
    val rtncliente:String,
    val telefonocliente:String,
    val direccioncliente:String
)