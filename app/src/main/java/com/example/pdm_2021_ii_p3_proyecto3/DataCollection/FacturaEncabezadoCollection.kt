package com.example.pdm_2021_ii_p3_proyecto3.DataCollection

class FacturaEncabezadoCollection : ArrayList<FacturaEncabezadoCollectionItem>()

data class FacturaEncabezadoCollectionItem(
    val idfactura:Long,
    val fechaemisionfactura: String,
    val idcai:Long,
    val idsucursal:Long,
    val totalfactura:Double,
    val idempleado:Long,
    val idcaso:Long
)