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

	FIRE(BaseElements.FIRE), // ranged aoe attacks
	WATER(BaseElements.WATER), // obstacles / roots
	WOOD(BaseElements.WOOD), // healing + buffs
	METAL(BaseElements.METAL), // melee attacks + buffs
	AIR(BaseElements.AIR), // jumping / teleporting + ranged attacks

	OIL(BaseElements.FIRE, BaseElements.WATER), // setting things on fire
	SLIME(BaseElements.FIRE, BaseElements.WOOD), // explosions
	LAVA(BaseElements.FIRE, BaseElements.METAL), // ground based dots
	LIGHTNING(BaseElements.FIRE, BaseElements.AIR), // chain damage

	BLOOD(BaseElements.WATER, BaseElements.WOOD), // life for damage
	CORROSION(BaseElements.WATER, BaseElements.METAL), // acid/poison armour reduction
	PRISMATIC(BaseElements.WATER, BaseElements.AIR), // illusions

	NECROTIC(BaseElements.WOOD, BaseElements.METAL), // raising dead
	COSMIC(BaseElements.WOOD, BaseElements.AIR), // summoning

	VORPAL(BaseElements.METAL, BaseElements.AIR); // super crits

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