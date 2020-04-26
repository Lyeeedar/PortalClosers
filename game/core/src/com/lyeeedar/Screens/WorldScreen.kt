package com.lyeeedar.Screens

import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Systems.World

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<*>

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		world = MapCreator.generateWorld("Maps/test", "Factions/Rats", player, 1, 0)
	}

	override fun doRender(delta: Float)
	{
		world.update(delta)
	}
}