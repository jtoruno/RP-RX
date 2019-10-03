package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class CommercesVMTest: RPTestCase() {
    lateinit var vm : CommercesFragmentVM.ViewModel
    val commerces = TestObserver<CommercesResult>()
    val showError = TestObserver<String>()
    val commerceSelectedAction = TestObserver<Commerce>()
    val loadingActive = TestObserver<Boolean>()
    val filterButtonAction = TestObserver<Unit>()
    val categorySelectedAction = TestObserver<Category>()

    private fun setUpEnv(environment: Environment){
        vm = CommercesFragmentVM.ViewModel(environment)
        vm.outputs.commerces().subscribe(this.commerces)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.commerceSelectedAction().subscribe(this.commerceSelectedAction)
        vm.outputs.loadingActive().subscribe(this.loadingActive)
        vm.outputs.filterButtonAction().subscribe(this.filterButtonAction)
        vm.outputs.categorySelectedAction().subscribe(this.categorySelectedAction)
    }

    @Test
    fun testShowError(){
        setUpEnv(environment()!!)
        vm.inputs.searchButtonPressed("Test")
        showError.assertValueCount(1)
    }

    @Test
    fun testSearchAction(){
        setUpEnv(environment()!!)
        vm.inputs.searchButtonPressed("Pizza")
        commerces.assertValueCount(1)
    }

    @Test
    fun testCategorySelected(){
        setUpEnv(environment()!!)
        val category = Category("123124", "Food",  "")
        vm.inputs.categorySelected(category)
        categorySelectedAction.assertValue(category)
    }


    @Test
    fun testFilterAction(){
        setUpEnv(environment()!!)
        val category = Category( "54321", "Food",  "")
        vm.inputs.filterByCategory(category)
        commerces.assertValueCount(1)
    }

    @Test
    fun testCommerces(){
        setUpEnv(environment()!!)
        vm.inputs.fetchCommerces()
        commerces.assertValueCount(1)
    }

    @Test
    fun testFilterButtonAction(){
        setUpEnv(environment()!!)
        vm.inputs.filterButtonPressed()
        filterButtonAction.assertValueCount(1)
    }

    @Test
    fun testCommerceSelectedAction(){
        setUpEnv(environment()!!)
        vm.inputs.commerceSelected(getCommerce())
        commerceSelectedAction.assertValueCount(1)
    }


    private fun getCommerce() : Commerce {
        val location = Location( "Jaco Bar", 94.0,  -10.0)
        val store = Store( "1", "Jaco Bar", location,  "", null)

        return Commerce( "1",  "Jaco Bar",  "",  "www.jacobar.com",  "www.facebook.com/jacobar",  "+", "instagram.com/jacobar",  "Food",  mutableListOf(store),  "",  "",  "",  Offer( 10),  10,  true)
    }
}