package com.example.mycurrency.ui.theme

import android.content.Context
import android.widget.Toast

fun ShowError (errorText : String, context: Context){
    Toast.makeText(context,errorText,Toast.LENGTH_SHORT).show()
}