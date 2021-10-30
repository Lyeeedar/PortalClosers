package com.lyeeedar.Game.Portal

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.renderable
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.UI.SkeletonWidget
import com.lyeeedar.Util.Colour

class BossEncounter : AbstractEncounter()
{
	override val title: String
		get() = TODO("Not yet implemented")
	override val description: String
		get() = TODO("Not yet implemented")

	override fun createPreviewTable(): Table
	{
		TODO("Not yet implemented")
	}

	override fun actions(screen: PortalScreen): Sequence<Pair<String, () -> Unit>>
	{
		TODO("Not yet implemented")
	}

	override fun getMapWidget(): Widget
	{
		val entity = EntityLoader.load("Entities/elemental1")
		val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
		skeleton.colour = Colour.RED
		return SkeletonWidget(skeleton, 48f, 48f)
	}
}