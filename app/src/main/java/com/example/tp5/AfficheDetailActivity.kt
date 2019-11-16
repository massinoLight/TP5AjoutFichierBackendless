package com.example.tp5


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.detail_affiche.*



class AfficheDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_VALIDER = "AjouterPersonne.valider"
        const val EXTRA_NOM = "AjouterPersonne.nom"
        const val EXTRA_EMAIL = "AjouterPersonne.email"
        const val EXTRA_TEL = "AjouterPersonne.tel"
        const val EXTRA_FAXE = "AjouterPersonne.fixe"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_affiche)
        val nomRess = intent.getStringExtra("NOM")
        val telRessu = intent.getStringExtra("TEL")
        val mailRessu = intent.getStringExtra("MAIL")
        val faxeRessu = intent.getStringExtra("FAXE")

        tv_nom.text="NOM: "+nomRess
        tv_tel.text="Mobile: "+telRessu
        tv_email.text="MAIL: "+mailRessu
        tv_fixe.text="FAXE: "+faxeRessu

    }
}