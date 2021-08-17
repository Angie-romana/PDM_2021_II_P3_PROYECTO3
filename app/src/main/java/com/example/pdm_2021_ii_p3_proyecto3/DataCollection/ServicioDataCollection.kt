package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class ServicioDataCollection: ArrayList<ServicioDataCollectionItem>()

   data class ServicioDataCollectionItem (
       val idservicio:Long?,
       val nombreservicio:String,
       val descripcionservicio:String

           )



