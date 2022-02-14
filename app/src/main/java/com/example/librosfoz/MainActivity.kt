package com.example.librosfoz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.librosfoz.dataclass.Errors
import com.example.librosfoz.extra.estaEnLinea
import com.example.librosfoz.extra.iniciarSesion
import com.example.librosfoz.extra.mensajeEmergente
import com.example.librosfoz.extra.validarSesion
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.qualifiedName
    private lateinit var til_correo: TextInputLayout
    private lateinit var tiet_correo: TextInputEditText
    private lateinit var til_contrasena: TextInputLayout
    private lateinit var tiet_contrasena: TextInputEditText
    private lateinit var btn_ingresar: Button
    private lateinit var pb_login: ProgressBar
    private lateinit var tv_registrate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        if (validarSesion(applicationContext)){
            lanzarActivity()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init(){
        til_correo = findViewById(R.id.til_correo)
        tiet_correo = findViewById(R.id.tiet_correo)
        til_contrasena = findViewById(R.id.til_contrasena)
        tiet_contrasena = findViewById(R.id.tiet_contrasena)
        btn_ingresar = findViewById(R.id.btn_ingresar)
       pb_login = findViewById(R.id.pb_login)
        btn_ingresar = findViewById(R.id.btn_ingresar)
        btn_ingresar.setOnClickListener {
            if (validarCorreo() && validarContrasena()) {
                realizarPeticion()
            }
        }
            tv_registrate = findViewById(R.id.tv_registrate)
            tv_registrate.setOnClickListener{
                startActivity(Intent(this,Registro::class.java))
            }
        }

    fun realizarPeticion(){
        VolleyLog.DEBUG = true
        if(estaEnLinea(applicationContext)){
            btn_ingresar.visibility = View.GONE
            pb_login.visibility = View.VISIBLE
            val cola = Volley.newRequestQueue(applicationContext)
            val json = JSONObject()
            json.put("email",tiet_correo.text.toString())
            json.put("password",tiet_contrasena.text.toString())
            json.put("device_name","User's phone")
            val peticion = object : JsonObjectRequest(Request.Method.POST,getString(R.string.url_servidor)+getString(R.string.api_login),json,
                {
                    response->
                    val jsonObject = JSONObject(response.toString())
                    iniciarSesion(applicationContext,jsonObject)
                    if (validarSesion(applicationContext)){
                        lanzarActivity()
                    }
                },
                {
                    error ->
                    btn_ingresar.visibility = View.VISIBLE
                    pb_login.visibility = View.GONE
                    val json = JSONObject(String(error.networkResponse.data, Charsets.UTF_8))
                    val errors = Json.decodeFromString<Errors>(json.toString())
                    for (error in errors.errors){
                        mensajeEmergente(this,error.detail)
                    }
                    Log.e(TAG,error.networkResponse.toString())
                    Log.e(TAG,error.toString())
                }
           ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] ="application/json"
                    headers["Content-type"] = "application/json"
                    return headers
                }
            }
            cola.add(peticion)
        }
        else{
            mensajeEmergente(this,getString(R.string.error_internet))
        }
    }

    fun lanzarActivity(){
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }

    private fun validarCorreo(): Boolean{
        return if(tiet_correo.text.toString().isEmpty()){
            til_correo.error = getString(R.string.campo_vacio)
            false
        }
        else{
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(tiet_correo.text.toString()).matches()){
                til_correo.isErrorEnabled = false
                true
            }
            else{
                til_correo.error = getString(R.string.error_correo)
                false
            }
        }
    }

   private fun validarContrasena(): Boolean{
       return if (tiet_contrasena.text.toString().isEmpty()){
           til_contrasena.error = getString(R.string.campo_vacio)
           false
       }
       else{
           til_contrasena.isErrorEnabled = false
           true
       }
   }
}