package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.*
import com.example.pdm_2021_ii_p3_proyecto3.Service.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abogado.*
import kotlinx.android.synthetic.main.activity_cobro.*
import kotlinx.android.synthetic.main.activity_expediente.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CobroActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var idCliente = ArrayList<String>()
    var idDetalle = ArrayList<String>()
    var idCaso = ArrayList<String>()
    var arrayCai = ArrayList<String>()
    var idFactura:Long = 0
    var idEmpleado:Long = 0
    var contadorSpinnerCliente = 0
    var contadorSpinnerCaso = 0
    var totalFactura = 0.0
    var fechaHoy:String = ""
    var caiFinal:Long = 0
    var numFactura = 1
    var contador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro)
        desactivarSpinner()
        var intent = intent
        idEmpleado = intent.getSerializableExtra("idEmpleado") as Long
        llenarSpinnersPorDefecto()
        llenarSpinnerCliente()
        fechaHoy = capturarFecha()
        capturarCai()
        btnIniciarFactura.setOnClickListener {iniciar()}
        spiClientes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(contadorSpinnerCliente>0){
                    if(validarSpinnerClienteVacio()){
                        contadorSpinnerCliente++
                        return
                    }
                    llenarSpinnerCaso()
                }
                contadorSpinnerCliente++
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spnCaso.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(contadorSpinnerCaso>2){
                    if(validarSpinnerCasoVacio()){
                        contadorSpinnerCaso++
                        return
                    }
                    btnIniciarFactura.isEnabled = true
                }
                contadorSpinnerCaso++
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        btnCerrarFactura.setOnClickListener {
            restablecerPantalla()
        }

        lstServicios.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var idServicio: String = lstServicios.getItemAtPosition(position).toString()
                idServicio = idServicio.substring(0,1)
                var precio: String = lstServicios.getItemAtPosition(position).toString() //1
                val list = precio.split("|")
                val precioFinal = list[1]
                totalFactura = totalFactura + precioFinal.toDouble()
                callServicePostFacturaDetalle(idServicio.toLong())
                callServicePutFacturaEncabezado(idServicio.toLong(),totalFactura)
            }
        }

    }

    private fun callServicePutFacturaEncabezado(idEmpleado:Long, totalFactura:Double) {
        val facturaEncabezadoData = FacturaEncabezadoCollectionItem(
            idfactura =idFactura, //Se pone 0 porque es automatico y no se puede dejar vacio
            fechaemisionfactura = fechaHoy,
            idcai = caiFinal,
            idsucursal = 1, //Porque solo hay 1
            totalfactura = totalFactura,
            idempleado = idEmpleado,
            idcaso = spnCaso.selectedItem.toString().substring(0,1).toLong()
        )
        val retrofit = RestEngine.buildService().create(FacturaEncabezadoService::class.java)
        var result: Call<FacturaEncabezadoCollectionItem> = retrofit.updateFacturasEncabezado(facturaEncabezadoData)
        result.enqueue(object : Callback<FacturaEncabezadoCollectionItem> {
            override fun onFailure(call: Call<FacturaEncabezadoCollectionItem>, t: Throwable) {

            }
            override fun onResponse(call: Call<FacturaEncabezadoCollectionItem>,
                                    response: Response<FacturaEncabezadoCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@CobroActivity,"OK"+response.body()!!.totalfactura,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CobroActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CobroActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePostFacturaDetalle(idServicio:Long) {
        val facturaDetalleInfo = FacturaDetalleCollectionItem(
            iddetalle = 0, //Es automatico
            idfactura = idFactura,
            idservicio = idServicio,
            cantidadfactura = "1" //Siempre se manejara que se agrega un articulo por click
        )
        addFacturaDetalle(facturaDetalleInfo) {
            if (it?.iddetalle != null) {
                Toast.makeText(this,"Se ha añadido el servicio a la factura", Toast.LENGTH_LONG).show()
            } else {
            }
        }

    }

    fun addFacturaDetalle(facturaDetalleData: FacturaDetalleCollectionItem, onResult: (FacturaDetalleCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(FacturaDetalleService::class.java)
        var result: Call<FacturaDetalleCollectionItem> = retrofit.addFacturaDetalle(facturaDetalleData)
        result.enqueue(object : Callback<FacturaDetalleCollectionItem> {
            override fun onFailure(call: Call<FacturaDetalleCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<FacturaDetalleCollectionItem>,
                                    response: Response<FacturaDetalleCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedPerson = response.body()!!
                    onResult(addedPerson)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@CobroActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CobroActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun capturarIdFactura(){
        val facturaEncabezadoService:FacturaEncabezadoService = RestEngine.buildService().create(FacturaEncabezadoService::class.java)
        var result: Call<List<FacturaEncabezadoCollectionItem>> = facturaEncabezadoService.listFacturasEncabezados()

        result.enqueue(object :  Callback<List<FacturaEncabezadoCollectionItem>> {
            override fun onFailure(call: Call<List<FacturaEncabezadoCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CobroActivity,t.message,Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<List<FacturaEncabezadoCollectionItem>>,
                response: Response<List<FacturaEncabezadoCollectionItem>>
            ) {
                idFactura = response.body()!!.get(response.body()!!.size-1).idfactura
            }
        })
    }


    private fun capturarCai(){
        val caiService:CaiService = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<List<CaiDataCollectionItem>> = caiService.listCai()

        result.enqueue(object :  Callback<List<CaiDataCollectionItem>> {
            override fun onFailure(call: Call<List<CaiDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CobroActivity,t.message,Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CaiDataCollectionItem>>,
                response: Response<List<CaiDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    arrayCai.add(response.body()!!.get(i).idcai.toString() + "|" + response.body()!!.get(i).cai + "|" + response.body()!!.get(i).fechalimite + "|" + response.body()!!.get(i).rangoinicial + "|"
                            + response.body()!!.get(i).rangofinal)
                }
                for(array in arrayCai){
                    val list = array.split("|")
                    val idCai = list[0]
                    val cai = list[1]
                    val fechaLimite = list[2]
                    val rangoInicial = list[3]
                    val rangoFinal = list[4]
                    val format = SimpleDateFormat("yyyy-mm-dd")
                    val fecha = format.parse(fechaHoy)
                    val fechaLimiteFinal = format.parse(fechaLimite)
                    val list2 = rangoInicial.split("-")
                    val rangoInicialFinal = (list2[0]+list2[1]).toInt()
                    val list3 = rangoFinal.split("-")
                    val rangoFinalFinal = (list3[0]+list3[1]).toInt()
                    if(fecha < fechaLimiteFinal && numFactura >= rangoInicialFinal && numFactura <= rangoFinalFinal){
                         caiFinal = idCai.toLong()
                    }

                }
            }
        })

    }


    private fun callServicePostFacturaEncabezado() {
        val facturaEncabezadoData = FacturaEncabezadoCollectionItem(
            idfactura =0, //Se pone 0 porque es automatico y no se puede dejar vacio
            fechaemisionfactura = fechaHoy,
            idcai = caiFinal,
            idsucursal = 1, //Porque solo hay 1
            totalfactura = totalFactura,
            idempleado = idEmpleado,
            idcaso = spnCaso.selectedItem.toString().substring(0,1).toLong()
        )
        addFacturaEncabezado(facturaEncabezadoData) {
            if (it?.idfactura != null) {
                Toast.makeText(this,"OK", Toast.LENGTH_LONG).show()
            } else {

            }
        }
    }


    fun addFacturaEncabezado(facturaEncabezadoData: FacturaEncabezadoCollectionItem, onResult: (FacturaEncabezadoCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(FacturaEncabezadoService::class.java)
        var result: Call<FacturaEncabezadoCollectionItem> = retrofit.addFacturasEncabezado(facturaEncabezadoData)
        result.enqueue(object : Callback<FacturaEncabezadoCollectionItem> {
            override fun onFailure(call: Call<FacturaEncabezadoCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<FacturaEncabezadoCollectionItem>,
                                    response: Response<FacturaEncabezadoCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedFacturaEncabezado = response.body()!!
                    onResult(addedFacturaEncabezado)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)
                    Toast.makeText(this@CobroActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CobroActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun capturarFecha():String{
        val c = Calendar.getInstance()
        var año = c.get(Calendar.YEAR).toString()
        var mes = (c.get(Calendar.MONTH)+1)
        var mesFinal = (c.get(Calendar.MONTH)+1).toString()
        var dia = c.get(Calendar.DAY_OF_MONTH)
        var diaFinal = c.get(Calendar.DAY_OF_MONTH).toString()
        if(mes < 10){
            mesFinal = "0" + mes.toString()
        }
        if(dia < 10){
            diaFinal = "0" + dia.toString()
        }
        val fechaActual = año + "-" + mesFinal + "-"+diaFinal
        return fechaActual
    }

    /*fun capturarIdDetalle(){
        val facturaDetalleService:FacturaDetalleService = RestEngine.buildService().create(FacturaDetalleService::class.java)
        var result: Call<List<FacturaDetalleCollectionItem>> = facturaDetalleService.listFacturasDetalle()

        result.enqueue(object :  Callback<List<FacturaDetalleCollectionItem>> {
            override fun onFailure(call: Call<List<FacturaDetalleCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CobroActivity,t.message,Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<List<FacturaDetalleCollectionItem>>,
                response: Response<List<FacturaDetalleCollectionItem>>
            ) {
                Toast.makeText(this@CobroActivity,"Sirve",Toast.LENGTH_LONG).show()
                for(i in 0..(response.body()!!.size-1)){
                    idDetalle.add(response.body()!!.get(i).idfactura.toString() + "|" + response.body()!!.get(i).iddetalle.toString())
                    Toast.makeText(this@CobroActivity,idDetalle.size.toString(),Toast.LENGTH_LONG).show()
                }
            }
        })
    }*/

    private fun eliminarFactura(){
        if(contador==1){
            return
        }
        callServiceDeleteFacturaDetalle(idFactura)
        callServiceDeleteFacturaEncabezado(idFactura)
        Toast.makeText(this@CobroActivity,"Cobro cancelado exitosamente",Toast.LENGTH_LONG).show()
        restablecerPantalla()
    }

    private fun callServiceDeleteFacturaDetalle(idFactura: Long) {
        val facturaDetalle:FacturaDetalleService = RestEngine.buildService().create(FacturaDetalleService::class.java)
        var result: Call<ResponseBody> = facturaDetalle.deleteFacturaDetalle(idFactura)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CobroActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CobroActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CobroActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServiceDeleteFacturaEncabezado(idFactura: Long) {
        val facturaEncabezado:FacturaEncabezadoService = RestEngine.buildService().create(FacturaEncabezadoService::class.java)
        var result: Call<ResponseBody> = facturaEncabezado.deleteFacturasEncabezado(idFactura)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CobroActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CobroActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CobroActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun restablecerPantalla(){
        btnIniciarFactura.isEnabled = false
        btnCerrarFactura.isEnabled = false
        btnBorrar.isEnabled = false
        llenarSpinnersPorDefecto()
        llenarSpinnerCliente()
        fechaHoy = capturarFecha()
        capturarCai()
        desactivarSpinner()
        limpiarListView()
        contadorSpinnerCaso = 0
        contadorSpinnerCliente = 0
        totalFactura = 0.0
    }

    private fun limpiarListView(){
        val arrayAdapter: ArrayAdapter<*>
        array.clear()
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,array)
        lstServicios.adapter = arrayAdapter
        lstServicios.isEnabled = false
        Toast.makeText(this,"Factura cerrada exitosamente",Toast.LENGTH_LONG)
    }

    private fun validarSpinnerClienteVacio(): Boolean{
        if(spiClientes.selectedItem.equals("Seleccione el cliente")) {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_LONG)
                .show()
            spnCaso.setSelection(0)
            spnCaso.isEnabled = false
            btnIniciarFactura.isEnabled = false
            btnCerrarFactura.isEnabled = false
            limpiarListView()
            return true
        }
        return false
    }

    private fun validarSpinnerCasoVacio(): Boolean{
        if(spnCaso.selectedItem.equals("Seleccione el caso")) {
            Toast.makeText(this, "Debe seleccionar un caso", Toast.LENGTH_LONG)
                .show()
            btnCerrarFactura.isEnabled = false
            btnIniciarFactura.isEnabled = false
            limpiarListView()
            return true
        }
        return false
    }

    private fun desactivarSpinner(){
        spnCaso.isEnabled = false
    }

    private fun iniciar(){
        btnIniciarFactura.isEnabled = false
        btnBorrar.isEnabled = true
        btnCerrarFactura.isEnabled = true
        lstServicios.isEnabled = true
        llenarListView()
        callServicePostFacturaEncabezado()
        capturarIdFactura()
        btnBorrar.setOnClickListener { eliminarFactura() }
        contador++
    }

    private fun llenarSpinnersPorDefecto(){
        llenarSpinnerClientePorDefecto()
        llenarSpinnerCasoPorDefecto()
    }


    private fun llenarSpinnerClientePorDefecto(){
        val arrayAdapter: ArrayAdapter<*>
        idCliente.clear()
        idCliente.add("Seleccione el cliente")
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,idCliente)
        spiClientes.adapter = arrayAdapter

    }

    private fun llenarSpinnerCasoPorDefecto(){
        val arrayAdapter: ArrayAdapter<*>
        idCaso.clear()
        idCaso.add("Seleccione el caso")
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,idCaso)
        spnCaso.adapter = arrayAdapter
    }



    private fun llenarSpinnerCliente(){
        val arrayAdapter: ArrayAdapter<*>

        val clienteService: ClienteService = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<List<ClienteDataCollectionItem>> = clienteService.listClientes()

        result.enqueue(object : Callback<List<ClienteDataCollectionItem>> {
            override fun onFailure(call: Call<List<ClienteDataCollectionItem>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<List<ClienteDataCollectionItem>>,
                response: Response<List<ClienteDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    idCliente.add(response.body()!!.get(i).idcliente.toString() + ". " + response.body()!!.get(i).nombrecliente)
                }

            }
        })
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,idCliente)
        spiClientes.adapter = arrayAdapter
    }

    private fun llenarSpinnerCaso(){
        val arrayAdapter: ArrayAdapter<*>

        val casosService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var idCliente = spiClientes.selectedItem.toString().substring(0,1).toLong()
        var result: Call<List<CasoDataCollectionItem>> = casosService.listCaso()

        result.enqueue(object : Callback<List<CasoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CobroActivity,"Error en Caso",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoDataCollectionItem>>,
                response: Response<List<CasoDataCollectionItem>>
            ) {
                llenarSpinnerCasoPorDefecto()
                for(i in 0..(response.body()!!.size-1)){
                    if(response.body()!!.get(i).idcaso == idCliente){
                        idCaso.add(response.body()!!.get(i).idcaso.toString() + ". " + response.body()!!.get(i).sentenciacaso)
                    }
                }
            }
        })
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,idCaso)
        spnCaso.adapter = arrayAdapter
        spnCaso.isEnabled = true
    }

    private fun llenarListView(){
        val servicioService: ServicioService = RestEngine.buildService().create(
            ServicioService::class.java)
        var result: Call<List<ServicioDataCollectionItem>> = servicioService.listServicio()
        var arrayAdapter: ArrayAdapter<*>
        result.enqueue(object : Callback<List<ServicioDataCollectionItem>> {
            override fun onFailure(call: Call<List<ServicioDataCollectionItem>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<List<ServicioDataCollectionItem>>,
                response: Response<List<ServicioDataCollectionItem>>
            ) {
                arrayAdapter = ArrayAdapter(this@CobroActivity,android.R.layout.simple_list_item_1,array)
                lstServicios.adapter = arrayAdapter
                array.clear()
                for(i in 0..(response.body()!!.size-1)){
                    val precioHistoricoService: PrecioHistoricoService = RestEngine.buildService().create(
                        PrecioHistoricoService::class.java)
                    val idServicio = response.body()!!.get(i).idservicio.toString().toLong()
                    var result2: Call<PrecioHistoricoDataCollectionItem> = precioHistoricoService.getPrecioHistoricoByIdServicio(idServicio)
                    result2.enqueue(object : Callback<PrecioHistoricoDataCollectionItem>
                    {
                        override fun onResponse(
                            call: Call<PrecioHistoricoDataCollectionItem>,
                            response2: Response<PrecioHistoricoDataCollectionItem>
                        ) {
                            array.add(response.body()!!.get(i).idservicio.toString() + "." + response.body()!!.get(i).nombreservicio + "|" + response2.body()!!.precio)
                            arrayAdapter = ArrayAdapter(this@CobroActivity,android.R.layout.simple_list_item_1,array)
                            lstServicios.adapter = arrayAdapter
                        }
                        override fun onFailure(
                            call: Call<PrecioHistoricoDataCollectionItem>,
                            t: Throwable
                        ) {
                            TODO("Not yet implemented")
                        }
                    })
                }
        }
    })

    }

}