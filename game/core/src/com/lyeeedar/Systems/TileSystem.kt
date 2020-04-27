package com.lyeeedar.Systems

import com.badlogic.gdx.utils.IntSet
import com.lyeeedar.Components.position
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.ShadowCastCache
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.Colour
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

				for (slot in SpaceSlot.Values)
				{
					val entity = tile.contents[slot]
					if (entity != null && !entity.isValid())
					{
						tile.contents.remove(slot)
					}
				}
			}
		}
	}

	val visionShadowCast = ShadowCastCache()
	val visionSet = IntSet()
	val seenSet = IntSet()
	fun doVisibility(deltaTime: Float)
	{
		val screenTileWidth = (Statics.resolution.x.toFloat() / world.tileSize).toInt() + 4
		val screenTileHeight = (Statics.resolution.y.toFloat() / world.tileSize).toInt() + 4

		val playerPos = world.player!!.position()!!.position
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

				val isVisible = visionSet.contains(tileHash)
				val isSeen = seenSet.contains(tileHash)

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