package com.lyeeedar.Components

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Renderables.ShadowCastCache
import com.lyeeedar.Util.Point

class VisionComponent : AbstractComponent()
{
	override val type: ComponentType = ComponentType.Vision

	private val visionCache = ShadowCastCache()
	fun getVision(x: Int, y: Int): Array<Point>
	{
		return visionCache.getShadowCast(x, y, 6)
	}

	override fun reset()
	{

	}
}