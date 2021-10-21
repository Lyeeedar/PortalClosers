package com.lyeeedar.Systems

import com.badlogic.gdx.math.Vector2
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Statics.Companion.spriteTargetResolution

fun World<*>.renderSystem() = systems.filterIsInstance<RenderSystem>().firstOrNull()
const val numHpPips = 10
open class RenderSystem(world: World<*>) : AbstractRenderSystem(world)
{
	val allyHpCol = Colour.GREEN.copy()
	val enemyHpCol = Colour.RED.copy()
	val lostHpCol = Colour.ORANGE.copy()
	val emptyCol = Colour.BLACK.copy()

	val predictedAttackCol = Colour.RED.copy()
	val validTargetCol = Colour.LIGHT_GRAY.copy()
	val targetCol = Colour.DARK_GRAY.copy()
	val selectedTargetCol = Colour.GOLD.copy()
	val previewTargetCol = Colour.GREEN.copy()

	val white = AssetManager.tryLoadTextureRegion("Sprites/white.png")!!
	val hp_border = AssetManager.tryLoadTextureRegion("Sprites/GUI/health_border.png")!!
	val attack = AssetManager.tryLoadTextureRegion("Sprites/Oryx/Custom/terrain/selection.png")!!

	val checkerCol = Colour(0f, 0f, 0f, 0.04f)

	override fun drawExtraEntity(entity: Entity, deltaTime: Float)
	{
		val renderer = renderer

		val pos = entity.position()!!
		val renderable = entity.renderable()!!.renderable
		val stats = entity.statistics()
		if (stats != null)
		{
			val px = pos.position.xFloat + pos.offset.x
			val py = pos.position.yFloat + pos.offset.y
			var ax = px
			var ay = py

			val offset = renderable.animation?.renderOffset(false)
			if (offset != null)
			{
				ax += offset[0]
				ay += offset[1]
			}

			val hpColour = if (world.player!!.isAllies(entity)) allyHpCol else enemyHpCol

			val totalWidth = pos.size.toFloat() * 0.95f

			val hp = stats.hp
			val maxhp = stats.getStat(Statistic.MAX_HP)

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
				val ratio = height / spriteTargetResolution
				overhead *= ratio
			}
			else if (renderable is SkeletonRenderable)
			{
				val ratio = 1.5f
				overhead *= ratio
			}

			val index = SpaceSlot.ABOVEENTITY.ordinal

			renderer.queueTexture(white, ax + totalWidth*0.5f - solid*0.5f, ay+overhead, index, 1, colour = emptyCol, width = totalWidth, height = 0.1f, sortX = ax, sortY = ay, lit = false)

			val lostLen = (hp + stats.lostHp) / maxhp
			renderer.queueTexture(white, ax + totalWidth*lostLen*0.5f - solid*0.5f, ay+overhead, index, 2, colour = lostHpCol, width = totalWidth*lostLen, height = 0.1f, sortX = ax, sortY = ay, lit = false)

			val hpLen = hp / maxhp
			renderer.queueTexture(white, ax + totalWidth*hpLen*0.5f - solid*0.5f, ay+overhead, index, 3, colour = hpColour, width = totalWidth*hpLen, height = 0.1f, sortX = ax, sortY = ay, lit = false)

			for (i in 0 until numHpPips)
			{
				renderer.queueTexture(hp_border, ax+i*spacePerPip, ay+overhead, index, 4, width = solid, height = 0.1f, sortX = ax, sortY = ay, lit = false)
			}

			for (i in 0 until stats.buffs.size)
			{
				val buff = stats.buffs[i]
				val icon = buff.icon ?: continue

				renderer.queueTexture(icon.currentTexture, ax+i*spacePerPip*3, ay+overhead+0.1f+spacePerPip, index, 4, width = spacePerPip*3, height = spacePerPip*3, sortX = ax, sortY = ay, colour = icon.colour, lit = false)
			}
		}
	}

	override fun drawExtraTile(tile: AbstractTile)
	{
		if (tile.wall == null)
		{
			if ((tile.x + tile.y).rem(2) == 0)
			{
				renderer.queueTexture(white, tile.x.toFloat() + 0.5f, tile.y.toFloat() + 0.5f, SpaceSlot.FLOOR.ordinal, index = 1, colour = checkerCol)
			}
		}

		val tile = tile as Tile
		if (tile.predictedAttacksFrom.size > 0)
		{
			val rotation = (tile.x + tile.y).rem(3) * 90f
			renderer.queueTexture(attack, tile.x.toFloat() + 0.5f, tile.y.toFloat() + 0.5f, SpaceSlot.BELOWENTITY.ordinal, colour = predictedAttackCol, scaleX = 0.95f, scaleY = 0.95f, rotation = rotation, lit = false)
		}
		if (tile.isSelectedTarget)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(attack, tile.x.toFloat() + 0.5f, tile.y.toFloat() + 0.5f, SpaceSlot.BELOWENTITY.ordinal, colour = selectedTargetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
		}
		else if (tile.isPreviewedTarget)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(attack, tile.x.toFloat() + 0.5f, tile.y.toFloat() + 0.5f, SpaceSlot.BELOWENTITY.ordinal, colour = previewTargetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
		}
		else if (tile.isValidTarget)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(attack, tile.x.toFloat() + 0.5f, tile.y.toFloat() + 0.5f, SpaceSlot.BELOWENTITY.ordinal, colour = validTargetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
		}
		else if (tile.isTargetted)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(attack, tile.x.toFloat() + 0.5f, tile.y.toFloat() + 0.5f, SpaceSlot.BELOWENTITY.ordinal, colour = targetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
		}
	}

	private val playerPosVec = Vector2()
	override fun getPlayerPosition(deltaTime: Float?): Vector2
	{
		val entity = world.player!!
		val pos = entity.position()!!
		val renderable = entity.renderable()!!.renderable
		if (deltaTime != null) renderer.update(renderable, deltaTime)

		val px = pos.position.xFloat + pos.offset.x
		val py = pos.position.yFloat + pos.offset.y
		var ax = px
		var ay = py

		val offset = renderable.animation?.renderOffset(true)
		if (offset != null)
		{
			ax += offset[0]
			ay += offset[1]
		}

		return playerPosVec.set(ax, ay)
	}
}