package com.lyeeedar.Systems

import com.badlogic.gdx.utils.IntSet
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.ShadowCastCache
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.Statics
import com.lyeeedar.Util.max

class TileSystem(world: World<*>) : AbstractSystem(world)
{
	override fun doUpdate(deltaTime: Float)
	{
		doVisibility(deltaTime)

		for (x in 0 until world.grid.width)
		{
			for (y in 0 until world.grid.height)
			{
				val tile = world.grid[x, y] as Tile

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

				if (tile.isTargetted)
				{
					tile.isTargetted = false
					tile.tileCol = Colour.WHITE
					tile.isTileDirty = true
				}

				if (tile.predictedAttacksFrom.size > 0)
				{
					val itr = tile.predictedAttacksFrom.iterator()
					while (itr.hasNext())
					{
						val attack = itr.next()
						if (!attack.isValid())
						{
							itr.remove()
							tile.isTileDirty = true
						}
					}
				}
			}
		}

		val weapon = world.player?.weapon()
		if (weapon != null)
		{
			for (move in weapon.weapon.moves)
			{
				val ab = move.getAsAbility()
				if (ab.isSelected)
				{
					for (tile in ab.getValidTargets(world.player!!, world, null))
					{
						val tile = tile as Tile
						tile.isTargetted = true
						tile.tileCol = Colour.GOLD
						tile.isTileDirty = true
					}
				}
			}
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