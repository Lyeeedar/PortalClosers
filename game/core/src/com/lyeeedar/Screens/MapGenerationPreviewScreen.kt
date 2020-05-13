package com.lyeeedar.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Tile
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Systems.AbstractRenderSystem
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.Point

class MapGenerationPreviewScreen : AbstractScreen()
{
	lateinit var world: World<Tile>
	val viewPos = Vector2()

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)

		world = MapCreator.generateWorld("Maps/test", "Factions/Rats", player, 1, 2)

		for (tile in world.grid)
		{
			tile.isSeen = true
			tile.updateVisibility(1000f, true, true)
		}

		world.systems.add(MapGenRenderSystem(world, this))

		viewPos.set(world.grid.width * 0.5f, world.grid.height * 0.5f)
	}

	override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean
	{
		viewPos.sub(deltaX / world.tileSize, -deltaY / world.tileSize)
		return true
	}

	override fun scrolled(amount: Int): Boolean
	{
		world.tileSize -= amount

		return super.scrolled(amount)
	}

	override fun doRender(delta: Float)
	{
		world.update(delta)
	}
}

class MapGenRenderSystem(world: World<*>, val screen: MapGenerationPreviewScreen) : AbstractRenderSystem(world)
{
	override fun drawExtraEntity(entity: Entity, deltaTime: Float)
	{

	}

	override fun getPlayerPosition(deltaTime: Float): Vector2
	{
		return screen.viewPos
	}
}