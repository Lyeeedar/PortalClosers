package com.lyeeedar.Components

import com.lyeeedar.Game.Ability

class ActiveAbilityComponent : AbstractComponent()
{
	override val type: ComponentType = ComponentType.ActiveAbility

	var ability: Ability? = null

	override fun reset()
	{
		ability = null
	}
}