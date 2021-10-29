package com.lyeeedar.Game.Portal

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Components.DescriptionComponent
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.description
import com.lyeeedar.Components.renderable
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.Screens.ScreenEnum
import com.lyeeedar.Screens.WorldScreen
import com.lyeeedar.UI.Seperator
import com.lyeeedar.UI.SkeletonWidget
import com.lyeeedar.UI.addTapToolTip
import com.lyeeedar.Util.Localisation
import com.lyeeedar.Util.Statics
import ktx.scene2d.scene2d
import ktx.scene2d.table

class CombatEncounter(val biome: Biome, val pack: Pack) : AbstractEncounter()
{
	override val title: String
		get() = "Battle"
	override val description: String
		get() = "A pack of enemies fills the area, you are going to need to slay them to progress."

	override fun createPreviewTable(): Table
	{
		val uniqueCreatures = pack.creatures.distinctBy { it.entity }

		val table = scene2d.table {
			add(Seperator(Statics.skin, false)).growX()
			row()

			table {
				for (creature in uniqueCreatures)
				{
					val entity = EntityLoader.load(creature.entity)
					val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
					val widget = SkeletonWidget(skeleton, 48f, 48f)

					widget.addTapToolTip {
						val desc = entity.description()

						if (desc != null)
						{
							val name = Localisation.getText(desc.nameID, "Entity")
							val description = Localisation.getText(desc.descriptionID, "Entity")

							name + "\n" + description
						} else
						{
							"???"
						}
					}

					add(widget).pad(5f)
				}
			}

			row()
			add(Seperator(Statics.skin, false)).growX()
		}

		return table
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
		val entity = EntityLoader.load(pack.creatures[0].entity)
		val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
		return SkeletonWidget(skeleton, 48f, 48f)
	}
}