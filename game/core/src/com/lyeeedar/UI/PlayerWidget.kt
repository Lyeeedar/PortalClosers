package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.weapon
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import ktx.scene2d.scene2d
import ktx.scene2d.table

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

		debug()

		val left = scene2d.table {
			add(ResourcesWidget(player)).growX().height(24f).pad(3f)
			row()
			table { cell ->
				cell.grow()
				for (move in player.weapon()!!.weapon.moves)
				{
					val moveWidget = MoveWidget(move, world)
					add(moveWidget).growY().uniformX().padLeft(2f)
				}
				add(Table()).grow()
			}
		}

		val right = scene2d.table {
			add(Table()).growX().height(24f).pad(3f) // will be buffs
			row()
			table { cell ->
				cell.grow()
				val ability = player.ability()
				if (ability != null)
				{
					for (ab in ability.abilities)
					{
						val widget = AbilityWidget(ab, world)
						add(widget).growY().uniformX().padLeft(2f)
					}
				}
				add(Table()).grow()
			}
		}

		val hp = HealthBarWidget(player)

		add(left).growY().width(Value.percentWidth(0.4f, this))
		add(hp).uniform().width(Value.percentWidth(0.2f, this))
		add(right).growY().width(Value.percentWidth(0.4f, this))
	}
}