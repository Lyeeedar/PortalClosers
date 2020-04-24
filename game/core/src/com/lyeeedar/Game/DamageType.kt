package com.lyeeedar.Game

import com.lyeeedar.Util.Localisation

enum class DamageType
{
	NONE,
	FIRE, // applies damage dot
	ICE, // freezes target in place for a number of turns
	LIGHTNING, // chance to chain damage to a nearby target
	VORPAL, // pierces armour
	POISON, // applies a dot that isnt mitigated by armour or dr
	ACID; // reduces armour and dr

	val niceName: String
		get()
		{
			return when(this)
			{
				NONE -> ""
				FIRE -> Localisation.getText("damagetype.fire", "UI")
				ICE -> Localisation.getText("damagetype.ice", "UI")
				LIGHTNING -> Localisation.getText("damagetype.lightning", "UI")
				VORPAL -> Localisation.getText("damagetype.vorpal", "UI")
				POISON -> Localisation.getText("damagetype.poison", "UI")
				ACID -> Localisation.getText("damagetype.acid", "UI")
			}
		}

	val tooltip: String
		get()
		{
			return when(this)
			{
				NONE -> ""
				FIRE -> Localisation.getText("damagetype.fire.description", "UI")
				ICE -> Localisation.getText("damagetype.ice.description", "UI")
				LIGHTNING -> Localisation.getText("damagetype.lightning.description", "UI")
				VORPAL -> Localisation.getText("damagetype.vorpal.description", "UI")
				POISON -> Localisation.getText("damagetype.poison.description", "UI")
				ACID -> Localisation.getText("damagetype.acid.description", "UI")
			}
		}

	companion object
	{
		val Values = values()
	}
}