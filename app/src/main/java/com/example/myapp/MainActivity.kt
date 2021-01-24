package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    lateinit var spinner : Spinner
    lateinit var mDatabase : DatabaseReference
    lateinit var addButton : Button
    lateinit var listView: ListView
    lateinit var recipeList : MutableList<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButton = findViewById(R.id.addBtn)
        spinner = findViewById(R.id.spinner)
        listView = findViewById(R.id.listView)

        recipeList = mutableListOf()

        //initiate FirebaseDatabase by calling reference
        mDatabase = FirebaseDatabase.getInstance().reference.child("RecipeTypes")

        val recipeType = arrayOf("All", "Main", "Cake", "Dessert")
        val arrayAdapter = ArrayAdapter(this, R.layout.style_spinner, recipeType)

        //Spinner
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinnerValue = spinner.selectedItem.toString()
                if (spinnerValue == "All"){
                    retrieveData()
                } else {
                    mDatabase.orderByChild("recipeType").equalTo(spinnerValue).addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    recipeList.clear()
                                    for (h in snapshot.children) {
                                        val recipe = h.getValue(Recipe::class.java)
                                        recipeList.add(recipe!!)
                                    }

                                    val adapter = MyAdapter(
                                        applicationContext,
                                        R.layout.list_layout,
                                        recipeList
                                    )
                                    listView.adapter = adapter
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //Intent to Add Activity
        addButton.setOnClickListener {
            val intent = Intent(this, AddRecipe::class.java)
            startActivity(intent)
            finish()
        }

        // Retrieve data when open the app
        retrieveData()
    }

    private fun retrieveData() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    recipeList.clear()
                    for (h in snapshot.children) {
                        val recipe = h.getValue(Recipe::class.java)
                        recipeList.add(recipe!!)
                    }

                    val adapter = MyAdapter(applicationContext, R.layout.list_layout, recipeList)
                    listView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}