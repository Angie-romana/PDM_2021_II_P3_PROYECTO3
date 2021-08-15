package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.EmpleadoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.EmpleadoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abogado.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.apache.commons.codec.digest.DigestUtils

class AbogadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abogado)
        btnGuardarEmpleado.setOnClickListener { v-> callServicePostEmpleado() }
        btnActualizarEmpleado.setOnClickListener { v -> callServicePutPerson() }
    }


    private fun callServicePostEmpleado() {
        val claveSinEncriptar = txtClaveEmpleado.text.toString()
        val claveEncriptada = DigestUtils.md5Hex(claveSinEncriptar)
        val empleadoInfo = EmpleadoDataCollectionItem(
            idempleado = 0, // Este se llena solo para no dejar vacio. Es incremental
            nombreempleado = txtNombreEmpleado.text.toString(),
            apellidoempleado = txtApellidoEmpleado.text.toString(),
            dniempleado = txtDNIEmpleado.text.toString(),
            telefonoempleado = txtTelefonoEmpleado.text.toString().toLong(),
            direccionempleado = txtDireccionEmpleado.text.toString(),
            salarioempleado = txtSalarioEmpleado.text.toString().toDouble(),
            tipoempleado = txtTipoEmpleado.text.toString(),
            nombreusuario = txtUsuarioEmpleado.text.toString(),
            claveusuario = claveEncriptada
        )
        addEmpleado(empleadoInfo) {
            if (it?.idempleado != null) {
                Toast.makeText(this@AbogadoActivity,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@AbogadoActivity,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun addEmpleado(empleadoData: EmpleadoDataCollectionItem, onResult: (EmpleadoDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<EmpleadoDataCollectionItem> = retrofit.addEmpleado(empleadoData)
        result.enqueue(object : Callback<EmpleadoDataCollectionItem> {
            override fun onFailure(call: Call<EmpleadoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<EmpleadoDataCollectionItem>,
                                    response: Response<EmpleadoDataCollectionItem>
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


                    Toast.makeText(this@AbogadoActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@AbogadoActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }


    private fun callServicePutPerson() {
        val claveSinEncriptar = txtClaveEmpleado.text.toString()
        val claveEncriptada = DigestUtils.md5Hex(claveSinEncriptar)
        val empleadoInfo = EmpleadoDataCollectionItem(
            idempleado = 0,
            nombreempleado = txtNombreEmpleado.text.toString(),
            apellidoempleado = txtApellidoEmpleado.text.toString(),
            dniempleado = txtDNIEmpleado.text.toString(),
            telefonoempleado = txtTelefonoEmpleado.text.toString().toLong(),
            direccionempleado = txtDireccionEmpleado.text.toString(),
            salarioempleado = txtSalarioEmpleado.text.toString().toDouble(),
            tipoempleado = txtTipoEmpleado.text.toString(),
            nombreusuario = txtUsuarioEmpleado.text.toString(),
            claveusuario = claveEncriptada
        )

        val retrofit = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<EmpleadoDataCollectionItem> = retrofit.updateEmpleado(empleadoInfo)

        result.enqueue(object : Callback<EmpleadoDataCollectionItem> {
            override fun onFailure(call: Call<EmpleadoDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@AbogadoActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<EmpleadoDataCollectionItem>,
                                    response: Response<EmpleadoDataCollectionItem>) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@AbogadoActivity,"OK"+response.body()!!.nombreempleado,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@AbogadoActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@AbogadoActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        })
    }



}