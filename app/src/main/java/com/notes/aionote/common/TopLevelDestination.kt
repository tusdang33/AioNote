package com.notes.aionote.common

import androidx.annotation.DrawableRes
import com.notes.aionote.R
import com.notes.aionote.presentation.home.homeRoute
import com.notes.aionote.presentation.save.saveRoute
import com.notes.aionote.presentation.search.searchRoute
import com.notes.aionote.presentation.setting.settingRoute

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
	SAVE(
		R.drawable.bookmark_fill,
		R.drawable.bookmark_outline,
		"Search",
		saveRoute
	),
	SEARCH(
		R.drawable.search_fill,
		R.drawable.search_outline,
		"Order",
		searchRoute
	),
	SETTING(
		R.drawable.cog_fill,
		R.drawable.cog_outline,
		"Profile",
		settingRoute
	),
}