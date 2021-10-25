package com.lyeeedar.Screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.renderable
import com.lyeeedar.Game.Encounter
import com.lyeeedar.Game.Portal
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.UI.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Statics
import ktx.scene2d.*

class PortalScreen : AbstractScreen()
{
	val portal = Portal()

	override fun create()
	{
		portal.generate(10)
		update()
	}

	override fun getStageBatch(): Batch
	{
		return PolygonSpriteBatch(2000)
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

						stack { cell ->
							cell.pad(1f)
							add(SpriteWidget(AssetManager.loadSprite("darkest/terrain/portal_tile", drawActualSize = true), 48f, 48f))

							if (encounter.state == Encounter.EncounterState.COMPLETED)
							{
								add(SpriteWidget(AssetManager.loadSprite("Oryx/Custom/terrain/flag_complete", drawActualSize = true)))
							}
							else if (encounter.state == Encounter.EncounterState.NEXT)
							{
								add(SpriteWidget(AssetManager.loadSprite("Oryx/Custom/terrain/flag_combat", drawActualSize = true)))
								addClickListener {
										var fullscreenTable: Table? = null
										fullscreenTable = FullscreenTable.createCloseable(scene2d.table {
											label("Fight some stuff", skin = Statics.skin)
											row()
											add(SpriteWidget(AssetManager.loadSprite("darkest/terrain/portal_tile"), 48f, 48f))
											row()
											textButton("Fight", skin = Statics.skin) {
												this.addClickListener {
													val world = Statics.game.getTypedScreen<WorldScreen>()!!
													world.baseCreate()
													world.create()
													world.completionCallback = {
														Statics.game.switchScreen(ScreenEnum.PORTAL)
														portal.completeEncounter(encounter)
														update()
													}
													Statics.game.switchScreen(ScreenEnum.WORLD)
													fullscreenTable?.remove()
												}
											}
										})
									}
							}
							else if (encounter.state == Encounter.EncounterState.FUTURE)
							{
								val entity = EntityLoader.load("Entities/elemental1")
								val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
								add(SkeletonWidget(skeleton, 48f, 48f))
							}
							else
							{
								add(SpriteWidget(AssetManager.loadSprite("Oryx/Custom/terrain/flag_complete", drawActualSize = true)).tint(
									Color.DARK_GRAY))
							}
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