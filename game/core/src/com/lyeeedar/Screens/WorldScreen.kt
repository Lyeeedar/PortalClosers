package com.lyeeedar.Screens

import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.statistics
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Systems.World
import com.lyeeedar.UI.RenderSystemWidget

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<*>

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)
		world = MapCreator.generateWorld("Maps/test", "Factions/Rats", player, 1, 2)

		mainTable.add(RenderSystemWidget(world)).grow()
	}

	override fun doRender(delta: Float)
	{
		world.update(delta)
	}
}