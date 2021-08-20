package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class FacturaDetalleCollection : ArrayList<FacturaDetalleCollectionItem>()

data class FacturaDetalleCollectionItem(
    val iddetalle:Long,
    val idfactura:Long,
    val idservicio:Long,
    val cantidadfactura:String
)