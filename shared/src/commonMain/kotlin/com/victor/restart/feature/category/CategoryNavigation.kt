package com.victor.restart.feature.category

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("category")
data object CategoryRoute

@Serializable
@SerialName("category_form")
data class CategoryFormRoute(
    val categoryId: Long? = null
)

fun NavController.navigateToCategoryForm(categoryId: Long? = null) {
    navigate(CategoryFormRoute(categoryId))
}

fun NavGraphBuilder.categoryDestination(
    navController: NavController
) {
    composable<CategoryRoute> {
        CategoryScreen(
            navigateToCategoryForm = { navController.navigateToCategoryForm() },
            navigateToEditCategory = { id -> navController.navigateToCategoryForm(id) },
            navController = navController
        )
    }

    composable<CategoryFormRoute> {
        CategoryFormScreen(
            navigateBack = { navController.popBackStack() },
            navController = navController

        )
    }
}