package io.qtd.fungpt.auth.core

import io.qtd.fungpt.auth.core.services.AuthService
import io.qtd.fungpt.auth.core.usecases.AuthUsecase
import io.qtd.fungpt.common.core.bases.CoreModuleCreation
import org.koin.dsl.module

class AuthCoreModuleCreation : CoreModuleCreation() {
    override fun setupKoinModule() = module {

        single<AuthUsecase> {
            AuthService(
                passwordChecker = get(),
                tokenGenerator = get(),
                refreshTokenPort = get(),
                userPort = get(),
                txPort = get(),
                eventPublisherPort = get()
            )
        }
    }

}