package com.lyeeedar.Game.Portal

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.renderable
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.Screens.ScreenEnum
import com.lyeeedar.Screens.WorldScreen
import com.lyeeedar.UI.SkeletonWidget
import com.lyeeedar.Util.Statics

class CombatEncounter : AbstractEncounter()
{
	override val title: String
		get() = "Battle"
	override val description: String
		get() = "A pack of enemies fills the area, you are going to need to slay them to progress."

	override fun createPreviewTable(): Table
	{
		return Table()
	}

	override fun actions(screen: PortalScreen): Sequence<Pair<String, () -> Unit>>
	{
		return sequence {
			yield(Pair("Fight") {
				val world = Statics.game.getTypedScreen<WorldScreen>()!!
				world.completionCallback = {
					Statics.game.switchScreen(ScreenEnum.PORTAL)
					screen.portal.completeEncounter(this@CombatEncounter)
					screen.update()
				}
				Statics.game.switchScreen(ScreenEnum.WORLD)
				world.createWorld(screen.portal.player, this@CombatEncounter)
			})
		}
	}

	override fun getMapWidget(): Widget
	{
		val entity = EntityLoader.load("Entities/elemental1")
		val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
		return SkeletonWidget(skeleton, 48f, 48f)
	}
}