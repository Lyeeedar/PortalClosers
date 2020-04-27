package com.lyeeedar.Components

import com.lyeeedar.Renderables.ShadowCastCache

class VisionComponent : AbstractComponent()
{
	override val type: ComponentType = ComponentType.Vision

	val visionCache = ShadowCastCache()

	override fun reset()
	{

	}
}