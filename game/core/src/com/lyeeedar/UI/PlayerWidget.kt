package com.lyeeedar.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lyeeedar.Components.equipment
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import ktx.scene2d.scene2d
import ktx.scene2d.table

class PlayerWidget(var world: World<*>) : Table()
{
	val basePanel = AssetManager.tryLoadTextureRegion("GUI/BasePanel")

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
					background = NinePatchDrawable(NinePatch(AssetManager.tryLoadTextureRegion("Icons/Active"), 4, 4, 4, 4))

					val equip = player.equipment()!!
					table { cell ->
						cell.grow()
						for (move in equip.weapon.moves)
						{
							val moveWidget = MoveWidget(move, world)
							add(moveWidget).growY().uniformX().padRight(1f)
						}
						add(Table()).grow()
					}
					row()
					table { cell ->
						cell.grow()

						val widget = AbilityWidget(equip.sigil.getAsAbility(), world)
						add(widget).growY().uniformX().padRight(1f)

						for (ab in equip.core.abilities)
						{
							val widget = AbilityWidget(ab.getAsAbility(), world)
							add(widget).growY().uniformX().padRight(1f)
						}
						add(Table()).grow()
					}
				}
			}
		}).grow()
	}
}