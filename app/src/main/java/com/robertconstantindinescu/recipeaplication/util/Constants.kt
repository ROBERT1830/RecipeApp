package com.robertconstantindinescu.recipeaplication.util



/**
 * Clase utilizada para crear constantes que suaremos con retrofit como la url base.
 * Se utiliza un companion object par apoder acceder a estas constantes desde todos los sitios de la
 * app.
 */
class Constants {
    companion object{

        const val BASE_URL = "https://api.spoonacular.com"
        const val BASE_IMAGE_URL ="https://spoonacular.com/cdn/ingredients_100x100/"
        const val API_KEY = "fc9a88b126ac432786da2b6181c073d5"

        const val RECIPE_RESULT_KEY = "recipeBundle"

        //Api Query keys TO USE WITH THE VALUES IN RECIPESVIEWMODEL CLASS
        const val QUERY_SEARCH = "query"
        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"
        const val QUERY_INGREDIENTS ="ingredients"
        const val QUERY_INFORMATION = "information"


        //ROOM DATABASE
        const val DATABASE_NAME = "recipes_database"
        const val RECIPES_TABLE = "recipes_table"
        const val FAVORITE_RECIPES_TABLE = "favorite_recipes_table"
        const val PERSONALIZED_RECIPES_TABLE = "personalized_recipes_table"

        //Bottom Sheet and data store preferences
        const val DEFAULT_RECIPES_NUMBER = "50"
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"

        const val PREFERENCES_NAME = "foody_preferences"
        const val PREFERENCES_MEAL_TYPE = "mealType"
        const val PREFERENCES_MEAL_TYPE_ID = "mealTypeId"
        const val PREFERENCES_DIET_TYPE = "dietType"
        const val PREFERENCES_DIET_TYPE_ID = "dietTypeId"


        //Bottom Sheet for ingredients
        //Default types
        const val DEFAULT_MEAT_TYPE = "pork"
        const val DEFAULT_VEGETABLE_TYPE = "potato"
        const val DEFAULT_FISH_TYPE = "salmon"
        const val DEFAULT_OTHER_INGREDIENT_TYPE = "salt"
        //Preferences
        const val PREFERENCES_MEAT_TYPE = "meatType"
        const val PREFERENCES_VEGETABLE_TYPE = "vegetableType"
        const val PREFERENCES_FISH_TYPE = "fishType"
        const val PREFERENCES_OTHER_INGREDIENT_TYPE = "otherType"

        const val MEAT_TYPE_ID = "meatTypeId"
        const val VEGETABLE_TYPE_ID = "vegetableTypeId"
        const val FISH_TYPE_ID = "fishTypeId"
        const val OTHER_INGREDIENT_TYPE_ID = "otherIngredientTypeId"

        //Preferences INTERNET
        const val PREFERENCES_BACK_ONLINE = "backOnline"



    }

}