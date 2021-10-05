package com.lyeeedar.Screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityOrb
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.addSystems
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Systems.World
import com.lyeeedar.UI.EntityWidget
import com.lyeeedar.UI.PlayerWidget
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.getXml
import com.lyeeedar.Util.random

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<Tile>

	var timeMultiplier = 1f

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)

		world = MapCreator.generateWorld("Maps/test", player, 1, 2)
		world.addSystems()

		val rat = EntityLoader.load("Entities/rat")
		rat.statistics()!!.calculateStatistics(1)
		rat.statistics()!!.faction = "enemy"
		rat.ai()!!.state.set(rat.getRef(), world, 0)
		rat.addComponent(ComponentType.Task)
		rat.position()!!.position = world.grid.random()!!
		rat.position()!!.addToTile(rat)
		world.addEntity(rat)

		val topBarTable = Table()

		topBarTable.add(Table()).grow()
		mainTable.add(topBarTable).growX()
		mainTable.row()

		mainTable.add(RenderSystemWidget(world)).grow()
		mainTable.row()
		mainTable.add(PlayerWidget(world)).height(48f).growX().padBottom(15f)

		debugConsole.register("time", "") { args, _ ->

			timeMultiplier = args[0].toFloat()

			true
		}
	}

	override fun doRender(delta: Float)
	{
		world.update(delta * timeMultiplier)
	}
}