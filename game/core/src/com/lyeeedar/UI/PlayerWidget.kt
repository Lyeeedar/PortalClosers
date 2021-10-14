package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.lyeeedar.Components.weapon
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager

class PlayerWidget(val world: World<*>) : Table()
{
	val emptySlot = AssetManager.loadSprite("Icons/Empty")

	init
	{
		update()
	}

	fun update()
	{
		clear()

		val player = world.player!!

		val hp = HealthBarWidget(player)
		add(hp).growX().height(10f).pad(3f)
		row()
		val resourcesTable = ResourcesWidget(player)
		add(resourcesTable).growX().height(24f).pad(3f)
		row()
		val movesTable = Table()
		for (move in player.weapon()!!.weapon.moves)
		{
			val moveWidget = MoveWidget(move, world)
			movesTable.add(moveWidget).growY().uniformX().padLeft(2f)
		}
		movesTable.add(Table()).grow()

		add(movesTable).growX().height(32f).pad(2f)
		row()
	}
}