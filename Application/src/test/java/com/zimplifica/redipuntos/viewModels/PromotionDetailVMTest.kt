package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.Commerce
import com.zimplifica.domain.entities.Location
import com.zimplifica.domain.entities.Offer
import com.zimplifica.domain.entities.Store
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SocialNetworkObject
import io.reactivex.observers.TestObserver
import org.junit.Test

class PromotionDetailVMTest : RPTestCase() {
    lateinit var vm : PromotionDetailVM.ViewModel
    private val commerceAction = TestObserver<Commerce>()
    private val locationAction = TestObserver<Location>()
    private val openUrlAction = TestObserver<SocialNetworkObject>()

    private fun setUpEnv(environment: Environment){
        val location = Location("Jaco Bar",  94.0,  -10.0)
        val store = Store("1", "Jaco Bar",location,  "", null)
        val commerce = Commerce("1","Jaco Bar", "", "www.jacobar.com", "www.facebook.com/jacobar", "+",  "instagram.com/jacobar", "Food", mutableListOf(store),  "",  "",  "", Offer( 10),  10,  true)

        vm = PromotionDetailVM.ViewModel(environment)
        vm.intent(Intent().putExtra("commerce",commerce))
        vm.outputs.commerceAction().subscribe(this.commerceAction)
        vm.outputs.locationAction().subscribe(this.locationAction)
        vm.outputs.openUrlAction().subscribe(this.openUrlAction)
    }

    @Test
    fun testCommerceAction(){
        setUpEnv(environment()!!)
        val testCommerce = getCommerce()
        commerceAction.assertValue(testCommerce)
    }

    @Test
    fun testLocationAction(){
        setUpEnv(environment()!!)
        val location = getCommerce().stores.first().location
        vm.inputs.locationPressed(location)
        locationAction.assertValue(location)
    }

    @Test
    fun testOpenUrl(){
        setUpEnv(environment()!!)
        val socialNetwork = SocialNetworkObject("www.facebook.com/PizzaHutCr", "")
        vm.inputs.openUrl(socialNetwork)
        openUrlAction.assertValue(socialNetwork)
    }

    private fun getCommerce() : Commerce {
        val location = Location( "Jaco Bar", 94.0,  -10.0)
        val store = Store( "1", "Jaco Bar", location,  "", null)

        return Commerce( "1",  "Jaco Bar",  "",  "www.jacobar.com",  "www.facebook.com/jacobar",  "+", "instagram.com/jacobar",  "Food",  mutableListOf(store),  "",  "",  "",  Offer( 10),  10,  true)
    }
}