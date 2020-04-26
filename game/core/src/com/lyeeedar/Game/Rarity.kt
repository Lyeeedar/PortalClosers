package com.lyeeedar.Game

enum class Rarity
{
	COMMON,
	UNCOMMON,
	RARE,
	SUPERRARE,
	LEGENDARY;

	companion object
	{
		val Values = Rarity.values()
	}
}