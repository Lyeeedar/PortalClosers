package com.lyeeedar.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.esotericsoftware.spine.*
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.addSystems
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Renderables.Animation.ExpandAnimation
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Systems.World
import com.lyeeedar.Systems.renderSystem
import com.lyeeedar.UI.PlayerWidget
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.random

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<Tile>

	var timeMultiplier = 1f

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)

		world = MapCreator.generateWorld("Maps/test", player, 1, 4)
		world.addSystems()

		world.tileSize = 50f

		for (i in 0 until 5)
		{
			val rat = EntityLoader.load("Entities/elemental2")
			rat.statistics()!!.calculateStatistics(1)
			rat.statistics()!!.faction = "enemy"
			rat.ai()!!.state.set(rat.getRef(), world, 0)
			rat.addComponent(ComponentType.Task)

			var tile = world.grid.random()!!
			while (tile.contents.containsKey(rat.position()!!.slot))
			{
				tile = world.grid.random()!!
			}

			rat.position()!!.position = tile
			rat.position()!!.addToTile(rat)
			world.addEntity(rat)
		}

		val topBarTable = Table()

		topBarTable.add(Table()).grow()
		mainTable.add(topBarTable).growX()
		mainTable.row()

		mainTable.add(RenderSystemWidget(world)).grow()
		mainTable.row()
		mainTable.add(PlayerWidget(world)).growX()

		debugConsole.register("time", "") { args, _ ->

			timeMultiplier = args[0].toFloat()

			true
		}

		//drawFPS = false
		world.ambientLight.set(0.8f, 0.8f, 0.8f, 1f)
	}

	override fun doRender(delta: Float)
	{
		world.update(delta * timeMultiplier)
	}
}