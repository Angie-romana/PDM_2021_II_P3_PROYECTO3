package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ClienteDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.ClienteService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cliente.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ClienteActivity : AppCompatActivity() {
    var array = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente)
        callServiceGetClientes()
        btnGuardarCliente.setOnClickListener { v-> callServicePostCliente() }
        btnActualizarCliente.setOnClickListener { v -> actualizarCliente(v) }
        btnBorrarCliente.setOnClickListener { v-> borrarCliente(v) }
    }
    private fun callServiceGetClientes() {
        val clienteService: ClienteService = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<List<ClienteDataCollectionItem>> = clienteService.listClientes()

        result.enqueue(object : Callback<List<ClienteDataCollectionItem>> {
            override fun onFailure(call: Call<List<ClienteDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@ClienteActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<ClienteDataCollectionItem>>,
                response: Response<List<ClienteDataCollectionItem>>
            ) {
                array.add("Todos los clientes")
                array.add("ID|Nombre|Apellido|DNI|RTN|Teléfono|Dirección")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idcliente.toString() + "|" + response.body()!!.get(i).nombrecliente + "|" + response.body()!!.get(i).apellidocliente + "|" + response.body()!!.get(i).dnicliente + "|"
                            + response.body()!!.get(i).rtncliente + "|" + response.body()!!.get(i).telefonocliente + "|" + response.body()!!.get(i).direccioncliente)
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@ClienteActivity,android.R.layout.simple_list_item_1,array)
                    lvwPrincipalCliente.adapter = arrayAdapter
                }

            }
        })
    }

    private fun callServicePostCliente() {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val clienteInfo = ClienteDataCollectionItem(
            idcliente = 0, // Este se pone asi porque es automatico
            nombrecliente = txtNombreCliente.text.toString(),
            apellidocliente = txtApellidoCliente.text.toString(),
            dnicliente = txtDNICliente.text.toString(),
            rtncliente = txtRTNCliente.text.toString(),
            telefonocliente = txtTelefonoCliente.text.toString(),
            direccioncliente = txtDireccionCliente.text.toString()
        )
        addCliente(clienteInfo) {
            if (it?.idcliente != null) {
                callServiceGetClientes()
                Toast.makeText(this,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }




    fun addCliente(clienteData: ClienteDataCollectionItem, onResult: (ClienteDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<ClienteDataCollectionItem> = retrofit.addCliente(clienteData)
        result.enqueue(object : Callback<ClienteDataCollectionItem> {
            override fun onFailure(call: Call<ClienteDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<ClienteDataCollectionItem>,
                                    response: Response<ClienteDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCliente = response.body()!!
                    onResult(addedCliente)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@ClienteActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ClienteActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun estaVacio():Boolean{
        if(txtNombreCliente.text.toString().isEmpty()) {
            txtNombreCliente.error ="Debe rellenar el nombre del cliente"
            return true
        }else if(txtApellidoCliente.text.toString().isEmpty()){
            txtApellidoCliente.error = "Debe rellenar el apellido del cliente"
            return true
        }
        if(txtDNICliente.text.toString().isEmpty()) {
            txtDNICliente.error ="Debe rellenar el DNI del cliente"
            return true
        }
        if(txtRTNCliente.text.toString().isEmpty()) {
            txtRTNCliente.error ="Debe rellenar el salario del cliente"
            return true
        }
        if(txtTelefonoCliente.text.toString().isEmpty()) {
            txtTelefonoCliente.error ="Debe rellenar el teléfono del cliente"
            return true
        }
        if(txtDireccionCliente.text.toString().isEmpty()) {
            txtDireccionCliente.error ="Debe rellenar la dirección del cliente"
            return true
        }
        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtNombreCliente.text.toString().length < 3) {
            txtNombreCliente.error ="El nombre no puede ser menor a 3 caracteres"
            return true
        }else if(txtApellidoCliente.text.toString().length < 3){
            txtApellidoCliente.error = "El apellido no puede ser menor a 3 caracteres"
            return true
        }
        if(txtDNICliente.text.toString().length != 15) {
            txtDNICliente.error ="El dni no puede ser distinto a 13 dígitos, no olvide ingresar los guiones"
            return true
        }
        if(txtRTNCliente.text.toString().length != 14) {
            txtRTNCliente.error ="El RTN del cliente no puede ser distinto a 14 dígitos"
            return true
        }
        if(txtTelefonoCliente.text.toString().length != 8) {
            txtTelefonoCliente.error ="El telefono no puede ser distinto a 8 dígitos"
            return true
        }
        if(txtDireccionCliente.text.toString().length < 10) {
            txtDireccionCliente.error ="La dirección del empleado no puede ser menor a 10 caracteres"
            return true
        }
        return false
    }


    fun actualizarCliente(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del cliente")
        builder.setMessage("Por favor ingrese el ID del cliente a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_cliente, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutCliente(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServicePutCliente(idCliente:Long) {

        val clienteInfo = ClienteDataCollectionItem(
            idcliente = idCliente,
            nombrecliente = txtNombreCliente.text.toString(),
            apellidocliente = txtApellidoCliente.text.toString(),
            dnicliente = txtDNICliente.text.toString(),
            rtncliente = txtRTNCliente.text.toString(),
            telefonocliente = txtTelefonoCliente.text.toString(),
            direccioncliente = txtDireccionCliente.text.toString()
        )

        val retrofit = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<ClienteDataCollectionItem> = retrofit.updateCliente(clienteInfo)
        callServiceGetClientes()
        result.enqueue(object : Callback<ClienteDataCollectionItem> {
            override fun onFailure(call: Call<ClienteDataCollectionItem>, t: Throwable) {

            }

            override fun onResponse(call: Call<ClienteDataCollectionItem>,
                                    response: Response<ClienteDataCollectionItem>) {
                if (response.isSuccessful) {
                    callServiceGetClientes()
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@ClienteActivity,"OK"+response.body()!!.nombrecliente,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@ClienteActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ClienteActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    fun borrarCliente(view:View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del cliente")
        builder.setMessage("Por favor ingrese el ID del cliente a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_cliente, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteCliente(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceDeleteCliente(idCliente: Long) {
        val clienteService:ClienteService = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<ResponseBody> = clienteService.deleteCliente(idCliente)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ClienteActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ClienteActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@ClienteActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@ClienteActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }



}