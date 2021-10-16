package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.weapon
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import ktx.scene2d.scene2d
import ktx.scene2d.table

class PlayerWidget(val world: World<*>) : Table()
{
	val basePanel = AssetManager.loadTextureRegion("GUI/BasePanel")

	init
	{
		update()
	}

	fun update()
	{
		clear()

		val player = world.player!!

		add(Table()).growX().height(24f).pad(1f) // will be buffs
		row()
		add(scene2d.table {
			background = TextureRegionDrawable(basePanel)

			add(HealthBarWidget(player)).growY().width(Value.percentHeight(1f, this))

			table {
				cell -> cell.grow()
				add(ResourcesWidget(player)).growX().height(16f).pad(1f)
				row()
				table { cell ->
					cell.grow().padLeft(2f)
					for (move in player.weapon()!!.weapon.moves)
					{
						val moveWidget = MoveWidget(move, world)
						add(moveWidget).growY().uniformX().padLeft(1f)
					}
					add(Table()).grow()
				}
				row()
				table { cell ->
					cell.grow().pad(2f)
					val ability = player.ability()
					if (ability != null)
					{
						for (ab in ability.abilities)
						{
							val widget = AbilityWidget(ab, world)
							add(widget).growY().uniformX().padLeft(1f)
						}
					}
					add(Table()).grow()
				}
			}
		}).grow()
	}
}