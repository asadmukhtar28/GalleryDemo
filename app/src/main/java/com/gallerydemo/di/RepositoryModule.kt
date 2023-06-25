package com.gallerydemo.di

import com.gallerydemo.data.repository.GalleryRepository
import com.gallerydemo.data.repository.GalleryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    fun provideGalleryRepository(repository: GalleryRepositoryImpl): GalleryRepository {
        return repository
    }
}