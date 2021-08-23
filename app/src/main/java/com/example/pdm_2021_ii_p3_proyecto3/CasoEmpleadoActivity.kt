package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.*
import com.example.pdm_2021_ii_p3_proyecto3.Service.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_caso.*
import kotlinx.android.synthetic.main.activity_caso_empleado.*
import kotlinx.android.synthetic.main.activity_cobro.*
import kotlinx.android.synthetic.main.activity_precio_historico.*
import kotlinx.android.synthetic.main.activity_sucursal.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CasoEmpleadoActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var servicioElegir = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caso_empleado)
        callServiceGetCaso()
        callServiceGetCasoEmpleado()
        callServiceGetEmpleado()
        btnGuardarCasoEmpleado.setOnClickListener { v -> callServicePostCasoEmpleado() }
        btnActualizarCasoEmpleado.setOnClickListener { v -> actualizarCasoEmpleado(v) }
        btnEliminarCasoEmpleado.setOnClickListener { v -> borrarCasoEmpleado(v) }
    }
private fun callServiceGetEmpleado()
{
    val empleadoService: EmpleadoService = RestEngine.buildService().create(EmpleadoService::class.java)
    var result: Call<List<EmpleadoDataCollectionItem>> = empleadoService.listEmpleados()

    result.enqueue(object :  Callback<List<EmpleadoDataCollectionItem>> {
        override fun onFailure(call: Call<List<EmpleadoDataCollectionItem>>, t: Throwable) {
            Toast.makeText(this@CasoEmpleadoActivity,"Error",Toast.LENGTH_LONG).show()
        }

        override fun onResponse(
            call: Call<List<EmpleadoDataCollectionItem>>,
            response: Response<List<EmpleadoDataCollectionItem>>
        ) {
            servicioElegir.add("Seleccione el Empleado")
            for(i in 0..(response.body()!!.size-1)){
                servicioElegir.add(response.body()!!.get(i).idempleado.toString() + "|" + response.body()!!.get(i).nombreempleado)
                val arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(this@CasoEmpleadoActivity,android.R.layout.simple_list_item_1,servicioElegir)
                spnIdEmpleado.adapter = arrayAdapter
            }



        }
    })
}
    private fun callServiceGetCasoEmpleado() {
        val casoempleadoService: CasoEmpleadoService =
            RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<List<CasoEmpleadoDataCollectionItem>> =
            casoempleadoService.listCasoEmpleado()

        result.enqueue(object : Callback<List<CasoEmpleadoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoEmpleadoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CasoEmpleadoActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoEmpleadoDataCollectionItem>>,
                response: Response<List<CasoEmpleadoDataCollectionItem>>
            ) {
                array.add("Todos los casos")
                array.add("ID|ID Empleado|ID Caso|Fecha Inicio|Fecha Final|Descripcion")
                for (i in 0..(response.body()!!.size - 1)) {
                    array.add(
                        response.body()!!.get(i).idcasoempleado.toString() + "|" +response.body()!!.get(i).idempleado.toString()+ "|"
                                +response.body()!!.get(i).idcaso.toString() +"|"+ response.body()!!
                            .get(i).fechainiciotrabajoencaso + "|"
                                + response.body()!!.get(i).fechafinaltrabajoencaso + "|"
                            + response.body()!!.get(i).descripcioncasoempleado
                    )
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(
                        this@CasoEmpleadoActivity,
                        android.R.layout.simple_list_item_1,
                        array
                    )
                    lvwCasoEmpleado.adapter = arrayAdapter
                }

            }
        })
    }


        private fun callServiceGetCaso() {
        val casoService: CasoService = RestEngine.buildService().create(CasoService::class.java)
        var result: Call<List<CasoDataCollectionItem>> = casoService.listCaso()

        result.enqueue(object :  Callback<List<CasoDataCollectionItem>> {
            override fun onFailure(call: Call<List<CasoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CasoEmpleadoActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CasoDataCollectionItem>>,
                response: Response<List<CasoDataCollectionItem>>
            ) {
                servicioElegir.add("Seleccione el id del caso")
                for(i in 0..(response.body()!!.size-1)){
                    servicioElegir.add(response.body()!!.get(i).idcaso.toString() )
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@CasoEmpleadoActivity,android.R.layout.simple_list_item_1,servicioElegir)
                    spnIdCaso.adapter = arrayAdapter
                }

            }
        })
    }
        private fun callServicePostCasoEmpleado() {

            val casoempleadoInfo = CasoEmpleadoDataCollectionItem(
                idcaso = 0, // Este se pone asi porque es automatico
              fechafinaltrabajoencaso = txtFechaFinalT.text.toString(),
                fechainiciotrabajoencaso = txtFechaInicioT.text.toString(),
                        idempleado = spnIdEmpleado.selectedItemId.toString().substring(0,1).toLong(),
                descripcioncasoempleado = txtDescripCasoE.text.toString(),
                idcasoempleado = txtIdCasoEmpleado.text.toString().toLong(),

            )
            addCasoEmpleado(casoempleadoInfo) {
                if (it?.idcaso != null) {
                    callServiceGetCasoEmpleado()
                    Toast.makeText(this,"OK", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    fun addCasoEmpleado(casoempleadoData: CasoEmpleadoDataCollectionItem, onResult: (CasoEmpleadoDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<CasoEmpleadoDataCollectionItem> = retrofit.addCasoEmpleado(casoempleadoData)
        result.enqueue(object : Callback<CasoEmpleadoDataCollectionItem> {
            override fun onFailure(call: Call<CasoEmpleadoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<CasoEmpleadoDataCollectionItem>,
                                    response: Response<CasoEmpleadoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCasoEmpleado = response.body()!!
                    onResult(addedCasoEmpleado)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@CasoEmpleadoActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CasoEmpleadoActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun estaVacio():Boolean{
        if(spnIdCaso.selectedItem.equals("Seleccione el id del caso")) {
            Toast.makeText(this, "Debe seleccionar un id caso", Toast.LENGTH_LONG)
                .show()

        }else if(txtFechaInicioT.text.toString().isEmpty()){
            txtFechaInicioT.error = "Debe rellenar la fecha"
            return true
        }
        if(txtFechaFinalT.text.toString().isEmpty())
        {
            txtFechaFinalT.error ="Debe rellenar la fecha "
        }

        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtFechaInicioT.text.toString().length < 3){
            txtFechaInicioT.error = "La fecha no puede ser menor a 3 caracteres"
            return true
        }
        if(txtFechaFinalT.text.toString().length <3) {
            txtFechaFinalT.error ="La fecha no puede ser menor a 3 caracteres"
            return true
        }

        return false
    }


    fun actualizarCasoEmpleado(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del caso")
        builder.setMessage("Por favor ingrese el ID del caso a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_caso_empleado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutCasoEmpleado(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }
    private fun callServicePutCasoEmpleado(idCaso:Long) {

        val casoempleadoInfo = CasoEmpleadoDataCollectionItem(
            idcaso = spnIdCaso.selectedItemId.toString().substring(0,1).toLong(),
            fechafinaltrabajoencaso = txtFechaFinalT.text.toString(),
            fechainiciotrabajoencaso = txtFechaInicioT.text.toString(),
            idcasoempleado = txtIdCasoEmpleado.text.toString().toLong(),
            idempleado = spnIdEmpleado.selectedItemId.toString().substring(0,1).toLong(),
            descripcioncasoempleado = txtDescripCasoE.text.toString()
        )

        val retrofit = RestEngine.buildService().create(CasoEmpleadoService::class.java)
        var result: Call<CasoEmpleadoDataCollectionItem> = retrofit.updateCasoEmpleado(casoempleadoInfo)
        callServiceGetCasoEmpleado()
        result.enqueue(object : Callback<CasoEmpleadoDataCollectionItem> {
            override fun onFailure(call: Call<CasoEmpleadoDataCollectionItem>, t: Throwable) {

            }

            override fun onResponse(call: Call<CasoEmpleadoDataCollectionItem>,
                                    response: Response<CasoEmpleadoDataCollectionItem>) {
                if (response.isSuccessful) {
                    callServiceGetCasoEmpleado()
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@CasoEmpleadoActivity,"OK"+response.body()!!.idcaso,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CasoEmpleadoActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CasoEmpleadoActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    fun borrarCasoEmpleado(view:View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del caso")
        builder.setMessage("Por favor ingrese el ID del caso a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_caso_empleado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteCasoEmpleado(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }
//
    private fun callServiceDeleteCasoEmpleado(idCaso: Long) {
    val casoempleadoService: CasoEmpleadoService =
        RestEngine.buildService().create(CasoEmpleadoService::class.java)
    var result: Call<ResponseBody> = casoempleadoService.deleteCasoEmpleado(idCaso)

    result.enqueue(object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Toast.makeText(this@CasoEmpleadoActivity, "Error", Toast.LENGTH_LONG).show()
        }

        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {
            if (response.isSuccessful) {
                Toast.makeText(this@CasoEmpleadoActivity, "DELETE", Toast.LENGTH_LONG).show()
            } else if (response.code() == 401) {
                Toast.makeText(this@CasoEmpleadoActivity, "Sesion expirada", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(
                    this@CasoEmpleadoActivity,
                    "Fallo al traer el item",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    })
}

}

//



