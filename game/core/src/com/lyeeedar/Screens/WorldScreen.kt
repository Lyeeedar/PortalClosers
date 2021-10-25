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
import com.lyeeedar.Systems.*
import com.lyeeedar.UI.PlayerWidget
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.random

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<Tile>
	lateinit var entities: EntitySignature

	var timeMultiplier = 1f

	lateinit var completionCallback: () -> Unit

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)

		world = MapCreator.generateWorld("Maps/test", player, 1, 4)
		world.addSystems()

		world.tileSize = 50f

		for (i in 0 until 5)
		{
			val rat = EntityLoader.load("Entities/elemental${Random.sharedRandom.nextInt(2)+1}")
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

		debugConsole.commands.clear()
		debugConsole.register("time", "") { args, _ ->

			timeMultiplier = args[0].toFloat()

			true
		}

		//drawFPS = false
		world.ambientLight.set(0.8f, 0.8f, 0.8f, 1f)

		for (system in world.systems)
		{
			system.registerDebugCommands(debugConsole)
		}

		entities = world.getEntitiesFor().all(ComponentType.Statistics).get()
	}

	override fun doRender(delta: Float)
	{
		world.update(delta * timeMultiplier)

		val tileSystem = world.tileSystem()!!
		if (tileSystem.completed)
		{
			world.free()
			completionCallback.invoke()
		}
		else if (!tileSystem.completing)
		{
			var hasEnemies = false
			for (entity in entities.entities)
			{
				if (entity.isEnemies(world.player!!))
				{
					hasEnemies = true
					break
				}
			}
			if (!hasEnemies)
			{
				tileSystem.completeLevel()
			}
		}
	}
}