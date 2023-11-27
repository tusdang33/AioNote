package com.notes.aionote.common

import androidx.annotation.DrawableRes
import com.notes.aionote.R
import com.notes.aionote.presentation.home.homeRoute
import com.notes.aionote.presentation.save.finishedRoute
import com.notes.aionote.presentation.search.searchRoute
import com.notes.aionote.presentation.setting.navigation.settingRoute

enum class TopLevelDestination(
	@DrawableRes
	val selectedIconRes: Int,
	@DrawableRes
	val unselectedIconRes: Int,
	val nameItem: String,
	val route: String
) {
	HOME(
		R.drawable.home_fill,
		R.drawable.home_outline,
		"Home",
		homeRoute
	),
	FINISHED(
		R.drawable.note_check_fill,
		R.drawable.note_check_outline,
		"Finished",
		finishedRoute
	),
	SEARCH(
		R.drawable.search_fill,
		R.drawable.search_outline,
		"Search",
		searchRoute
	),
	SETTING(
		R.drawable.cog_fill,
		R.drawable.cog_outline,
		"Setting",
		settingRoute
	),
}