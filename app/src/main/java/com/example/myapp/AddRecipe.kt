package com.example.myapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.util.*


class AddRecipe : AppCompatActivity() {

    lateinit var mDatabase : DatabaseReference
    lateinit var radioGroup: RadioGroup
    private lateinit var recipeType : RadioButton
    lateinit var mainButton: RadioButton
    lateinit var recipeName : EditText
    lateinit var ingredient : EditText
    lateinit var step : EditText
    lateinit var addBtn : Button
    lateinit var cancelBtn : Button
    lateinit var imageView: ImageView
    lateinit var imageUri : Uri
    lateinit var storageReference : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        //initiate FirebaseDatabase by calling reference
        mDatabase = FirebaseDatabase.getInstance().reference.child("RecipeTypes")

        //initiate FirebaseStorage by calling reference
        storageReference = FirebaseStorage.getInstance().reference.child("Images").child(System.currentTimeMillis().toString())

        radioGroup = findViewById(R.id.radioGroup)
        recipeName = findViewById(R.id.recipe_text)
        ingredient = findViewById(R.id.ingredient_text)
        step = findViewById(R.id.step_text)
        addBtn = findViewById(R.id.add_bt)
        cancelBtn = findViewById(R.id.cancel_bt)
        mainButton = findViewById(R.id.main_bt)
        imageView = findViewById(R.id.recipe_Image)

        mainButton.isChecked = true

        //Image Choosing
        imageView.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 111)
        }

        //Add data to firebase
        addBtn.setOnClickListener {

            val intSelectButton: Int = radioGroup.checkedRadioButtonId
            recipeType = findViewById(intSelectButton)
            val recipeName = recipeName.text.toString()
            val recipeType = recipeType.text.toString()
            val ingredient = ingredient.text.toString()
            val step = step.text.toString()

            if((recipeName.isEmpty() || ingredient.isEmpty() || step.isEmpty() || recipeType.isEmpty())){
                Toast.makeText(this, "Please fill up all field given", Toast.LENGTH_SHORT).show()
            } else{
                val uploadTask = storageReference!!.putFile(imageUri)
                val task = uploadTask.continueWithTask {
                        task ->
                    if (!task.isSuccessful)
                    {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                    storageReference!!.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
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
                        Toast.makeText(this, "$recipeName Added", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                }
            }


        }

        //Cancel back to Main Activity
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