package com.example.librosfoz

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.librosfoz.adaptadores.AdaptadorBooks
import com.example.librosfoz.dataclass.Books
import com.example.librosfoz.extra.eliminarSesion
import com.example.librosfoz.extra.estaEnLinea
import com.example.librosfoz.extra.mensajeEmergente
import com.example.librosfoz.extra.obtenerDeSesion
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import com.example.librosfoz.dataclass.Errors as Errors

class Home : AppCompatActivity() {
    private val TAG = Home::class.qualifiedName
    private lateinit var tv_cerrarsesion: TextView
    private lateinit var rv_books: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
    }

    fun init(){
        rv_books = findViewById(R.id.rv_books)
        tv_cerrarsesion = findViewById(R.id.tv_cerrarsesion)
        tv_cerrarsesion.setOnClickListener {
            val cola = Volley.newRequestQueue(applicationContext)
            val peticion = object: StringRequest(Request.Method.POST,getString(R.string.url_servidor)+getString(R.string.api_logout),{
                response ->
                Log.d(TAG,"Todo salio bien")
                eliminarSesion(applicationContext)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },{
                    error ->
                Log.e(TAG,error.toString())
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Authorization"] = "Bearer ${obtenerDeSesion(applicationContext,"token")}"
                    return headers
                }
            }
            cola.add(peticion)
        }
        Log.d(TAG,"token: ${obtenerDeSesion(applicationContext,"token")}")
    }

        override fun onResume() {
        super.onResume()
        realizarPeticion()
        }

    fun realizarPeticion(){
        if(estaEnLinea(applicationContext)){
            val cola = Volley.newRequestQueue(applicationContext)
            val peticion =object: JsonObjectRequest(Request.Method.GET,getString(R.string.url_servidor)+getString(R.string.api_libros),null,{
                    response ->
                Log.d(TAG,response.toString())
                val books = Json.decodeFromString<Books>(response.toString())
                val adaptador = AdaptadorBooks(this,books.data)
                rv_books.layoutManager = LinearLayoutManager(this)
                rv_books.adapter = adaptador
                adaptador.notifyDataSetChanged()
               rv_books.visibility = View.VISIBLE
            },{
                    error ->
                if(error.networkResponse.statusCode == 401){
                    eliminarSesion(applicationContext)
                    val intent = Intent(this,MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                else{
                    val json = JSONObject(String(error.networkResponse.data, Charsets.UTF_8))
                    val errors = Json.decodeFromString<Errors>(json.toString())
                    for (error in errors.errors){
                        mensajeEmergente(this,error.detail)
                    }
                    rv_books.visibility = View.GONE

            }
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Authorization"] = "Bearer ${obtenerDeSesion(applicationContext,"token")}"
                    headers["Accept"] = "application/json"
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
}

