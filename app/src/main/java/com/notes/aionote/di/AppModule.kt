package com.notes.aionote.di

import android.content.Context
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.data.repository.AudioPlayerImpl
import com.notes.aionote.data.repository.AudioRecorderImpl
import com.notes.aionote.domain.repository.AudioPlayer
import com.notes.aionote.domain.repository.AudioRecorder
import com.notes.aionote.domain.use_case.authentication.ValidateEmailUseCase
import com.notes.aionote.domain.use_case.authentication.ValidatePasswordUseCase
import com.notes.aionote.domain.use_case.authentication.ValidateRetypePasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	@Provides
	@Dispatcher(AioDispatcher.IO)
	fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
	
	@Provides
	@Dispatcher(AioDispatcher.Default)
	fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
	
	@Provides
	@Dispatcher(AioDispatcher.Main)
	fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

	@Singleton
	@Provides
	fun provideValidateEmail() : ValidateEmailUseCase = ValidateEmailUseCase()

	@Singleton
	@Provides
	fun provideValidatePassword() : ValidatePasswordUseCase = ValidatePasswordUseCase()

	@Singleton
	@Provides
	fun provideValidateRetypePassword() : ValidateRetypePasswordUseCase = ValidateRetypePasswordUseCase()
	
	@Singleton
	@Provides
	fun provideAudioRecorder(@ApplicationContext context: Context) : AudioRecorder = AudioRecorderImpl(context)
	
	@Singleton
	@Provides
	fun provideAudioPlayer(@ApplicationContext context: Context) : AudioPlayer = AudioPlayerImpl(context)
}