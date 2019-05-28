package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.Person
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface CitizenInfoVM {
    interface Inputs{
        fun nextButtonPressed()
        fun personInfo(person : Person)
    }
    interface Outputs{
        fun startScanActivity() : Observable<Unit>
        fun startNextActivity() : Observable<Person>

    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<CitizenInfoVM>(environment),Inputs, Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val nextButtonPressed = PublishSubject.create<Unit>()
        private val personInfo = BehaviorSubject.create<Person>()

        //Outputs
        private val startScanActivity : Observable<Unit>
        private val startNextActivity = PublishSubject.create<Person>()

        init {
            this.startScanActivity = this.nextButtonPressed

            personInfo
                .subscribe(this.startNextActivity)

        }

        override fun personInfo(person: Person) {
            return this.personInfo.onNext(person)
        }

        override fun startNextActivity(): Observable<Person> = this.startNextActivity

        override fun nextButtonPressed() = this.nextButtonPressed.onNext(Unit)

        override fun startScanActivity(): Observable<Unit> = this.startScanActivity

    }
}