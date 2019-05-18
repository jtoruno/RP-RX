package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.Observable

interface SPSelectionVM {
    interface Inputs {
        fun qrCameraButtonPressed()
        fun nextButtonPressed()
        fun descriptionTextFieldChanged(description: String)
        fun vendorInformation(vendor: Vendor)
    }

    interface Outputs{
        fun qrCameraButtonAction() : Observable<Unit>
        fun nextButtonAction() : Observable<SitePaySellerSelectionObject>
        fun nextButtonEnabled() : Observable<Boolean>
        fun nextButtonLoadingIndicator() : Observable<Boolean>
        fun showError() : Observable<String>
        fun descriptionTextField() : Observable<String>
        fun vendorInformationAction() : Observable<Vendor>
    }
}