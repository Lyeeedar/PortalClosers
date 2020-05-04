package com.lyeeedar

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.position
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.Sprite.SpriteWrapper
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.Array2D

fun createWorld(gridStr: String): TestWorld
{
	val lines = gridStr.split("\n")
	val firstLineLen = lines[0].count()

	val entitiesToCountDown = Array<Entity>()

	var startTile: Tile? = null
	var endTile: Tile? = null
	fun createTile(x: Int, y: Int, char: Char): Tile
	{
		val tile = Tile(x, y)

		if (char == '#')
		{
			tile.wall = SpriteWrapper()
		}
		else if (char == '@')
		{
			startTile = tile
		}
		else if (char == '+')
		{
			endTile = tile
		}
		else if (char == 'A' || char == 'a')
		{
			val entity = Entity()
			entity.obtained = true
			entity.addComponent(ComponentType.Position)
			entity.addComponent(ComponentType.Task)
			entity.addComponent(ComponentType.Statistics)
			entity.position()!!.position = tile
			entity.statistics()!!.faction = "faction"

			tile.contents[SpaceSlot.ENTITY] = entity.getRef()

			if (char == 'a')
			{
				entitiesToCountDown.add(entity)
			}
		}

		return tile
	}

	val grid = Array2D<Tile>(firstLineLen, lines.count()) { x, y -> createTile(x, y, lines[y][x]) }
	val world = World(grid)
	for (tile in grid)
	{
		tile.world = world
	}

	return TestWorld(world, startTile!!, endTile!!, entitiesToCountDown)
}

class TestWorld(val world: World<Tile>, val startTile: Tile, val endTile: Tile, val entitiesToCountDown: Array<Entity>)