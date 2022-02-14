package com.example.librosfoz.adaptadores

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.librosfoz.Detalles
import com.example.librosfoz.R
import com.example.librosfoz.dataclass.Book

class AdaptadorBooks(val activity: Activity,val books: MutableList<Book>): RecyclerView.Adapter<AdaptadorBooks.BookHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookHolder(view)
    }
    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        val book = books.get(position)
        with(holder) {
            cv_detalles
                .setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("book", book)
                val intent = Intent(activity,Detalles::class.java)
                intent.putExtras(bundle)
                activity.startActivity(intent)
            }
            tv_title.text = book.attributes.title
            tv_autor.text = book.relationships.authors.links.self
            tv_categorias.text = book.relationships.categories.links.self


        }
    }

    override fun getItemCount(): Int = books.size

    class BookHolder(view: View): RecyclerView.ViewHolder(view){
        val tv_title: TextView = view.findViewById(R.id.tv_title)
        val tv_autor: TextView = view.findViewById(R.id.tv_autor)
        val tv_categorias: TextView = view.findViewById(R.id.yv_categoria)
        val cv_detalles: CardView = view.findViewById(R.id.cv_detalles)


    }
}