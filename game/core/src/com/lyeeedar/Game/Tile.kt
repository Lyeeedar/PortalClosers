package com.lyeeedar.Game

import com.badlogic.gdx.math.MathUtils.lerp
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.ActionSequence.ActionSequenceReference
import com.lyeeedar.Components.EntityReference
import com.lyeeedar.Components.isAllies
import com.lyeeedar.Components.position
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.min

class Tile(x: Int, y: Int) : AbstractTile(x, y)
{
	public var isSeen = false
	private val seen = LerpedValue(0.3f)
	private val visible = LerpedValue(0.3f)

	var isTargetted = false

	val predictedAttacksFrom = ObjectSet<ActionSequenceReference>()

	private var lastRenderColData: Int = 0

	private val cachedRenderCol: Colour = Colour.WHITE.copy()

	fun updateVisibility(delta: Float, isSeen: Boolean, isVisible: Boolean)
	{
		this.isSeen = this.isSeen or isSeen
		seen.targetValue = isSeen
		visible.targetValue = isVisible

		seen.update(delta)
		visible.update(delta)

		skipRender = !seen.currentValue && !seen.targetValue
		skipRenderEntities = !visible.currentValue && !visible.targetValue

		updateColour()
	}

	override fun getRenderCol(): Colour
	{
		updateColour()
		return cachedRenderCol
	}

	fun updateColour()
	{
		val seen = seen.alpha
		val visible = visible.alpha
		val renderColData = (seen*255).toInt() + (visible*255).toInt() + tileCol.hashCode()
		if (renderColData != lastRenderColData)
		{
			lastRenderColData = renderColData

			val prevHash = cachedRenderCol.hashCode()
			cachedRenderCol.set(bakedLighting)
			cachedRenderCol.mul(tileCol)
			cachedRenderCol.mul(seen)
			cachedRenderCol.mul(0.3f + 0.7f * visible)

			if (cachedRenderCol.hashCode() != prevHash)
			{
				isTileDirty = true
			}
		}
	}

	override fun getPassable(travelType: SpaceSlot, self: Any?): Boolean
	{
		if (wall != null) return false
		if (contents.get(SpaceSlot.WALL) != null) { return false; }

		if (travelType == SpaceSlot.LIGHT)
		{
			return true
		}

		if (self is EntityReference)
		{
			val selfEntity = self.get()
			val obj = contents.get(travelType)?.get()
			if (obj != null && selfEntity != null && obj != selfEntity)
			{
				if (
					obj.position()!!.moveable && !obj.position()!!.moveLocked &&
					selfEntity.isAllies(obj) &&
					(selfEntity.position()!!.canSwap || obj.position()!!.turnsOnTile < 3)
				)
				{
					return true
				}

				return false
			}
		}

		return true
	}

	override fun getInfluence(travelType: SpaceSlot, self: Any?): Int
	{
		if (self is EntityReference)
		{
			val selfEntity = self.get()
			val obj = contents.get(travelType)?.get()
			if (obj != null && selfEntity != null && obj != selfEntity && !selfEntity.position()!!.canSwap)
			{
				return 100
			}
		}

		return 0
	}
}

class LerpedValue(val duration: Float)
{
	var currentValue: Boolean = false
	var targetValue: Boolean = false
		set(value)
		{
			if (value != field)
			{
				accumulator = 0f
				field = value
			}
		}

	var accumulator: Float = 0f

	var alpha: Float = 0f

	fun update(delta: Float)
	{
		if (currentValue != targetValue || (alpha != 0f && alpha != 1f))
		{
			accumulator += delta

			if (accumulator >= duration)
			{
				currentValue = targetValue
				alpha = if (currentValue) 1f else 0f
			}
			else
			{
				val current = if (currentValue) 1f else 0f
				val target = if (targetValue) 1f else 0f
				val alphaRaw = min(1f, accumulator / duration)

				alpha = lerp(current, target, alphaRaw)
			}
		}
	}
}