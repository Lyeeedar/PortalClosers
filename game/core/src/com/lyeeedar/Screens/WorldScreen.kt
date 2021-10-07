package com.lyeeedar.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.esotericsoftware.spine.*
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.addSystems
import com.lyeeedar.MapGeneration.MapCreator
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Systems.World
import com.lyeeedar.UI.PlayerWidget
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.random

class WorldScreen : AbstractScreen()
{
	lateinit var world: World<Tile>

	var timeMultiplier = 1f

	fun setSkeleton(entity: Entity)
	{
		val atlas = TextureAtlas(Gdx.files.internal("NewProject_1.atlas"))
		val json = SkeletonJson(atlas) // This loads skeleton JSON data, which is stateless.

		json.scale = 0.2f // Load the skeleton at 60% the size it was in Spine.

		val skeletonData: SkeletonData = json.readSkeletonData(Gdx.files.internal("NewProject_1.json"))

		val skeleton = Skeleton(skeletonData) // Skeleton holds skeleton state (bone positions, slot attachments, etc).

		val stateData = AnimationStateData(skeletonData) // Defines mixing (crossfading) between animations.

		stateData.setMix("idle", "walk", 0.2f)
		stateData.setMix("walk", "idle", 0.2f)

		val state = AnimationState(stateData) // Holds the animation state for a skeleton (current animation, time, etc).

		state.setTimeScale(0.5f) // Slow all animations down to 50% speed.


		// Queue animations on track 0.

		// Queue animations on track 0.
		state.setAnimation(0, "idle", true)
		state.addAnimation(0, "walk", false, 2f) // Jump after 2 seconds.

		state.addAnimation(0, "idle", true, 0f) // Run after the jump.

		state.addAnimation(0, "walk", true, 6f) // Jump after 2 seconds.

		entity.removeComponent(ComponentType.DirectionalSprite)
		entity.renderable()!!.renderable = SkeletonRenderable(skeleton, state)
	}

	override fun create()
	{
		val player = EntityLoader.load("Entities/player")
		player.statistics()!!.calculateStatistics(1)

		world = MapCreator.generateWorld("Maps/test", player, 1, 4)
		world.addSystems()

		setSkeleton(player)

		for (i in 0 until 5)
		{
			val rat = EntityLoader.load("Entities/rat")
			rat.statistics()!!.calculateStatistics(1)
			rat.statistics()!!.faction = "enemy"
			rat.ai()!!.state.set(rat.getRef(), world, 0)
			rat.addComponent(ComponentType.Task)
			rat.position()!!.position = world.grid.random()!!
			rat.position()!!.addToTile(rat)
			world.addEntity(rat)
		}

		val topBarTable = Table()

		topBarTable.add(Table()).grow()
		mainTable.add(topBarTable).growX()
		mainTable.row()

		mainTable.add(RenderSystemWidget(world)).grow()
		mainTable.row()
		mainTable.add(PlayerWidget(world)).height(48f).growX().padBottom(15f)

		debugConsole.register("time", "") { args, _ ->

			timeMultiplier = args[0].toFloat()

			true
		}
	}

	override fun doRender(delta: Float)
	{
		world.update(delta * timeMultiplier)
	}
}