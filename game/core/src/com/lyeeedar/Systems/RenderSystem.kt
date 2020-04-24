package com.lyeeedar.Systems

import com.badlogic.gdx.math.Vector2
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour

const val numHpPips = 10
class RenderSystem(world: World) : AbstractRenderSystem(world)
{
	val allyHpCol = Colour.GREEN.copy()
	val enemyHpCol = Colour.RED.copy()
	val lostHpCol = Colour.ORANGE.copy()
	val emptyCol = Colour.BLACK.copy()

	val white = AssetManager.loadTextureRegion("Sprites/white.png")!!
	val hp_border = AssetManager.loadTextureRegion("Sprites/GUI/health_border.png")!!

	override fun drawExtraEntity(entity: Entity, deltaTime: Float)
	{
		val pos = entity.pos()!!
		val renderable = entity.renderable()!!.renderable
		val stats = entity.stats()
		if (stats != null)
		{
			val px = pos.position.x.toFloat() + pos.offset.x
			val py = pos.position.y.toFloat() + pos.offset.y
			var ax = px
			var ay = py

			val offset = renderable.animation?.renderOffset(false)
			if (offset != null)
			{
				ax += offset[0]
				ay += offset[1]
			}

			val hpColour = if (world.player!!.isAllies(entity)) allyHpCol else enemyHpCol

			val totalWidth = pos.data.size.toFloat() * 0.95f

			val hp = stats.hp
			val maxhp = stats.getStat(Statistic.MAXHP)

			val solidSpaceRatio = 0.05f
			val space = totalWidth
			val spacePerPip = space / numHpPips
			val spacing = spacePerPip * solidSpaceRatio
			val solid = spacePerPip - spacing

			var overhead = totalWidth
			if (renderable is Sprite && renderable.drawActualSize)
			{
				val region = renderable.currentTexture
				val height = region.regionHeight
				val ratio = height / 32f
				overhead *= ratio
			}

			renderer.queueTexture(white, ax + totalWidth*0.5f - solid*0.5f, ay+overhead, pos.data.slot.ordinal, 1, colour = emptyCol, width = totalWidth, height = 0.1f, sortX = ax, sortY = ay)

			val lostLen = (hp + stats.lostHp) / maxhp
			renderer.queueTexture(white, ax + totalWidth*lostLen*0.5f - solid*0.5f, ay+overhead, pos.data.slot.ordinal, 2, colour = lostHpCol, width = totalWidth*lostLen, height = 0.1f, sortX = ax, sortY = ay)

			val hpLen = hp / maxhp
			renderer.queueTexture(white, ax + totalWidth*hpLen*0.5f - solid*0.5f, ay+overhead, pos.data.slot.ordinal, 3, colour = hpColour, width = totalWidth*hpLen, height = 0.1f, sortX = ax, sortY = ay)

			for (i in 0 until numHpPips)
			{
				renderer.queueTexture(hp_border, ax+i*spacePerPip, ay+overhead, pos.data.slot.ordinal, 4, width = solid, height = 0.1f, sortX = ax, sortY = ay)
			}
		}
	}

	private val playerPosVec = Vector2()
	override fun getPlayerPosition(): Vector2
	{
		val entity = world.player!!
		val pos = entity.pos()!!
		val renderable = entity.renderable()!!.renderable

		val px = pos.position.x.toFloat() + pos.offset.x
		val py = pos.position.y.toFloat() + pos.offset.y
		var ax = px
		var ay = py

		val offset = renderable.animation?.renderOffset(false)
		if (offset != null)
		{
			ax += offset[0]
			ay += offset[1]
		}

		return playerPosVec.set(ax, ay)
	}
}