package com.lyeeedar.UI

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.equipment
import com.lyeeedar.Util.AssetManager
import ktx.scene2d.scene2d
import ktx.scene2d.table

class PlayerPreviewWidget(val player: Entity) : Table()
{
	val basePanel = AssetManager.tryLoadTextureRegion("GUI/BasePanel")

	init
	{
		update()
	}

	fun update()
	{
		clear()

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
							val moveWidget = MovePreviewWidget(move)
							add(moveWidget).growY().uniformX().padRight(1f)
						}
						add(Table()).grow()
					}
					row()
					table { cell ->
						cell.grow()

						val widget = AbilityPreviewWidget(equip.sigil.getAsAbility())
						add(widget).growY().uniformX().padRight(1f)

						for (ab in equip.core.abilities)
						{
							val widget = AbilityPreviewWidget(ab.getAsAbility())
							add(widget).growY().uniformX().padRight(1f)
						}
						add(Table()).grow()
					}
				}
			}
		}).grow()
	}
}