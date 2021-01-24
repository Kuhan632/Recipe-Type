package com.example.myapp

class Recipe(var recipeName: String, var ingredient: String, var step: String, var recipeType: String, var image: String) {

    constructor() : this("", "", "", "", ""){

    }

}