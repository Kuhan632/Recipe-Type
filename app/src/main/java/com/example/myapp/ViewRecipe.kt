package com.example.myapp

import android.R.string
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.net.URL


class ViewRecipe : AppCompatActivity() {

    lateinit var recipeName : EditText
    lateinit var ingredient : EditText
    lateinit var step : EditText
    lateinit var imageView : ImageView
    lateinit var mainView: RadioButton
    lateinit var cakeView: RadioButton
    lateinit var dessertView: RadioButton
    lateinit var recipeType : RadioButton
    lateinit var saveBtn : Button
    lateinit var cancelBtn : Button
    lateinit var mDatabase : DatabaseReference
    lateinit var radioGroup : RadioGroup
    lateinit var imageUri : Uri
    lateinit var storageReference : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe)

        mDatabase = FirebaseDatabase.getInstance().reference.child("RecipeTypes")
        storageReference = FirebaseStorage.getInstance().reference.child("Images").child(
            System.currentTimeMillis().toString()
        )

        recipeName = findViewById(R.id.recipe_view)
        ingredient = findViewById(R.id.ingredient_view)
        imageView = findViewById(R.id.recipe_Image)
        step = findViewById(R.id.step_view)
        mainView = findViewById(R.id.main_bt)
        cakeView = findViewById(R.id.cake_bt)
        dessertView = findViewById(R.id.dessert_bt)
        saveBtn = findViewById(R.id.save_bt)
        cancelBtn = findViewById(R.id.cancel_bt)
        radioGroup = findViewById(R.id.radioGroup)

        val name = intent.getStringExtra("name")
        val ing = intent.getStringExtra("ingredient")
        val stp = intent.getStringExtra("step")
        val typ = intent.getStringExtra("type")
        val img = intent.getStringExtra("image")

        recipeName.setText(name)
        ingredient.setText(ing)
        step.setText(stp)

        Picasso.get().load(img).into(imageView)
        val uri = Uri.parse(img)

        when (typ) {
            "Main" -> {
                mainView.isChecked = true
            }
            "Cake" -> {
                cakeView.isChecked = true
            }
            "Dessert" -> {
                dessertView.isChecked = true
            }
        }

        imageView.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 111)
        }

        saveBtn.setOnClickListener {

            val intSelectButton: Int = radioGroup.checkedRadioButtonId
            recipeType = findViewById(intSelectButton)
            val recipeName = recipeName.text.toString()
            val recipeType = recipeType.text.toString()
            val ingredient = ingredient.text.toString()
            val step = step.text.toString()

            if((recipeName.isEmpty() || ingredient.isEmpty() || step.isEmpty() || recipeType.isEmpty())){
                Toast.makeText(this, "Please fill up all field given", Toast.LENGTH_SHORT).show()
            } else {
                val uploadTask = storageReference!!.putFile(imageUri)
                val task = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                    storageReference!!.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        val image = downloadUri!!.toString()

                        mDatabase.child(recipeName).setValue(
                            Recipe(
                                recipeName,
                                ingredient,
                                step,
                                recipeType,
                                image
                            )
                        )
                        Toast.makeText(this, "$recipeName Updated", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                }
            }
        }

        cancelBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!

            Picasso.get().load(imageUri)
            imageView.setImageURI(imageUri)

        }
    }








}