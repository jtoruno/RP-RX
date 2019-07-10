package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.Location
import com.zimplifica.domain.entities.Promotion
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SocialNetworkObject
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PromotionDetailVM {
    interface Inputs {
        /// Call when a location is selected.
        fun locationPressed(location: Location)

        /// Call when a social network is selected.
        fun openUrl(socialNetwork: SocialNetworkObject)
    }
    interface Outputs {
        /// Emits to get the promotion.
        fun promotionAction() : Observable<Promotion>

        /// Emits when a location is selected.
        fun locationAction() : Observable<Location>

        /// Emits when a social network is selected.
        fun openUrlAction() : Observable<SocialNetworkObject>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<PromotionDetailVM>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val locationPressed = PublishSubject.create<Location>()
        private val openUrl = PublishSubject.create<SocialNetworkObject>()

        //Outputs
        private val promotionAction = BehaviorSubject.create<Promotion>()
        private val locationAction = BehaviorSubject.create<Location>()
        private val openUrlAction = BehaviorSubject.create<SocialNetworkObject>()

        init {
            intent()
                .filter { it.hasExtra("promotion") }
                .map { it.getSerializableExtra("promotion") as Promotion }
                .subscribe(this.promotionAction)

            this.locationPressed
                .subscribe(this.locationAction)

            this.openUrl
                .subscribe(this.openUrlAction)
        }

        override fun locationPressed(location: Location) {
            return this.locationPressed.onNext(location)
        }

        override fun openUrl(socialNetwork: SocialNetworkObject) {
            return this.openUrl.onNext(socialNetwork)
        }

        override fun promotionAction(): Observable<Promotion> = this.promotionAction

        override fun locationAction(): Observable<Location> = this.locationAction

        override fun openUrlAction(): Observable<SocialNetworkObject> = this.openUrlAction
    }
}