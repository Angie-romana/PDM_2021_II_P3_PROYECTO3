package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.EmpleadoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.SucursalDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.Service.EmpleadoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.example.pdm_2021_ii_p3_proyecto3.Service.SucursalService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abogado.*
import kotlinx.android.synthetic.main.activity_servicio.*
import kotlinx.android.synthetic.main.activity_sucursal.*
import okhttp3.ResponseBody
import org.apache.commons.codec.digest.DigestUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class SucursalActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var tipoEmpleado = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sucursal)
        callServiceGetSucursal()
        btnGuardarSucursal.setOnClickListener { v -> callServicePostSucursal() }
        btnActualizarSucursal.setOnClickListener {v -> actualizarSucursal(v) }
        btnEliminarSucursal.setOnClickListener { v -> borrarSucursal(v) }
    }
    private fun callServiceGetSucursal() {
        val personService: SucursalService = RestEngine.buildService().create(SucursalService::class.java)
        var result: Call<List<SucursalDataCollectionItem>> = personService.listSucursal()

        result.enqueue(object :  Callback<List<SucursalDataCollectionItem>> {
            override fun onFailure(call: Call<List<SucursalDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@SucursalActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<SucursalDataCollectionItem>>,
                response: Response<List<SucursalDataCollectionItem>>
            ) {
                array.add("Todas las Sucursales")
                array.add("ID|Nombre Sucursal|Direcciòn|E-mail")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idsucursal.toString() + "|" + response.body()!!.get(i).nombresucursal + "|" + response.body()!!.get(i).dirreccionsucursal + "|" + response.body()!!.get(i).telefonosucursal + "|"
                            + response.body()!!.get(i).emailsucursal )
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@SucursalActivity,android.R.layout.simple_list_item_1,array)
                    lvwPrincipal.adapter = arrayAdapter
                }

            }
        })
    }


    fun actualizarSucursal(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id de la sucursal")
        builder.setMessage("Por favor ingrese el ID de la sucursal a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_sucursal, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutSucursal(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
        callServiceGetSucursal()
    }

    private fun callServicePutSucursal(idSucursal:Long) {

        val sucursalInfo = SucursalDataCollectionItem(
            idsucursal = idSucursal,
            nombresucursal = txtNombreSucursal.text.toString(),
            dirreccionsucursal = txtDireccionSucursal.text.toString(),
            telefonosucursal =  txtTelefonoSucursal.text.toString().toLong(),
            emailsucursal = txtcorreo.text.toString()

        )

        val retrofit = RestEngine.buildService().create(SucursalService::class.java)
        var result: Call<SucursalDataCollectionItem> = retrofit.updateSucursal(sucursalInfo)
        result.enqueue(object : Callback<SucursalDataCollectionItem> {
            override fun onFailure(call: Call<SucursalDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@SucursalActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<SucursalDataCollectionItem>,
                                    response: Response<SucursalDataCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@SucursalActivity,"OK"+response.body()!!.nombresucursal,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@SucursalActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@SucursalActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }



    private fun callServicePostSucursal() {

        val sucursalInfo = SucursalDataCollectionItem(
            idsucursal = 0, // Este se llena solo para no dejar vacio. Es incremental
            nombresucursal = txtNombreSucursal.text.toString(),
            dirreccionsucursal = txtDireccionSucursal.text.toString(),
            telefonosucursal = txtDireccionSucursal.text.toString().toLong(),
            emailsucursal = txtcorreo.text.toString()
        )
        addSucursal(sucursalInfo) {
            if (it?.idsucursal != null) {
                Toast.makeText(this@SucursalActivity, "OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@SucursalActivity, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun addSucursal(
        sucursalData: SucursalDataCollectionItem,
        onResult: (SucursalDataCollectionItem?) -> Unit
    ) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(SucursalService::class.java)
        var result: Call<SucursalDataCollectionItem> = retrofit.addSucursal(sucursalData)
        result.enqueue(object : Callback<SucursalDataCollectionItem> {
            override fun onFailure(call: Call<SucursalDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(
                call: Call<SucursalDataCollectionItem>,
                response: Response<SucursalDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedPerson = response.body()!!
                    onResult(addedPerson)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500) {
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse =
                        Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(
                        this@SucursalActivity,
                        errorResponse.errorDetails,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@SucursalActivity,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
        )
    }

    fun borrarSucursal(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Sucursal")
        builder.setMessage("Por favor ingrese el ID del Sucursal a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_sucursal, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if (editText.text.toString() == "") {
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteSucursal(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar") { dialogInterface, i -> return@setNegativeButton }
        builder.show()
    }

    private fun callServiceDeleteSucursal(idSucursal: Long) {
        val sucursalService: SucursalService =
            RestEngine.buildService().create(SucursalService::class.java)
        var result: Call<ResponseBody> = sucursalService.deleteSucursal(idSucursal)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SucursalActivity, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SucursalActivity, "DELETE", Toast.LENGTH_LONG).show()
                } else if (response.code() == 401) {
                    Toast.makeText(this@SucursalActivity, "Sesion expirada", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@SucursalActivity,
                        "Fallo al traer el item",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }
    private fun estaVacio():Boolean{
        if(txtNombreSucursal.text.toString().isEmpty()) {
            txtNombreSucursal.error ="Debe rellenar el nombre "
            return true
        }else if(txtDireccionSucursal.text.toString().isEmpty()){
            txtDireccionSucursal.error = "Debe rellenar la direcciòn"
            return true
        }
        if(txtTelefonoSucursal.text.toString().isEmpty()) {
            txtTelefonoSucursal.error ="Debe rellenar el telèfono"
            return true
        }
        if(txtcorreo.text.toString().isEmpty()) {
            txtcorreo.error ="Debe rellenar el correo"
            return true
        }

        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtNombreSucursal.text.toString().length < 7) {
            txtNombreSucursal.error ="El nombre no puede ser menor a 7 caracteres"
            return true
        }else if(txtDireccionSucursal.text.toString().length < 10){
            txtDireccionSucursal.error = "La dirrecciòn no puede ser menor a 10 caracteres"
            return true
        }
        if(txtTelefonoSucursal.text.toString().length != 8) {
            txtTelefonoSucursal.error ="El telefono no puede ser distinto a 8 dígitos"
            return true
        }
        if(txtcorreo.text.toString().length < 10) {
            txtcorreo.error ="El correo no puede ser menor a 10 caracteres"
            return true
        }

        return false
    }
}