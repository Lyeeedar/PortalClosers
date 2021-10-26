package com.lyeeedar.Game.Portal

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.UI.SkeletonWidget
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData

class SigilEncounter : AbstractEncounter()
{
	override val title: String
		get() = "Sigil Shrine"
	override val description: String
		get() = "Your sigil resonates with the energy radiating from this shrine. Absorb it to recharge your sigil usages, or take its power to enhance your body."

	override fun createPreviewTable(): Table
	{
		return Table()
	}

	override fun actions(screen: PortalScreen): Sequence<Pair<String, () -> Unit>>
	{
		return sequence {
			yield(Pair("Recharge Sigil") {
				screen.portal.completeEncounter(this@SigilEncounter)
				screen.update()
			})
			yield(Pair("Absorb Energy") {
				screen.portal.completeEncounter(this@SigilEncounter)
				screen.update()
			})
		}
	}

	override fun getMapWidget(): Widget
	{
		val renderable = AssetManager.loadSkeleton(XmlData.getXml("Sprites/Skeletons/sigil_shrine/sigil_shrine.xml"))
		return SkeletonWidget(renderable, 48f, 48f)
	}
}