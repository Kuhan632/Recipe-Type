package com.example.myapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.TestLooperManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase


class MyAdapter(val mCtx: Context, val layoutResId: Int, val recipeList: List<Recipe>) :
        ArrayAdapter<Recipe>(mCtx, layoutResId, recipeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view : View = layoutInflater.inflate(layoutResId, null)

        val textViewRecipe = view.findViewById<TextView>(R.id.recipe_name)
        val textViewType = view.findViewById<TextView>(R.id.recipe_type)
        val viewBtn = view.findViewById<TextView>(R.id.view_btn)
        val delBtn = view.findViewById<TextView>(R.id.delete_btn)

        val recipe = recipeList[position]

        textViewRecipe.text = recipe.recipeName
        textViewType.text = recipe.recipeType

        //Send data to View Activity
        viewBtn.setOnClickListener{

            val intent = Intent(mCtx, ViewRecipe::class.java)
            intent.putExtra("name", recipe.recipeName)
            intent.putExtra("ingredient", recipe.ingredient)
            intent.putExtra("step", recipe.step)
            intent.putExtra("type", recipe.recipeType)
            intent.putExtra("image", recipe.image)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mCtx.startActivity(intent)
        }

        //Delete data
        delBtn.setOnClickListener {
            val mDatabase = FirebaseDatabase.getInstance().reference.child("RecipeTypes")
            mDatabase.child(recipe.recipeName).removeValue()
            Toast.makeText(mCtx,"${recipe.recipeName} Deleted",Toast.LENGTH_LONG).show()
        }

        return view
    }

}