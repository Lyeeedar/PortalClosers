package com.lyeeedar.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.esotericsoftware.spine.*
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Portal.Biome
import com.lyeeedar.Game.Portal.CombatEncounter
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.addSystems
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Renderables.Animation.ExpandAnimation
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Systems.*
import com.lyeeedar.UI.PlayerWidget
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.getXml
import com.lyeeedar.Util.random

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<Tile>
	lateinit var entities: EntitySignature

	var timeMultiplier = 1f

	lateinit var completionCallback: () -> Unit

	lateinit var renderSystemWidget: RenderSystemWidget
	lateinit var playerWidget: PlayerWidget

	override fun create()
	{

	}

	var beenCreated = false
	fun setupUI()
	{
		if (beenCreated)
		{
			renderSystemWidget.world = world
			playerWidget.world = world
			playerWidget.update()
			return
		}
		beenCreated = true

		val topBarTable = Table()

		topBarTable.add(Table()).grow()
		mainTable.add(topBarTable).growX()
		mainTable.row()

		renderSystemWidget = RenderSystemWidget(world)
		playerWidget = PlayerWidget(world)

		mainTable.add(renderSystemWidget).grow()
		mainTable.row()
		mainTable.add(playerWidget).growX()
	}

	fun createWorld(player: Entity, encounter: CombatEncounter)
	{
		val biome = Biome()
		biome.load(getXml("Biomes/metalBiome"))

		world = MapCreator.generateWorld(biome, biome.normalPacks.random(), player, 1, 4)
		world.addSystems()

		world.tileSize = 50f

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

		setupUI()
	}

	override fun doRender(delta: Float)
	{
		world.update(delta * timeMultiplier)

		val tileSystem = world.tileSystem()!!
		if (tileSystem.completed)
		{
			world.removeEntity(world.player!!)
			world.update(0f)
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