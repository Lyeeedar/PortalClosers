package com.lyeeedar.Screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityOrb
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Systems.World
import com.lyeeedar.UI.EntityWidget
import com.lyeeedar.UI.PlayerWidget
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.getXml

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<*>

	var timeMultiplier = 1f

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)
		val ability = player.addOrGet(ComponentType.Ability) as AbilityComponent
		val abilityOrb = AbilityOrb()
		abilityOrb.load(getXml("Abilities/Fire/Firebolt"))
		ability.ability1 = Ability(abilityOrb.getAbility(1))
		ability.ability2 = Ability(abilityOrb.getAbility(2))
		ability.ability3 = Ability(abilityOrb.getAbility(3))
		ability.ability4 = Ability(abilityOrb.getAbility(4))

		world = MapCreator.generateWorld("Maps/test", "Factions/Rats", player, 1, 2)

		val topBarTable = Table()
		val packTable = Table()

		val pack = player.pack()!!
		for (ent in pack.pack.mobs)
		{
			val widget = EntityWidget(ent, world)
			packTable.add(widget).size(48f).pad(5f)
			packTable.row()
		}
		topBarTable.add(Table()).grow()
		topBarTable.add(packTable).width(48f)
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