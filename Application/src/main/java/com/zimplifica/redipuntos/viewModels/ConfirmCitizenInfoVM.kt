package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import android.support.annotation.NonNull
import com.zimplifica.domain.entities.Citizen
import com.zimplifica.domain.entities.CitizenInput
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.Person
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ConfirmCitizenInfoVM {
    interface Inputs {
        fun onCreate()
        fun nextButtonPressed()
    }
    interface Outputs{
        fun printData() : Observable<Person>
        fun loadingEnabled() : Observable<Boolean>
        fun showError(): Observable<String>
        fun citizenInformationConfirmed() : Observable<Citizen>

    }
    @SuppressLint("CheckResult")
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<ConfirmCitizenInfoVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()
        private val nextButtonPressed = PublishSubject.create<Unit>()
        //Outputs
        private val printData = BehaviorSubject.create<Person>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()
        private val citizenInformationConfirmed = BehaviorSubject.create<Citizen>()

        init {
            val personObservable = intent()
                .filter { it.hasExtra("citizen") }
                .map { return@map it.getSerializableExtra("citizen") as Person }
            personObservable
                .subscribe(this.printData)

            val updateInfo = personObservable
                .takeWhen(this.nextButtonPressed)
                .flatMap { updateUserInfo(it.second) }
                .share()
            updateInfo
                .filter { !it.isFail() }
                .map { when(it){
                    is Result.failure -> return@map null
                    is Result.success -> return@map it.value
                } }
                .subscribe(this.citizenInformationConfirmed)
            updateInfo
                .filter { it.isFail() }
                .subscribe {
                    when(it){
                        is Result.failure ->{
                            this.showError.onNext("Lo sentimos, ha ocurrido un error al actualizar.")
                        }
                        else -> {}
                    }
                }
        }

        override fun nextButtonPressed() {
           return this.nextButtonPressed.onNext(Unit)
        }

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun showError(): Observable<String> = this.showError

        override fun citizenInformationConfirmed(): Observable<Citizen> = this.citizenInformationConfirmed

        override fun onCreate() {
            return this.onCreate.onNext(Unit)
        }
        override fun printData(): Observable<Person> = this.printData

        private fun updateUserInfo(person : Person) : Observable<Result<Citizen>>{
            val input = CitizenInput(person.cedula?:"",person.nombre!!,person.apellido1+" "+person.apellido2,person.fechaNacimiento!!)
            return environment.userUseCase().updateUserInfo(input)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

    }
}