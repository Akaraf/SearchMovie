package com.raaf.android.searchmovie.di.modules

import android.content.Context
import androidx.room.Room
import com.raaf.android.searchmovie.database.CompilationDatabase
import com.raaf.android.searchmovie.database.MyFilmsDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(AppModule::class))
class DatabaseModule {

    @Provides
    @Singleton
    fun provideTopDB(context: Context) : TopDatabase {
        return Room.databaseBuilder(context, TopDatabase::class.java, "topDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideMyFilmsDB(context: Context) : MyFilmsDatabase {
        return Room.databaseBuilder(context, MyFilmsDatabase::class.java, "myFilmsDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideCompilationDB(context: Context) : CompilationDatabase {
        return Room.databaseBuilder(context, CompilationDatabase::class.java, "compilationDatabase")
                .build()
    }
}