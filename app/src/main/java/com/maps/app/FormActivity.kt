package com.maps.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class FormActivity : AppCompatActivity(){
    private lateinit var lat:EditText;
    private lateinit var lon:EditText;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place);
        lon = findViewById(R.id.editTextTextPersonName);
        lat = findViewById(R.id.editTextTextPersonName2);
    }

    fun clickButton(view:View){
        var intent = Intent()
        intent.putExtra("data", arrayOf(lat.text.toString().toDouble(),lon.text.toString().toDouble()))
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

}