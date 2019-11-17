package com.example.tp5

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.style_dune_ligne.view.*


data class Personne(val nom:String,val email:String,val tel:String,val fixe:String,val imgUri: Uri?)

class PersonneAdapter(val personneAAfficher: Array<Personne>, val listener: (Personne)-> Unit):
    RecyclerView.Adapter<PersonneAdapter.ViewHolder>() {


    //creer un elements ViewHolder du recuclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonneAdapter.ViewHolder {
        val uneLigneView=LayoutInflater.from(parent.context).inflate(R.layout.style_dune_ligne,parent,false)
        return ViewHolder(uneLigneView)
    }


    //charger le contenu d un objet personne dans un element du recyclerview
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.i("XXXX","onBindViewHolder")

        //on fournit l'objet personne a afficher et l'action a effectuer lors du clic sur l'element
        holder.bind(personneAAfficher[position],listener)

    }


    //dans le cas ou l'on souhaite avoir le nombre d'objet personne fourni
    override fun getItemCount()=personneAAfficher.size

    class ViewHolder(val elmtDeListe:View):RecyclerView.ViewHolder(elmtDeListe)
    {

        //cette fonction permet de charger les donnees dans l element du recyview
        fun bind(personne:Personne,listener: (Personne) -> Unit)= with(itemView)
        {
            android.util.Log.i("XXXX","FCT Bind ")
            //remplissage de la partie nom
            itemView.tv_name.text=personne.nom
            itemView.tv_tel.text= personne.tel
            itemView.image.setImageURI(personne.imgUri)

            //l'action a realiser lors du clic  sur un element
            setOnClickListener{(listener(personne))}
        }



    }




}