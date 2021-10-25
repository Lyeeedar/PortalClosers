package com.lyeeedar.Systems

import com.badlogic.gdx.utils.IntSet
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.ShadowCastCache
import com.lyeeedar.SpaceSlot
import com.lyeeedar.UI.DebugConsole
import com.lyeeedar.Util.*
import com.sun.javafx.Utils.clamp

fun World<*>.tileSystem() = systems.filterIsInstance<TileSystem>().firstOrNull()
class TileSystem(world: World<*>) : AbstractSystem(world)
{
	val levelCompleteTileEffect = AssetManager.loadParticleEffect("darkest/level_complete_tile").getParticleEffect()
	val levelCompleteFadeTime = 0.4f
	val levelCompleteFadeLeadIn = 0.6f
	val levelCompleteEndCol = Colour(0f, 0f, 0f, 0f).lockColour()
	var completing = false
	var completed = false

	override fun doUpdate(deltaTime: Float)
	{
		doVisibility(deltaTime)

		var allCompleted = true
		for (x in 0 until world.grid.width)
		{
			for (y in 0 until world.grid.height)
			{
				val tile = world.grid[x, y] as Tile
				allCompleted = allCompleted && tile.hasCompleted

				if (tile.queuedActions.size > 0)
				{
					val itr = tile.queuedActions.iterator()
					while (itr.hasNext())
					{
						val action = itr.next()
						action.delay -= deltaTime
						if (action.delay <= 0f)
						{
							itr.remove()
							action.function.invoke()
						}
					}
				}

				tile.isTargetted = false
				tile.isValidTarget = false
				tile.isSelectedTarget = false
				tile.isPreviewedTarget = false

				if (tile.predictedAttacksFrom.size > 0)
				{
					val itr = tile.predictedAttacksFrom.iterator()
					while (itr.hasNext())
					{
						val attack = itr.next()
						if (!attack.sequence.isValid())
						{
							itr.remove()
						}
					}
				}

				if (tile.levelCompleting)
				{
					if (tile.levelCompleteDelay > 0f)
					{
						tile.levelCompleteDelay -= deltaTime
						if (tile.levelCompleteDelay <= 0f)
						{
							val effect = levelCompleteTileEffect.copy()
							effect.isLit = false
							val entity = effect.addToWorld(world, tile)
							entity.renderable()!!.ignoreTileCol = true
						}
					}
					else if (!tile.hasCompleted)
					{
						tile.levelCompleteTimer += deltaTime

						val fadeTime = tile.levelCompleteTimer - levelCompleteFadeLeadIn

						val alpha = clamp(fadeTime / levelCompleteFadeTime, 0f, 1f)
						tile.tileCol.set(Colour.WHITE).lerp(levelCompleteEndCol, alpha)
						tile.isTileDirty = true

						if (tile.levelCompleteTimer >= levelCompleteFadeLeadIn + levelCompleteFadeTime)
						{
							tile.hasCompleted = true
						}
					}
				}
			}
		}

		for (ability in world.player!!.abilities())
		{
			if (ability.isSelected)
			{
				if (ability.data.targetType != AbilityData.TargetType.SELF && ability.data.targetType != AbilityData.TargetType.TILE)
				{
					for (tile in ability.getValidTargets(world.player!!, world, null, targetType = AbilityData.TargetType.TILE))
					{
						val tile = tile as Tile
						tile.isTargetted = true
					}
				}

				for (tile in ability.getValidTargets(world.player!!, world, null))
				{
					val tile = tile as Tile
					tile.isValidTarget = true
				}

				for (tile in ability.selectedTargets)
				{
					val tile = tile as Tile
					tile.isSelectedTarget = true

					for (prediction in ability.predictTargets(world.player!!, world, tile))
					{
						val tile = world.grid.tryGet(prediction.first, null) as? Tile ?: continue
						tile.isPreviewedTarget = true
						tile.previewTurns = prediction.second
					}
				}
			}
		}

		if (allCompleted)
		{
			completed = true
		}
	}

	fun completeLevel()
	{
		completing = true
		completed = false

		val playerPos = world.player!!.position()!!.position
		val maxDist = world.grid.width / 2f
		for (tile in world.grid)
		{
			val tile = tile as Tile
			tile.levelCompleting = true
			tile.levelCompleteTimer = 0f
			tile.hasCompleted = false
			tile.isSeen = true
			tile.tileCol.set(Colour.WHITE)
			tile.isTileDirty = true

			val distAlpha = 1f - clamp(tile.dist(playerPos) / maxDist, 0f, 1f)
			tile.levelCompleteDelay = distAlpha * 3f + Random.random(Random.sharedRandom, -0.5f, 0.5f)

			for (er in tile.contents.values)
			{
				val entity = er.get() ?: continue
				entity.statistics()?.renderStats = false
			}
		}
	}

	override fun registerDebugCommands(debugConsole: DebugConsole)
	{
		super.registerDebugCommands(debugConsole)

		debugConsole.register("complete", "") { args, console ->
			completeLevel()
			true
		}
	}

	val visionShadowCast = ShadowCastCache()
	val visionSet = IntSet()
	val seenSet = IntSet()
	val lastPos = Point(-1, -1)
	fun doVisibility(deltaTime: Float)
	{
		val screenTileWidth = (Statics.resolution.xFloat / world.tileSize).toInt() + 4
		val screenTileHeight = (Statics.resolution.yFloat / world.tileSize).toInt() + 4

		val playerPos = world.player!!.position()!!.position
		if (lastPos.equals(playerPos)) return

		val rawCast = visionShadowCast.getShadowCast(playerPos.x, playerPos.y, max(screenTileWidth, screenTileHeight) / 2)

		visionSet.clear()
		for (point in rawCast)
		{
			val hash = point.hashCode()
			visionSet.add(hash)
			seenSet.add(hash)
		}

		for (x in 0 until world.grid.width)
		{
			for (y in 0 until world.grid.height)
			{
				val tile = world.grid[x, y] as Tile
				val tileHash = tile.hashCode()

				val isVisible: Boolean
				val isSeen: Boolean
				if (tile.dist(playerPos) > 15)
				{
					isVisible = false
					isSeen = tile.isSeen
				}
				else
				{
					isVisible = visionSet.contains(tileHash)
					isSeen = tile.isSeen or seenSet.contains(tileHash)
				}

				if (!tile.isSeen && isSeen)
				{
					for (slot in SpaceSlot.EntityValues)
					{
						val entity = tile.contents[slot]?.get() ?: continue
						entity.ai()?.activate(entity)
					}
				}

				tile.updateVisibility(deltaTime, isSeen, isVisible)
			}
		}
	}

	override fun onTurn()
	{
		for (x in 0 until world.grid.width)
		{
			for (y in 0 until world.grid.height)
			{
				val tile = world.grid[x, y]

				for (slot in SpaceSlot.EntityValues)
				{
					val entity = tile.contents[slot]?.get() ?: continue
					val pos = entity.position()
					if (pos != null)
					{
						if (pos.lastPos == pos.position)
						{
							pos.turnsOnTile++
						}
						else
						{
							pos.turnsOnTile = 0
							pos.lastPos = pos.position
						}
					}
				}
			}
		}
	}
}