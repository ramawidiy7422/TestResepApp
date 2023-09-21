package com.ramawidi.ghoresepmakanan.data.repositories

import com.ramawidi.ghoresepmakanan.data.network.LocalDataSource
import com.ramawidi.ghoresepmakanan.data.network.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource) {
    val remoteData = remoteDataSource
    val localData = localDataSource

}