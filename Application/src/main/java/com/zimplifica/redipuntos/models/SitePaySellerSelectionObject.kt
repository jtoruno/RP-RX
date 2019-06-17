package com.zimplifica.redipuntos.models

import com.zimplifica.domain.entities.PaymentPayload
import com.zimplifica.domain.entities.Vendor
import java.io.Serializable

class SitePaySellerSelectionObject(val vendor : Vendor, val payload : PaymentPayload) : Serializable