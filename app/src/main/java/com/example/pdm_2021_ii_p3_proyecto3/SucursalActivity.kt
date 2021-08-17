package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.SucursalDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.example.pdm_2021_ii_p3_proyecto3.Service.ServicioService
import com.example.pdm_2021_ii_p3_proyecto3.Service.SucursalService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_servicio.*
import kotlinx.android.synthetic.main.activity_sucursal.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SucursalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sucursal)
        btnGuardarSucursal.setOnClickListener { v-> callServicePostSucursal() }
        btnActualizarSucursal.setOnClickListener {  }
        btnEliminarSucursal.setOnClickListener { v-> borrarSucursal(v) }
    }


    private fun callServicePostSucursal() {

        val  sucursalInfo = SucursalDataCollectionItem(
            idsucursal = 0, // Este se llena solo para no dejar vacio. Es incremental
            nombresucursal = txtNombreSucursal.text.toString(),
            dirreccionsucursal = txtDireccionSucursal.text.toString(),
            telefonosucursal = txtDireccionSucursal.text.toString().toLong(),
            emailsucursal = txtcorreo.text.toString()
            )
        addSucursal(sucursalInfo) {
            if (it?.idsucursal != null) {
                Toast.makeText(this@SucursalActivity,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@SucursalActivity,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }




    fun addSucursal(sucursalData: SucursalDataCollectionItem, onResult: (SucursalDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(SucursalService::class.java)
        var result: Call<SucursalDataCollectionItem> = retrofit.addSucursal(sucursalData)
        result.enqueue(object : Callback<SucursalDataCollectionItem> {
            override fun onFailure(call: Call<SucursalDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<SucursalDataCollectionItem>,
                                    response: Response<SucursalDataCollectionItem>
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


                    Toast.makeText(this@SucursalActivity,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@SucursalActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }
    fun borrarSucursal(view: View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Sucursal")
        builder.setMessage("Por favor ingrese el ID del Sucursal a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_abogado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteSucursal(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceDeleteSucursal(idSucursal: Long) {
        val sucursalService: SucursalService = RestEngine.buildService().create(SucursalService::class.java)
        var result: Call<ResponseBody> = sucursalService.deleteSucursal(idSucursal)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SucursalActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SucursalActivity,"DELETE", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@SucursalActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@SucursalActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
}