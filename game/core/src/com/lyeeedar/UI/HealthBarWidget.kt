package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.floor

class HealthBarWidget(val player: Entity) : Widget()
{
	val hpCol = Color.GREEN
	val lostHpCol = Color.ORANGE
	val emptyCol = Color.BLACK

	val white = AssetManager.loadTextureRegion("Sprites/white.png")!!
	val hp_border = AssetManager.loadTextureRegion("Sprites/GUI/health_border.png")!!

	val pipSize = 10f

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		val stats = player.statistics()!!

		val totalWidth = width// * 0.95f

		val numHpPips = (totalWidth / pipSize).floor()

		val hp = stats.hp
		val maxhp = stats.getStat(Statistic.MAX_HP)

		val solidSpaceRatio = 0.05f
		val space = totalWidth
		val spacePerPip = space / numHpPips
		val spacing = spacePerPip * solidSpaceRatio
		val solid = spacePerPip - spacing

		val prevCol = batch.packedColor
		batch.color = emptyCol
		batch.draw(white, x, y, totalWidth, height)

		batch.color = lostHpCol
		val lostLen = (hp + stats.lostHp) / maxhp
		batch.draw(white, x, y, totalWidth*lostLen, height)

		batch.color = hpCol
		val hpLen = hp / maxhp
		batch.draw(white, x, y, totalWidth*hpLen, height)

		for (i in 0 until numHpPips)
		{
			batch.draw(hp_border, x + i*spacePerPip, y, solid, height)
		}
		batch.packedColor = prevCol
	}
}