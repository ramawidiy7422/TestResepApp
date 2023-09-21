package com.ramawidi.ghoresepmakanan.di

import android.app.Application
import androidx.room.Room
import com.ramawidi.ghoresepmakanan.data.database.RecipesDao
import com.ramawidi.ghoresepmakanan.data.database.RecipesDatabase
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): RecipesDatabase {
        return Room.databaseBuilder(app, RecipesDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideDao(database: RecipesDatabase): RecipesDao {
        return database.recipesDao()
    }

}