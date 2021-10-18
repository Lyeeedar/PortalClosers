package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.weapon
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Statics
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

		add(LaunchButton()).expandX().height(24f).center()
		row()
		add(Table()).growX().height(24f).pad(1f) // will be buffs
		row()
		add(scene2d.table {
			background = TextureRegionDrawable(basePanel)

			add(HealthBarWidget(player)).growY().width(Value.percentHeight(1f, this))

			table {
				cell -> cell.grow()
				add(ResourcesWidget(player)).growX().height(24f).padLeft(2f).padTop(6f)
				row()
				table { cell ->
					cell.grow().padLeft(2f).padBottom(5f)
					background = NinePatchDrawable(NinePatch(AssetManager.loadTextureRegion("Icons/Active"), 4, 4, 4, 4))

					table { cell ->
						cell.grow()
						for (move in player.weapon()!!.weapon.moves)
						{
							val moveWidget = MoveWidget(move, world)
							add(moveWidget).growY().uniformX().padRight(1f)
						}
						add(Table()).grow()
					}
					row()
					table { cell ->
						cell.grow()
						val ability = player.ability()
						if (ability != null)
						{
							for (ab in ability.abilities)
							{
								val widget = AbilityWidget(ab, world)
								add(widget).growY().uniformX().padRight(1f)
							}
						}
						add(Table()).grow()
					}
				}
			}
		}).grow()
	}
}