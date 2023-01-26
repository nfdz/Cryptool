package io.github.nfdz.cryptool

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.platform.biometric.BiometricAndroid
import io.github.nfdz.cryptool.platform.clipboard.ClipboardAndroidImpl
import io.github.nfdz.cryptool.platform.legacy.LegacyPinCodeManagerImpl
import io.github.nfdz.cryptool.platform.legacy.LegacyPreferencesStorageAndroid
import io.github.nfdz.cryptool.platform.lifecycle.ApplicationManagerImpl
import io.github.nfdz.cryptool.platform.localization.LocalizedErrorAndroid
import io.github.nfdz.cryptool.platform.version.VersionProviderAndroid
import io.github.nfdz.cryptool.shared.core.export.ExportData
import io.github.nfdz.cryptool.shared.core.export.ExportDataImpl
import io.github.nfdz.cryptool.shared.core.import.ImportData
import io.github.nfdz.cryptool.shared.core.import.ImportDataImpl
import io.github.nfdz.cryptool.shared.core.realm.RealmGateway
import io.github.nfdz.cryptool.shared.core.realm.RealmGatewayImpl
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepositoryImpl
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionViewModel
import io.github.nfdz.cryptool.shared.encryption.viewModel.EncryptionViewModelImpl
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepositoryImpl
import io.github.nfdz.cryptool.shared.gatekeeper.repository.LegacyMigrationManager
import io.github.nfdz.cryptool.shared.gatekeeper.repository.LegacyMigrationManagerImpl
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModel
import io.github.nfdz.cryptool.shared.gatekeeper.viewModel.GatekeeperViewModelImpl
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageRepositoryImpl
import io.github.nfdz.cryptool.shared.message.viewModel.MessageViewModel
import io.github.nfdz.cryptool.shared.message.viewModel.MessageViewModelImpl
import io.github.nfdz.cryptool.shared.password.repository.PasswordRepository
import io.github.nfdz.cryptool.shared.password.repository.PasswordRepositoryImpl
import io.github.nfdz.cryptool.shared.password.viewModel.PasswordViewModel
import io.github.nfdz.cryptool.shared.password.viewModel.PasswordViewModelImpl
import io.github.nfdz.cryptool.shared.platform.biometric.Biometric
import io.github.nfdz.cryptool.shared.platform.file.*
import io.github.nfdz.cryptool.shared.platform.localization.LocalizedError
import io.github.nfdz.cryptool.shared.platform.sms.SmsReceiver
import io.github.nfdz.cryptool.shared.platform.sms.SmsReceiverAndroid
import io.github.nfdz.cryptool.shared.platform.sms.SmsSender
import io.github.nfdz.cryptool.shared.platform.sms.SmsSenderAndroid
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorageAndroid
import io.github.nfdz.cryptool.shared.platform.storage.LegacyPreferencesStorage
import io.github.nfdz.cryptool.shared.platform.version.ChangelogProvider
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider
import io.github.nfdz.cryptool.ui.platform.ApplicationManager
import io.github.nfdz.cryptool.ui.platform.ChangelogProviderAndroid
import io.github.nfdz.cryptool.ui.platform.ClipboardAndroid
import io.github.nfdz.cryptool.ui.platform.LegacyPinCodeManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.ERROR
import org.koin.dsl.module

class CryptoolApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupLogger()
        initKoin()
    }

    private fun setupLogger() {
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
    }

    private val appModule = module {
        // Platform + Core
        single<RealmGateway> { RealmGatewayImpl() }
        single<Biometric> { BiometricAndroid() }
        single<FileMessageSender> { FileMessageSenderAndroid(applicationContext) }
        single<SmsSender> { SmsSenderAndroid(applicationContext) }
        single<SmsReceiver> { SmsReceiverAndroid(applicationContext, get(), get(), get()) }
        single<KeyValueStorage> { KeyValueStorageAndroid(applicationContext) }
        single<ChangelogProvider> { ChangelogProviderAndroid(applicationContext) }
        single<LegacyPinCodeManager> { LegacyPinCodeManagerImpl }
        single<ClipboardAndroid> { ClipboardAndroidImpl }
        single<ApplicationManager> { ApplicationManagerImpl }
        single<VersionProvider> { VersionProviderAndroid(get()) }
        single<ImportFile> { ImportFileAndroid(applicationContext, get(), get()) }
        single<ExportFile> { ExportFileAndroid(applicationContext, get(), get()) }
        single<ExportData> { ExportDataImpl(get(), get(), get()) }
        single<ImportData> { ImportDataImpl(get(), get(), get()) }
        single<LocalizedError> { LocalizedErrorAndroid(applicationContext) }

        // Repositories
        single<GatekeeperRepository> { GatekeeperRepositoryImpl(get(), get(), get(), get(), get(), get(), get()) }
        single<EncryptionRepository> { EncryptionRepositoryImpl(get()) }
        single<MessageRepository> { MessageRepositoryImpl(get(), get(), get(), get()) }
        single<PasswordRepository> { PasswordRepositoryImpl(get()) }

        // View Models
        single<PasswordViewModel> { PasswordViewModelImpl(get()) }
        single<EncryptionViewModel> { EncryptionViewModelImpl(get()) }
        single<MessageViewModel> { MessageViewModelImpl(get(), get(), get()) }
        single<GatekeeperViewModel> { GatekeeperViewModelImpl(get(), get(), get(), get()) }

        // Legacy
        single<LegacyPreferencesStorage> { LegacyPreferencesStorageAndroid(applicationContext) }
        single<LegacyMigrationManager> { LegacyMigrationManagerImpl(get(), get()) }
    }

    private fun initKoin() {
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(ERROR)
            androidContext(this@CryptoolApp)
            modules(appModule)
        }
    }

}