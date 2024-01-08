package com.example.newrealrealassessment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf

class FeatureChoice : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_choice)
        val nameLabel = findViewById<TextView>(R.id.tv1)
        val nameInput = findViewById<EditText>(R.id.et1)
        nameLabel.text = "Feature Input"



        val submit = findViewById<Button>(R.id.submitbtn)
        submit.setOnClickListener {
            val featureChoice = nameInput.text.toString()
            sendBackPointValues(featureChoice)
            val tv4 = findViewById<TextView>(R.id.tv4)
            tv4.text = "Submitted!"
        }


    }

    fun sendBackPointValues(featureChoice: String) {
        val intent = Intent()
        val bundle = bundleOf("featureChoice" to featureChoice)
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
    }

}