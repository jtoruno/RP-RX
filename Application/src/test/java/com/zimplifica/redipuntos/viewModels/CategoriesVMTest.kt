package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.Category
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class CategoriesVMTest : RPTestCase(){
    lateinit var vm : CategoriesVM.ViewModel
    private val categoriesResult =  TestObserver<List<Category>>()
    private val categorySelectedAction = TestObserver<Category>()
    private val loadingActive =  TestObserver<Boolean>()
    private var showError = TestObserver<String>()

    private fun setUpEnv(environment: Environment){
        vm = CategoriesVM.ViewModel(environment)
        vm.outputs.categoriesResult().subscribe(this.categoriesResult)
        vm.outputs.categorySelectedAction().subscribe(this.categorySelectedAction)
        vm.outputs.loadingActive().subscribe(this.loadingActive)
        vm.outputs.showError().subscribe(this.showError)
    }

    @Test
    fun testCategoriesResult(){
        setUpEnv(environment()!!)
        val categories = mutableListOf<Category>()
        categories.add(Category("1234",  "Food",""))
        categories.add(Category("4321", "Health",""))
        vm.inputs.fetchCategories()
        categoriesResult.assertValue(categories)
    }

    @Test
    fun testLoadingActive(){
        setUpEnv(environment()!!)
        vm.inputs.fetchCategories()
        loadingActive.assertValues(true,false)
    }

    @Test
    fun testCategorySelected(){
        setUpEnv(environment()!!)
        val category = Category("1234","Food","")
        vm.inputs.fetchCategories()
        vm.inputs.categorySelected(category)
        categorySelectedAction.assertValue(category)
    }
}