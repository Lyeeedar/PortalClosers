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
	val full = AssetManager.loadTextureRegion("Sprites/GUI/attack_full.png")!!
	val empty = AssetManager.loadTextureRegion("Sprites/GUI/attack_empty.png")!!

	val lostHpCol = Color.ORANGE

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		val stats = player.statistics()!!

		val hp = stats.hp
		val maxhp = stats.getStat(Statistic.MAX_HP)
		val alpha = hp / maxhp
		val lostAlpha = (hp + stats.lostHp) / maxhp

		val prevCol = batch.packedColor
		batch.color = Color.WHITE
		batch.draw(empty, x, y, width, height)

		batch.color = lostHpCol
		batch.draw(full.texture, x, y, width, height * lostAlpha, full.u, full.v, full.u2, full.v + (full.v2 - full.v) * lostAlpha)

		batch.color = Color.WHITE
		batch.draw(full.texture, x, y, width, height * alpha, full.u, full.v, full.u2, full.v + (full.v2 - full.v) * alpha)

		batch.packedColor = prevCol
	}
}