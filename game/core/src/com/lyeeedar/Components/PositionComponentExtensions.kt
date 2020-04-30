package com.lyeeedar.Components

import com.lyeeedar.Direction
import com.lyeeedar.Game.Tile
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Util.Point

var PositionComponent.tile: Tile?
	get() = position as? Tile
	set(value)
	{
		if (value != null) position = value
	}

fun PositionComponent.isOnTile(point: Point): Boolean
{
	val tile = this.tile ?: return false

	for (x in 0 until size)
	{
		for (y in 0 until size)
		{
			val t = tile.world.grid.tryGet(tile, x, y, null) ?: continue
			if (t == point) return true
		}
	}
	return false
}

fun PositionComponent.getEdgeTiles(dir: Direction): com.badlogic.gdx.utils.Array<Tile>
{
	val tile = position as? Tile
	           ?: throw Exception("Position must be a tile!")

	var xstep = 0
	var ystep = 0

	var sx = 0
	var sy = 0

	if ( dir == Direction.NORTH )
	{
		sx = 0
		sy = size - 1

		xstep = 1
		ystep = 0
	}
	else if ( dir == Direction.SOUTH )
	{
		sx = 0
		sy = 0

		xstep = 1
		ystep = 0
	}
	else if ( dir == Direction.EAST )
	{
		sx = size - 1
		sy = 0

		xstep = 0
		ystep = 1
	}
	else if ( dir == Direction.WEST )
	{
		sx = 0
		sy = 0

		xstep = 0
		ystep = 1
	}

	val tiles = com.badlogic.gdx.utils.Array<Tile>(1)
	for (i in 0 until size)
	{
		val t = tile.world.grid.tryGet(tile, sx + xstep * i, sy + ystep * i, null) as? Tile ?: continue
		tiles.add(t)
	}

	return tiles
}

fun PositionComponent.isValidTile(t: AbstractTile, entity: Entity): Boolean
{
	for (x in 0 until size)
	{
		for (y in 0 until size)
		{
			val tile = t.world.grid.tryGet(t, x, y, null)
			if (tile == null || tile.wall != null || tile.contents.get(SpaceSlot.WALL) != null || (tile.contents.get(slot) != null && tile.contents.get(slot)?.get() != entity))
			{
				return false
			}
		}
	}

	return true
}

fun PositionComponent.removeFromTile(entity: Entity)
{
	if (tile == null) return

	for (x in 0 until size)
	{
		for (y in 0 until size)
		{
			val tile = tile!!.world.grid.tryGet(tile!!, x, y, null) ?: continue
			if (tile.contents[slot]?.get() == entity) tile.contents.remove(slot)
		}
	}
}

fun PositionComponent.addToTile(entity: Entity)
{
	val ref = EntityReference(entity)

	val t = tile!!
	for (x in 0 until size)
	{
		for (y in 0 until size)
		{
			val tile = t.world.grid.tryGet(t, x, y, null) ?: continue
			if (tile.contents[slot] != null) throw RuntimeException("Tile wasnt empty!")
			tile.contents[slot] = ref
		}
	}
}
fun Entity.addToTile(tile: Tile)
{
	val pos = addOrGet(ComponentType.Position) as PositionComponent
	pos.position = tile
	pos.addToTile(this)
}

fun PositionComponent.doMove(t: AbstractTile, entity: Entity)
{
	removeFromTile(entity)

	position = t

	addToTile(entity)
}
