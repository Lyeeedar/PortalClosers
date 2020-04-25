package com.lyeeedar.Components

import com.lyeeedar.Renderables.ShadowCastCache

inline fun Entity.vision(): VisionComponent? = this.components[ComponentType.Vision] as VisionComponent?
class VisionComponent : NonDataComponent()
{
	override val type: ComponentType = ComponentType.Vision

	val visionCache = ShadowCastCache()

	override fun reset()
	{

	}
}