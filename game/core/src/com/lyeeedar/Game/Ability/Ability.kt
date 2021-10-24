package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.ActionSequence.Actions.BlockTurnAction
import com.lyeeedar.ActionSequence.Actions.MarkAndWaitForPlayerAction
import com.lyeeedar.ActionSequence.Actions.PermuteAction
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.random
import com.lyeeedar.Util.round
import com.lyeeedar.Util.set
import squidpony.squidmath.IntSet
import squidpony.squidmath.LightRNG

class Ability(val data: AbilityData)
{
	var cooldown = 0
	var remainingUsages = data.usages
	var available = true

	var justUsed = false

	var isSelected = false
	val selectedTargets = Array<Tile>()
	var launch = false

	var icon: Sprite? = null
	var name: String? = null
	var description: String? = null

	fun isUsable() = cooldown == 0 && remainingUsages != 0 && available

	fun transform(initial: String, transforms: Array<(String)->String>): String
	{
		var current = initial
		for (transform in transforms)
		{
			current = transform.invoke(current)
		}
		return current
	}

	fun getValidTargets(entity: Entity, world: World<*>, fixedDirection: Direction?, targetType: AbilityData.TargetType? = null): List<AbstractTile>
	{
		val pos = entity.position()!!
		val vision = entity.addOrGet(ComponentType.Vision) as VisionComponent
		val visiblePoints = vision.getVision(pos.x, pos.y)
		var visibleTiles = visiblePoints.filter { it.dist(pos.position) in data.range.x..data.range.y }.mapNotNull { world.grid.tryGet(it, null) }

		if (data.cardinalDirectionsOnly)
		{
			visibleTiles = visibleTiles.filter { it.x == pos.x || it.y == pos.y }
		}

		if (fixedDirection != null)
		{
			visibleTiles = visibleTiles.filter { Direction.getCardinalDirection(it, pos.position) == fixedDirection }
		}

		return when (targetType ?: data.targetType)
		{
			AbilityData.TargetType.SELF -> listOf(world.grid[pos.position])

			AbilityData.TargetType.TILE -> visibleTiles
			AbilityData.TargetType.EMPTY_TILE -> visibleTiles.filter { it.getPassable(pos.slot, entity) }

			AbilityData.TargetType.ALLY -> visibleTiles.filter { it.contents[pos.slot]?.get()?.isAllies(entity) ?: false }

			AbilityData.TargetType.ANY_ENEMY, AbilityData.TargetType.TARGET_ENEMY -> visibleTiles.filter { it.contents[pos.slot]?.get()?.isEnemies(entity) ?: false }
		}
	}

	fun getValidTile(entity: Entity, world: World<*>, rng: LightRNG, targetTile: Point?, fixedDirection: Direction?): Tile?
	{
		val target =
			if (data.targetType == AbilityData.TargetType.TARGET_ENEMY)
				if (fixedDirection != null)
					entity.position()!!.position + fixedDirection
				else
					targetTile
			else
				pickTarget(entity, world, rng, fixedDirection)
		if (target == null) return null
		val tile = world.grid.tryGet(target, null) as? Tile ?: return null
		if (tile.dist(entity.position()!!.position) !in data.range.x..data.range.y) return null

		return tile
	}

	private val tempVariableMap = ObjectFloatMap<String>()
	private val values: Array<Pair<AbstractTile, Float>> = Array()
	fun pickTarget(entity: Entity, world: World<*>, rng: LightRNG, fixedDirection: Direction?): AbstractTile?
	{
		val validTargets = getValidTargets(entity, world, fixedDirection)
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
			other.variables()?.write(tempVariableMap)
			tempVariableMap["dist"] = other.position()!!.position.dist(pos.position).toFloat()

			if (data.targetCondition.evaluate(tempVariableMap, rng).round() != 0)
			{
				val value = data.sortCondition.evaluate(tempVariableMap, rng)
				values.add(Pair(target, value))
			}
		}

		val sorted = if (data.pickSortedMin) values.sortedBy { it.second } else values.sortedByDescending { it.second }
		return sorted.firstOrNull()?.first
	}

	private val predictionState = ActionSequenceState()
	fun predictTargets(entity: Entity, world: World<*>, tile: Tile): Array<Pair<Point, Int>>
	{
		predictionState.reset()
		predictionState.set(entity.getRef(), data.actionSequence, world, 0L)
		predictionState.targets.clear()
		predictionState.targets.add(tile)
		predictionState.facing = Direction.getCardinalDirection(tile, entity.position()!!.position)

		val output = Array<Pair<Point, Int>>()
		val added = IntSet()
		var turns = 0
		for (action in data.actionSequence.rawActions)
		{
			if (action.permutesTargets)
			{
				action.enter(predictionState)
				action.exit(predictionState)

				for (target in predictionState.targets)
				{
					if (!added.contains(target.hashCode()))
					{
						added.add(target.hashCode())
						output.add(Pair(target, turns))
					}
				}
			}
			else if (action is BlockTurnAction)
			{
				turns++
			}
		}

		return output
	}
}