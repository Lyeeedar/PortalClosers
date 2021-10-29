package com.lyeeedar.Screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.renderable
import com.lyeeedar.Game.Portal.AbstractEncounter
import com.lyeeedar.Game.Portal.Biome
import com.lyeeedar.Game.Portal.EncounterState
import com.lyeeedar.Game.Portal.Portal
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.UI.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Future
import com.lyeeedar.Util.Statics
import com.lyeeedar.Util.getXml
import ktx.actors.alpha
import ktx.actors.centerPosition
import ktx.scene2d.*
import kotlin.random.Random

class PortalScreen : AbstractScreen()
{
	val portal = Portal()
	lateinit var scroll: ScrollPane
	val pathTable = Table()

	override fun create()
	{
		val biome = Biome()
		biome.load(getXml("Biomes/metalBiome"))

		portal.generate(20, biome)
		update()

		backgroundColor.set(35f / 255f, 43f / 255f, 45f / 255f, 1f)

		scroll = ScrollPane(pathTable, Statics.skin, "noBar")
		scroll.setFlingTime(0.2f)
		scroll.setScrollingDisabled(true, false)
		scroll.setOverscroll(false, false)
		mainTable.add(scroll).grow()
		mainTable.row()
		mainTable.add(PlayerPreviewWidget(portal.player)).fillX()
	}

	override fun getStageBatch(): Batch
	{
		return PolygonSpriteBatch(2000)
	}

	fun update()
	{
		var playerTile: Widget? = null

		val pathTable = scene2d.table {
			add(Table()).height(300f)
			row()
			for (y in portal.encounters.size-1 downTo 0)
			{
				val row = portal.encounters[y]

				table {
					for (x in 0 until row.size)
					{
						val encounter = row[x]

						stack { cell ->
							cell.pad(1f)

							val tint = if (encounter.state == EncounterState.FUTURE) Color.LIGHT_GRAY else Color.WHITE

							if (encounter.state != EncounterState.SKIPPED)
							{
								add(SpriteWidget(AssetManager.loadSprite("darkest/terrain/portal_tile", drawActualSize = true), 48f, 48f).tint(tint))
							}
							else if (!encounter.animatedDrop)
							{
								val widget = SpriteWidget(AssetManager.loadSprite("darkest/terrain/portal_tile", drawActualSize = true), 48f, 48f).tint(tint)
								widget.addAction(parallel(moveBy(0f, -200f, 0.5f, Interpolation.pow2Out), fadeOut(0.5f, Interpolation.pow2Out)))
								add(widget)

								encounter.animatedDrop = true
							}
							else
							{
								add(SpriteWidget(AssetManager.loadSprite("blank"), 48f, 48f))
							}

							if (encounter.state == EncounterState.COMPLETED || encounter.state == EncounterState.SKIPPED)
							{

							}
							else if (encounter.state == EncounterState.CURRENT)
							{
								val skeleton = portal.player.renderable()!!.renderable as SkeletonRenderable
								playerTile = SkeletonWidget(skeleton, 48f, 48f)
								add(playerTile)
							}
							else
							{
								var widget: Widget = encounter.getMapWidget()
								widget = widget.tint(tint) as Widget

								addClickListener {
									createEncounterTable(encounter)
								}

								add(widget)
							}
						}
					}
				}
				row()
			}
			add(Table()).height(50f)
		}

		this.pathTable.clear()
		this.pathTable.add(pathTable).fill()

		Future.call(0.1f) {
			val pos = playerTile!!.localToActorCoordinates(pathTable, Vector2())
			scroll.scrollTo(0f, pos.y-stage.height*0.3f, playerTile!!.width, playerTile!!.height+stage.height*0.75f)
		}
	}

	fun createEncounterTable(encounter: AbstractEncounter)
	{
		val greyout = createGreyoutTable(stage, opacity = 0.6f)

		val backgroundTable = scene2d.table {
			table { cell ->
				cell.grow()
				background = NinePatchDrawable(NinePatch(AssetManager.tryLoadTextureRegion("Sprites/GUI/background.png"), 24, 24, 24, 24))
			}
		}

		val table = scene2d.table {
			val root = it

			table { cell ->
				cell.width(this@PortalScreen.stage.width * 0.8f)
				table {
					background = NinePatchDrawable(
						NinePatch(
							AssetManager.tryLoadTextureRegion("Sprites/GUI/background.png"),
							24,
							24,
							24,
							24
						         )
					                              )
					label(encounter.title, skin = Statics.skin)
				}
				row()

				label(encounter.description, skin = Statics.skin) { cell ->
					cell.growX()
					cell.pad(10f)
					wrap = true
				}
				row()

				add(encounter.createPreviewTable()).padBottom(15f)
				row()

				if (encounter.state == EncounterState.NEXT)
				{
					table {
						for (action in encounter.actions(this@PortalScreen))
						{
							textButton(action.first, skin = Statics.skin) { cell ->
								cell.pad(2f)
								this.addClickListener {
									action.second.invoke()
									root.remove()
									greyout.remove()
									backgroundTable.remove()
								}
							}
						}
					}
				}
			}
		}

		table.pack()
		table.centerPosition(scroll.width, scroll.height)
		table.setPosition(table.x + scroll.x, table.y + scroll.y)
		table.touchable = Touchable.enabled

		val actionsOffset = if (encounter.state == EncounterState.NEXT) 15f else 0f

		backgroundTable.width = table.width
		backgroundTable.height = table.height-(25f + actionsOffset)
		backgroundTable.x = table.x
		backgroundTable.y = table.y+actionsOffset
		backgroundTable.touchable = Touchable.enabled
		backgroundTable.layout()

		greyout.addClickListener {
			table.remove()
			backgroundTable.remove()
			greyout.remove()
		}

		Statics.stage.addActor(backgroundTable)
		Statics.stage.addActor(table)
	}

	var activeLightning: Actor? = null
	var lightningY = 0f
	var lightningScrollY = 0f

	var lightningTimer = 0f
	override fun doRender(delta: Float)
	{
		lightningTimer += delta
		if (lightningTimer > 0f)
		{
			lightningTimer -= 2f + Random.nextFloat() * 5f

			val particle = AssetManager.loadParticleEffect("darkest/cloud_lightning")
			particle.timeMultiplier = 0.9f + Random.nextFloat() * 0.2f
			val particleEffectActor = ParticleEffectActor(particle.getParticleEffect()) {
				activeLightning = null
			}
			particleEffectActor.setPosition(stage.width * Random.nextFloat(), stage.height * Random.nextFloat())
			lightningY = particleEffectActor.y
			lightningScrollY = scroll.scrollY

			val size = 15f + 15f * Random.nextFloat()
			particleEffectActor.setSize(size, size)
			particleEffectActor.alpha = 0.5f + 0.5f * Random.nextFloat()
			stage.addActor(particleEffectActor)
			particleEffectActor.toBack()

			activeLightning = particleEffectActor
		}

		activeLightning?.setPosition(activeLightning!!.x, lightningY+(scroll.scrollY - lightningScrollY) * 0.5f)
	}
}