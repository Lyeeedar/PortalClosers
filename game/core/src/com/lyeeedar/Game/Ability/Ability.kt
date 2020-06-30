package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import java.util.*
import squidpony.squidmath.LightRNG

class Ability(val data: AbilityData)
{
	var mana = 0f
	var remainingUsages = data.usages

	var justUsed = false

	val name: String
		get() = transform(Localisation.getText(data.name, "Ability"), data.nameTransforms)

	val description: String
		get() = transform(Localisation.getText(data.description, "Ability"), data.descriptionTransforms)

	var isSelected = false

	fun transform(initial: String, transforms: Array<(String)->String>): String
	{
		var current = initial
		for (transform in transforms)
		{
			current = transform.invoke(current)
		}
		return current
	}

	fun getValidTargets(entity: Entity, world: World<*>): List<AbstractTile>
	{
		val pos = entity.position()!!
		val vision = entity.addOrGet(ComponentType.Vision) as VisionComponent
		val visiblePoints = vision.getVision(pos.x, pos.y)
		val visibleTiles = visiblePoints.filter { it.dist(pos.position) in data.range.x..data.range.y }.mapNotNull { world.grid.tryGet(it, null) }

		return when (data.targetType)
		{
			AbilityData.TargetType.SELF -> listOf(world.grid[pos.position])

			AbilityData.TargetType.TILE -> visibleTiles
			AbilityData.TargetType.EMPTY_TILE -> visibleTiles.filter { it.getPassable(pos.slot, entity) }

			AbilityData.TargetType.ALLY -> visibleTiles.filter { it.contents[pos.slot]?.get()?.isAllies(entity) ?: false }

			AbilityData.TargetType.ANY_ENEMY, AbilityData.TargetType.TARGET_ENEMY -> visibleTiles.filter { it.contents[pos.slot]?.get()?.isEnemies(entity) ?: false }
		}
	}

	private val tempVariableMap = ObjectFloatMap<String>()
	private val values: Array<Pair<AbstractTile, Float>> = Array()
	fun pickTarget(entity: Entity, world: World<*>, rng: LightRNG): AbstractTile?
	{
		val validTargets = getValidTargets(entity, world)
		if (validTargets.isEmpty()) return null

		if (data.targetType == AbilityData.TargetType.TILE || data.targetType == AbilityData.TargetType.EMPTY_TILE)
		{
			return validTargets.random(rng)
		}
		else if (data.targetType == AbilityData.TargetType.SELF)
		{
			return world.grid[entity.position()!!.position]
		}

		val pos = entity.position()!!

		values.clear()
		for (target in validTargets)
		{
			val other = target.contents[pos.slot]!!.get()!!

			tempVariableMap.clear()
			other.statistics()!!.write(tempVariableMap)
			tempVariableMap["dist"] = other.position()!!.position.dist(pos.position).toFloat()

			if (data.targetCondition.evaluate(tempVariableMap, rng).round() != 0)
			{
				val value = data.sortCondition.evaluate(tempVariableMap, rng)
				values.add(Pair(target, value))
			}
		}

		val sorted = if (data.selectMinByCondition) values.sortedBy { it.second } else values.sortedByDescending { it.second }
		return sorted.firstOrNull()?.first
	}
}