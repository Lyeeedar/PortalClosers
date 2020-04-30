package com.lyeeedar.Components

import com.lyeeedar.Game.Pack

class PackComponent : AbstractComponent()
{
	override val type: ComponentType = ComponentType.Pack

	lateinit var pack: Pack

	fun getPackAgro(): EntityReference?
	{
		var bestTarget: EntityReference? = null
		var bestTargetValue = -1f

		for (entity in pack.entities)
		{
			val hate = entity.get()?.hate() ?: continue

			for (hated in hate.hateMap.values())
			{
				if (hated.value > bestTargetValue)
				{
					bestTargetValue = hated.value
					bestTarget = hated.entityReference
				}
			}
		}

		return bestTarget
	}

	override fun reset()
	{

	}
}

fun Entity.setPack(pack: Pack): Entity
{
	val comp = addOrGet(ComponentType.Pack) as PackComponent
	comp.pack = pack

	return this
}