package com.lyeeedar.Game

enum class BaseElements
{
	FIRE,
	WATER,
	WOOD,
	METAL,
	AIR;

	lateinit var effectiveAgainst: BaseElements

	companion object
	{
		init
		{
			FIRE.effectiveAgainst = METAL
			METAL.effectiveAgainst = WOOD
			WOOD.effectiveAgainst = AIR
			AIR.effectiveAgainst = WATER
			WATER.effectiveAgainst = FIRE
		}
	}
}

enum class Elements constructor(vararg val baseElements: BaseElements)
{
	NONE,

	FIRE(BaseElements.FIRE),
	WATER(BaseElements.WATER),
	WOOD(BaseElements.WOOD),
	METAL(BaseElements.METAL),
	AIR(BaseElements.AIR),

	OIL(BaseElements.FIRE, BaseElements.WATER),
	SLIME(BaseElements.FIRE, BaseElements.WOOD),
	LAVA(BaseElements.FIRE, BaseElements.METAL),
	LIGHTNING(BaseElements.FIRE, BaseElements.AIR),

	POISON(BaseElements.WATER, BaseElements.WOOD),
	ACID(BaseElements.WATER, BaseElements.METAL),
	PRISMATIC(BaseElements.WATER, BaseElements.AIR),

	NECROTIC(BaseElements.WOOD, BaseElements.METAL),
	COSMIC(BaseElements.WOOD, BaseElements.AIR),

	VORPAL(BaseElements.METAL, BaseElements.AIR);

	fun getDamageAgainst(target: Elements): Float
	{
		var count = 0
		for (el in baseElements)
		{
			for (tel in target.baseElements)
			{
				if (el.effectiveAgainst == tel)
				{
					count++
					break
				}
			}
		}

		return 1f + 0.2f * count
	}
}