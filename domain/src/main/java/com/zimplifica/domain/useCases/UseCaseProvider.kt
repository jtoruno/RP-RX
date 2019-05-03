package com.zimplifica.domain.useCases

interface UseCaseProvider {
    fun makeAuthenticationUseCase() : AuthenticationUseCase
}