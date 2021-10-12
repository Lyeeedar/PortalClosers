package com.lyeeedar.Screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.lyeeedar.Game.Encounter
import com.lyeeedar.Game.Portal
import com.lyeeedar.UI.SpriteWidget
import com.lyeeedar.UI.addClickListener
import com.lyeeedar.UI.tint
import com.lyeeedar.Util.AssetManager
import ktx.scene2d.scene2d
import ktx.scene2d.table

class PortalScreen : AbstractScreen()
{
	val portal = Portal()

	override fun create()
	{
		portal.generate(10)
		update()
	}

	fun update()
	{
		mainTable.clear()
		val pathTable = scene2d.table {
			for (y in portal.encounters.size-1 downTo 0)
			{
				val row = portal.encounters[y]

				table {
					for (x in 0 until row.size)
					{
						val encounter = row[x]

						if (encounter.state == Encounter.EncounterState.COMPLETED)
						{
							add(SpriteWidget(AssetManager.loadSprite("Oryx/Custom/terrain/flag_complete", drawActualSize = true)))
						}
						else if (encounter.state == Encounter.EncounterState.NEXT)
						{
							val next = SpriteWidget(AssetManager.loadSprite("Oryx/Custom/terrain/flag_combat", drawActualSize = true))
							next.addClickListener {
								portal.completeEncounter(encounter)
								update()
							}
							add(next)
						}
						else if (encounter.state == Encounter.EncounterState.FUTURE)
						{
							add(SpriteWidget(AssetManager.loadSprite("Oryx/Custom/terrain/flag_enemy", drawActualSize = true)))
						}
						else
						{
							add(SpriteWidget(AssetManager.loadSprite("Oryx/Custom/terrain/flag_complete", drawActualSize = true)).tint(
								Color.DARK_GRAY))
						}
					}
				}
				row()
			}
		}
		mainTable.add(pathTable).fill()
	}

	override fun doRender(delta: Float)
	{

	}
}