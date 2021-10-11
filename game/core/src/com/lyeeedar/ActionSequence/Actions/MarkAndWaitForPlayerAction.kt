package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.math.Bezier
import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.Animation.AlphaAnimation
import com.lyeeedar.Renderables.Animation.BlinkAnimation
import com.lyeeedar.Renderables.CurveRenderable
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.XmlData
import ktx.collections.set

@DataClass(category = "Meta", colour = "199,18,117", name = "MarkAndWait")
class MarkAndWaitForPlayerAction : AbstractOneShotActionSequenceAction()
{
	val key = "waitForPlayer"
	var turns: Int = 1

	override fun isDelayed(state: ActionSequenceState): Boolean
	{
		if (!state.detached && state.source.isValid())
		{
			// check if the source entity is being acted on
			val pos = state.source.get()?.position()?.position
			if (pos != null)
			{
				val tile = state.world.grid.tryGet(pos, null)
				if (tile != null)
				{
					if (tile.tileContainsDelayedAction(state.getRef()))
					{
						return true
					}
				}
			}
		}

		return false
	}

	override fun isBlocked(state: ActionSequenceState): Boolean
	{
		val counter = state.data[key] as Int? ?: 0
		return counter > 0
	}

	override fun enter(state: ActionSequenceState)
	{
		state.data[key] = turns

		val markers = Array<EntityReference>()

		// also mark the tiles
		for (point in state.targets)
		{
			val tile = state.world.grid[point] as? Tile ?: continue
			tile.predictedAttacksFrom.add(state.getRef())
			tile.isTileDirty = true

			val entity = EntityPool.obtain()
			val pos = entity.addOrGet(ComponentType.Position) as PositionComponent
			val renderable = entity.addOrGet(ComponentType.Renderable) as RenderableComponent

			pos.position = tile

			val p0 = state.source.get()!!.position()!!.position.toVec().add(0.5f, 0.5f)
			val p3 = tile.toVec().add(0.5f, 0.5f)
			val p1 = p0.cpy().lerp(p3, 0.25f).add(0f, 0.2f)
			val p2 = p0.cpy().lerp(p3, 0.75f).add(0f, 0.2f)

			renderable.renderable = CurveRenderable(Bezier(p0, p1, p2, p3), 2f, AssetManager.loadTextureRegion("ray.png")!!, 10)
			renderable.renderable.colour = Colour.RED
			renderable.renderable.animation = BlinkAnimation.obtain()
				.set(Colour(1f, 1f, 1f, 0.5f), Colour(1f, 1f, 1f, 0.8f), 2f, false)
				.setNonBlocking()
			state.world.addEntity(entity)

			markers.add(entity.getRef())
		}

		state.data[key+"entities"] = markers
	}

	override fun exit(state: ActionSequenceState)
	{
		val counter = state.data[key] as Int? ?: 0

		if (counter <= 0)
		{
			removeTileMark(state)

			state.data.remove(key)
		}
	}

	override fun cancel(state: ActionSequenceState)
	{
		removeTileMark(state)
	}

	private fun removeTileMark(state: ActionSequenceState)
	{
		// remove tile mark
		for (point in state.targets)
		{
			val tile = state.world.grid[point] as? Tile ?: continue
			tile.predictedAttacksFrom.remove(state.getRef())
			tile.isTileDirty = true
		}

		val markers = state.data.get(key+"entities") as Array<EntityReference>
		for (ref in markers)
		{
			ref.get()?.markForDeletion(0f)
		}
	}

	override fun preTurn(state: ActionSequenceState)
	{
		var counter = state.data[key] as Int
		counter--
		state.data[key] = counter
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		turns = xmlData.getInt("Turns", 1)
	}
	override val classID: String = "MarkAndWaitForPlayer"
	//endregion
}