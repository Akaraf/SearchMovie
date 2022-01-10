package com.raaf.android.searchmovie.di.components

import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.Settings
import com.raaf.android.searchmovie.backgroundJob.RefreshDB
import com.raaf.android.searchmovie.backgroundJob.services.FirebaseStorageService
import com.raaf.android.searchmovie.backgroundJob.services.RefreshDBService
import com.raaf.android.searchmovie.di.modules.*
import com.raaf.android.searchmovie.repository.FilmRepo
import com.raaf.android.searchmovie.repository.User
import com.raaf.android.searchmovie.ui.MainViewModel
import com.raaf.android.searchmovie.ui.film.FilmFramesViewModel
import com.raaf.android.searchmovie.ui.film.FilmViewModel
import com.raaf.android.searchmovie.ui.film.SequelsPrequelsViewModel
import com.raaf.android.searchmovie.ui.film.TrailerViewModel
import com.raaf.android.searchmovie.ui.home.HomeViewModel
import com.raaf.android.searchmovie.ui.home.swipeFragments.CompilationViewModel
import com.raaf.android.searchmovie.ui.home.swipeFragments.MyFilmsViewModel
import com.raaf.android.searchmovie.ui.home.swipeFragments.TopViewModel
import com.raaf.android.searchmovie.ui.home.swipeFragments.detailCategory.DetailCategoryViewModel
import com.raaf.android.searchmovie.ui.person.PersonViewModel
import com.raaf.android.searchmovie.ui.profile.AuthViewModel
import com.raaf.android.searchmovie.ui.profile.ProfileViewModel
import com.raaf.android.searchmovie.ui.profile.SettingsViewModel
import com.raaf.android.searchmovie.ui.profile.SupportServiceViewModel
import com.raaf.android.searchmovie.ui.search.SearchResultViewModel
import com.raaf.android.searchmovie.ui.search.SearchViewModel
import com.raaf.android.searchmovie.ui.search.categoryForSearch.CategoryForSearchViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, FilmFetcherModule::class, DatabaseModule::class, AppConverterModule::class, FirebaseModule::class, UserModule::class, AppPreferencesModule::class, SettingsModule::class, RefreshDBModule::class])
@Singleton
interface AppComponent {

    fun inject(app: App)
    //fun inject(worker: Worker)
    fun inject(filmRepo: FilmRepo)
    fun inject(user: User)
    fun inject(settings: Settings)
    fun inject(refreshDB: RefreshDB)

    fun inject(refreshDBService: RefreshDBService)
    fun inject(firebaseStorageService: FirebaseStorageService)

    //fun inject(refreshDBWorker: RefreshDBWorker)
    fun inject(mainActivityVM: MainViewModel)
    fun inject(searchResultVM: SearchResultViewModel)
    fun filmVM() : FilmViewModel.Factory
    fun inject(topVM: TopViewModel)
    fun inject(compilationVM: CompilationViewModel)
    fun inject(detailCategoryVM: DetailCategoryViewModel)
    fun inject(myFilmsVM: MyFilmsViewModel)
    fun inject(profileVM: ProfileViewModel)
    fun inject(authVM: AuthViewModel)
    fun inject(filmFramesVM: FilmFramesViewModel)
    fun inject(trailerVM: TrailerViewModel)
    fun inject(supportServiceVM: SupportServiceViewModel)
    fun inject(searchVM: SearchViewModel)
    fun inject(personVM: PersonViewModel)
    fun inject(sequelsPrequelsVM: SequelsPrequelsViewModel)
    fun inject(categoryForSearchVM: CategoryForSearchViewModel)
    fun inject(homeVM: HomeViewModel)
    fun inject(settingVM: SettingsViewModel)
}