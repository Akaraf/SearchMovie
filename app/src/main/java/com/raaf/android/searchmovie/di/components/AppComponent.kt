package com.raaf.android.searchmovie.di.components

import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.di.modules.*
import com.raaf.android.searchmovie.repository.AppRepository
import com.raaf.android.searchmovie.ui.FilmViewModel
import com.raaf.android.searchmovie.ui.MainActivity
import com.raaf.android.searchmovie.ui.home.swipeFragments.*
import com.raaf.android.searchmovie.ui.search.SearchResultViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(AppModule::class, FilmFetcherModule::class, DatabaseModule::class, AppRepositoryModule::class, AppConverterModule::class))
@Singleton
interface AppComponent {

    fun inject(appRepository: AppRepository)//delete this bullsheet
    fun inject(filmFetcher: FilmFetcher)
    fun inject(mainActivity: MainActivity)


    fun inject(searchResultVM: SearchResultViewModel)
    fun inject(filmVM: FilmViewModel)
    fun inject(topVM: TopViewModel)
    fun inject(compilationVM: CompilationViewModel)
    fun inject(detailCategoryVM: DetailCategoryViewModel)
    fun inject(myFilmsVM: MyFilmsViewModel)
}