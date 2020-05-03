package com.lyeeedar.Game

import com.badlogic.gdx.math.MathUtils.lerp
import com.lyeeedar.Components.Entity
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

	private var lastRenderColData: Int = 0

	fun updateVisibility(delta: Float, isSeen: Boolean, isVisible: Boolean)
	{
		this.isSeen = this.isSeen or isSeen
		seen.targetValue = isSeen
		visible.targetValue = isVisible

		seen.update(delta)
		visible.update(delta)

		skipRender = !seen.currentValue && !seen.targetValue
		skipRenderEntities = !visible.currentValue && !visible.targetValue

		val renderColData = (seen.alpha*255).toInt() + (visible.alpha*255).toInt() + tileCol.hashCode()
		if (renderColData != lastRenderColData)
		{
			lastRenderColData = renderColData

			val prevHash = renderCol.hashCode()
			renderCol.set(tileCol)
			renderCol.mul(seen.alpha)
			renderCol.mul(0.5f + 0.5f * visible.alpha)

			if (renderCol.hashCode() != prevHash)
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
				if (selfEntity.isAllies(obj))
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
			if (obj != null && selfEntity != null && obj != selfEntity)
			{
				if (selfEntity.isAllies(obj))
				{
					return 100
				}
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

	val alpha: Float
		get()
		{
			val current = if (currentValue) 1f else 0f
			val target = if (targetValue) 1f else 0f
			val alpha = min(1f, accumulator / duration)

			return lerp(current, target, alpha)
		}

	fun update(delta: Float)
	{
		if (currentValue != targetValue)
		{
			accumulator += delta

			if (accumulator >= duration)
			{
				currentValue = targetValue
			}
		}
	}
}