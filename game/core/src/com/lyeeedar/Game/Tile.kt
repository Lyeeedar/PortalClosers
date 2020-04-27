package com.lyeeedar.Game

import com.badlogic.gdx.math.MathUtils.lerp
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.isAllies
import com.lyeeedar.Components.position
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.min

class Tile(x: Int, y: Int) : AbstractTile(x, y)
{
	private val seen = LerpedValue(0.3f)
	private val visible = LerpedValue(0.3f)

	fun updateVisibility(delta: Float, isSeen: Boolean, isVisible: Boolean)
	{
		seen.targetValue = isSeen
		visible.targetValue = isVisible

		seen.update(delta)
		visible.update(delta)

		skipRender = !seen.currentValue && !seen.targetValue
		skipRenderEntities = !visible.currentValue && !visible.targetValue

		val prevHash = renderCol.hashCode()
		renderCol.set(Colour.WHITE)
		renderCol.mul(seen.alpha)
		renderCol.mul(0.5f + 0.5f * visible.alpha)

		if (renderCol.hashCode() != prevHash)
		{
			isTileDirty = true
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

		val obj = contents.get(travelType)
		if (obj != null && obj != self)
		{
			if (self is Entity)
			{
				if (self.isAllies(obj) && obj.position()!!.turnsOnTile < 3)
				{
					return true
				}
			}

			return false
		}

		return true
	}

	override fun getInfluence(travelType: SpaceSlot, self: Any?): Int
	{
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