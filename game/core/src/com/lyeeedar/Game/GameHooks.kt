package com.lyeeedar.Game

import com.lyeeedar.ScreenEnum
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.Settings
import com.lyeeedar.Util.Statics

fun save() {

}

fun newGame() {
	Statics.settings = Settings()

	Statics.game.switchScreen(ScreenEnum.WORLD)

	Statics.analytics.tutorialBegin()
}

fun load(): Boolean {
	return false
}

fun World<Tile>.addSystems()
{
	// update
	systems.add(TaskSystem(this))
	systems.add(ActionSequenceSystem(this))
	systems.add(StatisticsSystem(this))
	systems.add(TileSystem(this))
	systems.add(WaterSystem(this))
	systems.add(BloodSystem(this))
	systems.add(HateSystem(this))
	systems.add(EventSystem(this))

	// render
	systems.add(DialogueSystem(this))
	systems.add(DirectionalSpriteSystem(this))
	systems.add(RenderSystem(this))

	// cleanup
	systems.add(DeletionSystem(this))
}