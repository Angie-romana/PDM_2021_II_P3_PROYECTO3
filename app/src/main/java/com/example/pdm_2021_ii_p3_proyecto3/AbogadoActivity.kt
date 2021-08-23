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
import com.example.pdm_2021_ii_p3_proyecto3.Service.EmpleadoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abogado.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.apache.commons.codec.digest.DigestUtils
import java.util.ArrayList


class AbogadoActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var tipoEmpleado = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abogado)
        llenarSpinner()
        callServiceGetEmpleados()
        btnGuardarEmpleado.setOnClickListener { v-> callServicePostEmpleado() }
        btnActualizarEmpleado.setOnClickListener { v -> actualizarEmpleado(v) }
        btnBorrarEmpleado.setOnClickListener { v-> borrarEmpleado(v) }

    }

    private fun llenarSpinner(){
        val arrayAdapter: ArrayAdapter<*>
        tipoEmpleado.add("Seleccione el tipo de empleado")
        tipoEmpleado.add("Empleado Administrativo")
        tipoEmpleado.add("Abogado")
        arrayAdapter = ArrayAdapter(this@AbogadoActivity,android.R.layout.simple_list_item_1,tipoEmpleado)
        spnTipoEmpleado.adapter = arrayAdapter
    }

    private fun callServiceGetEmpleados() {
        val personService:EmpleadoService = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<List<EmpleadoDataCollectionItem>> = personService.listEmpleados()

        result.enqueue(object :  Callback<List<EmpleadoDataCollectionItem>> {
            override fun onFailure(call: Call<List<EmpleadoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@AbogadoActivity,"Error",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<EmpleadoDataCollectionItem>>,
                response: Response<List<EmpleadoDataCollectionItem>>
            ) {
                array.add("Todos los empleados")
                array.add("ID|Nombre|Apellido|DNI|Teléfono|Direccion|Salario|Tipo|Nombre de Usuario|Clave de Usuario")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idempleado.toString() + "|" + response.body()!!.get(i).nombreempleado + "|" + response.body()!!.get(i).apellidoempleado + "|" + response.body()!!.get(i).dniempleado + "|"
                    + response.body()!!.get(i).telefonoempleado + "|" + response.body()!!.get(i).direccionempleado + "|" + response.body()!!.get(i).salarioempleado + "|"
                            + response.body()!!.get(i).tipoempleado + "|" + response.body()!!.get(i).nombreusuario + "|" + response.body()!!.get(i).claveusuario)
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@AbogadoActivity,android.R.layout.simple_list_item_1,array)
                    lvwPrincipal.adapter = arrayAdapter
                }

            }
        })
    }


    fun actualizarEmpleado(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Abogado")
        builder.setMessage("Por favor ingrese el ID del abogado a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_abogado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutEmpleado(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    fun borrarEmpleado(view:View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Abogado")
        builder.setMessage("Por favor ingrese el ID del abogado a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_abogado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteEmpleado(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun estaVacio():Boolean{
        if(txtNombreEmpleado.text.toString().isEmpty()) {
            txtNombreEmpleado.error ="Debe rellenar el nombre del empleado"
            return true
        }else if(txtApellidoEmpleado.text.toString().isEmpty()){
            txtApellidoEmpleado.error = "Debe rellenar el apellido del empleado"
            return true
        }
        if(txtDNIEmpleado.text.toString().isEmpty()) {
            txtDNIEmpleado.error ="Debe rellenar el DNI del empleado"
            return true
        }
        if(txtTelefonoEmpleado.text.toString().isEmpty()) {
            txtTelefonoEmpleado.error ="Debe rellenar el teléfono del empleado"
            return true
        }
        if(txtDireccionEmpleado.text.toString().isEmpty()) {
            txtDireccionEmpleado.error ="Debe rellenar la dirección del empleado"
            return true
        }
        if(txtSalarioEmpleado.text.toString().isEmpty()) {
            txtSalarioEmpleado.error ="Debe rellenar el salario del empleado"
            return true
        }
        if(spnTipoEmpleado.selectedItem.toString().equals("Seleccione el tipo de empleado")){
            Toast.makeText(this@AbogadoActivity,"Debe seleccionar un tipo de empleado",Toast.LENGTH_LONG).show()
            return true
        }

        if(txtUsuarioEmpleado.text.toString().isEmpty()) {
            txtUsuarioEmpleado.error ="Debe rellenar el usuario del empleado"
            return true
        }
        if(txtClaveEmpleado.text.toString().isEmpty()) {
            txtClaveEmpleado.error ="Debe rellenar la contraseña del empleado"
            return true
        }
        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtNombreEmpleado.text.toString().length < 3) {
            txtNombreEmpleado.error ="El nombre no puede ser menor a 3 caracteres"
            return true
        }else if(txtApellidoEmpleado.text.toString().length < 3){
            txtApellidoEmpleado.error = "El apellido no puede ser menor a 3 caracteres"
            return true
        }
        if(txtDNIEmpleado.text.toString().length != 15) {
            txtDNIEmpleado.error ="El dni no puede ser distinto a 13 dígitos, no olvide ingresar los guiones"
            return true
        }
        if(txtTelefonoEmpleado.text.toString().length != 8) {
            txtTelefonoEmpleado.error ="El telefono no puede ser distinto a 8 dígitos"
            return true
        }
        if(txtDireccionEmpleado.text.toString().length < 10) {
            txtDireccionEmpleado.error ="La dirección del empleado no puede ser menor a 10 caracteres"
            return true
        }
        if(txtSalarioEmpleado.text.toString().length < 4) {
            txtSalarioEmpleado.error ="El salario no puede ser menor a 4 dígitos"
            return true
        }
        if(txtUsuarioEmpleado.text.toString().length < 8) {
            txtUsuarioEmpleado.error ="El nombre de usuario no puede ser menor a 8 dígitos"
            return true
        }
        if(txtClaveEmpleado.text.toString().length < 8) {
            txtClaveEmpleado.error ="La contraseña no puede ser menor a 8 dígitos"
            return true
        }
        return false
    }

    private fun callServicePutEmpleado(idEmpleado:Long) {
        val claveSinEncriptar = txtClaveEmpleado.text.toString()
        val claveEncriptada = DigestUtils.md5Hex(claveSinEncriptar)
        val empleadoInfo = EmpleadoDataCollectionItem(
            idempleado = idEmpleado,
            nombreempleado = txtNombreEmpleado.text.toString(),
            apellidoempleado = txtApellidoEmpleado.text.toString(),
            dniempleado = txtDNIEmpleado.text.toString(),
            telefonoempleado = txtTelefonoEmpleado.text.toString().toLong(),
            direccionempleado = txtDireccionEmpleado.text.toString(),
            salarioempleado = txtSalarioEmpleado.text.toString().toDouble(),
            tipoempleado = spnTipoEmpleado.selectedItem.toString().substring(0,1),
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

    private fun callServicePostEmpleado() {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
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
            tipoempleado = spnTipoEmpleado.selectedItem.toString().substring(0,1),
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

    private fun callServiceDeleteEmpleado(idEmpleado: Long) {
        val empleadoService:EmpleadoService = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<ResponseBody> = empleadoService.deleteEmpleado(idEmpleado)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AbogadoActivity,"DELETE",Toast.LENGTH_LONG).show()
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