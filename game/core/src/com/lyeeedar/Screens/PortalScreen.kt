package com.lyeeedar.Screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
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
import com.lyeeedar.Util.Future
import com.lyeeedar.Util.Statics
import ktx.actors.alpha
import ktx.actors.then
import ktx.scene2d.*
import kotlin.random.Random

class PortalScreen : AbstractScreen()
{
	val portal = Portal()
	val clouds1 = CloudEffect(10f, 3f, 2f)
	lateinit var scroll: ScrollPane
	val pathTable = Table()

	override fun create()
	{
		portal.generate(20)
		update()

		clouds1.alpha = 0.6f
		stage.addActor(clouds1)
		clouds1.toBack()

		backgroundColor.set(30f / 255f, 45f / 255f, 51f / 255f, 1f)

		scroll = ScrollPane(pathTable, Statics.skin, "noBar")
		scroll.setFlingTime(0.2f)
		scroll.setScrollingDisabled(true, false)
		scroll.setOverscroll(false, false)
		mainTable.add(scroll).fill()
	}

	override fun getStageBatch(): Batch
	{
		return PolygonSpriteBatch(2000)
	}

	fun update()
	{
		var playerTile: Widget? = null

		val pathTable = scene2d.table {
			padTop(400f)
			padBottom(50f)
			for (y in portal.encounters.size-1 downTo 0)
			{
				val row = portal.encounters[y]

				table {
					for (x in 0 until row.size)
					{
						val encounter = row[x]

						stack { cell ->
							cell.pad(1f)

							if (encounter.state != Encounter.EncounterState.SKIPPED)
							{
								add(SpriteWidget(AssetManager.loadSprite("darkest/terrain/portal_tile", drawActualSize = true), 48f, 48f))
							}
							else if (!encounter.animatedDrop)
							{
								val widget = SpriteWidget(AssetManager.loadSprite("darkest/terrain/portal_tile", drawActualSize = true), 48f, 48f)
								widget.addAction(parallel(moveBy(0f, -200f, 0.5f, Interpolation.pow2Out), fadeOut(0.5f, Interpolation.pow2Out)))
								add(widget)

								encounter.animatedDrop = true
							}
							else
							{
								add(SpriteWidget(AssetManager.loadSprite("blank"), 48f, 48f))
							}

							if (encounter.state == Encounter.EncounterState.COMPLETED)
							{

							}
							else if (encounter.state == Encounter.EncounterState.CURRENT)
							{
								val entity = EntityLoader.load("Entities/player")
								val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
								playerTile = SkeletonWidget(skeleton, 48f, 48f)
								add(playerTile)
							}
							else if (encounter.state == Encounter.EncounterState.NEXT)
							{
								val entity = EntityLoader.load("Entities/elemental1")
								val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
								add(SkeletonWidget(skeleton, 48f, 48f))
								addClickListener {
										var fullscreenTable: Table? = null
										fullscreenTable = FullscreenTable.createCloseable(scene2d.table {
											label("Fight some stuff", skin = Statics.skin)
											row()
											add(SpriteWidget(AssetManager.loadSprite("darkest/terrain/portal_tile"), 48f, 48f))
											row()
											textButton("Fight", skin = Statics.skin) {
												this.addClickListener {
//													val world = Statics.game.getTypedScreen<WorldScreen>()!!
//													world.baseCreate()
//													world.create()
//													world.completionCallback = {
//														Statics.game.switchScreen(ScreenEnum.PORTAL)
//														portal.completeEncounter(encounter)
//														update()
//													}
//													Statics.game.switchScreen(ScreenEnum.WORLD)
													portal.completeEncounter(encounter)
													update()
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
								add(SkeletonWidget(skeleton, 48f, 48f).tint(Color.LIGHT_GRAY))
							}
							else
							{

							}
						}
					}
				}
				row()
			}
		}

		this.pathTable.clear()
		this.pathTable.add(pathTable).fill()

		Future.call(0.1f) {
			val pos = playerTile!!.localToActorCoordinates(pathTable, Vector2())
			scroll.scrollTo(0f, pos.y-stage.height*0.3f, playerTile!!.width, playerTile!!.height+stage.height*0.75f)
		}
	}

	var activeLightning: Actor? = null
	var lightningY = 0f

	var lightningTimer = 0f
	override fun doRender(delta: Float)
	{
		lightningTimer += delta
		if (lightningTimer > 0f)
		{
			lightningTimer -= 2f + Random.nextFloat() * 10f

			val particle = AssetManager.loadParticleEffect("darkest/cloud_lightning")
			particle.timeMultiplier = 0.9f + Random.nextFloat() * 0.2f
			val particleEffectActor = ParticleEffectActor(particle.getParticleEffect()) {
				activeLightning = null
			}
			particleEffectActor.setPosition(stage.width * Random.nextFloat(), stage.height * Random.nextFloat())
			lightningY = particleEffectActor.y

			particleEffectActor.setSize(20f, 20f)
			particleEffectActor.alpha = 0.5f + 0.5f * Random.nextFloat()
			stage.addActor(particleEffectActor)
			particleEffectActor.toBack()

			activeLightning = particleEffectActor
		}

		activeLightning?.setPosition(activeLightning!!.x, lightningY+scroll.scrollY * 0.5f)
		clouds1.setPosition(0f, scroll.scrollY * -0.5f)
	}
}