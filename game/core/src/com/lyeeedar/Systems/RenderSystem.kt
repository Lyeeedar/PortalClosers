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
	val allyHpCol = Colour.GREEN.copy().lockColour()
	val enemyHpCol = Colour.RED.copy().lockColour()
	val lostHpCol = Colour.ORANGE.copy().lockColour()
	val emptyCol = Colour.BLACK.copy().lockColour()

	val predictedAttackCol = Colour.RED.copy().lockColour()
	val validTargetCol = Colour.LIGHT_GRAY.copy().lockColour()
	val targetCol = Colour.DARK_GRAY.copy().lockColour()
	val selectedTargetCol = Colour.GOLD.copy().lockColour()
	val previewTargetCol = Colour.GREEN.copy().lockColour()
	val additionalCol = Colour.LIGHT_GRAY.copy().a(0.5f).lockColour()

	val hp_border = AssetManager.tryLoadTextureRegion("Sprites/GUI/health_border.png")!!
	val selection = AssetManager.tryLoadTextureRegion("Sprites/darkest/terrain/selection.png")!!
	val num1 = AssetManager.tryLoadTextureRegion("Sprites/darkest/terrain/1.png")!!
	val num2 = AssetManager.tryLoadTextureRegion("Sprites/darkest/terrain/2.png")!!
	val num3 = AssetManager.tryLoadTextureRegion("Sprites/darkest/terrain/3.png")!!
	val target = AssetManager.tryLoadTextureRegion("Sprites/darkest/terrain/target.png")!!

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

			renderer.queueTexture(white, ax - 0.5f + totalWidth*0.5f - solid*0.5f, ay+overhead - 0.5f, index, 1, colour = emptyCol, width = totalWidth, height = 0.1f, sortX = ax, sortY = ay, lit = false)

			val lostLen = (hp + stats.lostHp) / maxhp
			renderer.queueTexture(white, ax - 0.5f + totalWidth*lostLen*0.5f - solid*0.5f, ay+overhead - 0.5f, index, 2, colour = lostHpCol, width = totalWidth*lostLen, height = 0.1f, sortX = ax, sortY = ay, lit = false)

			val hpLen = hp / maxhp
			renderer.queueTexture(white, ax - 0.5f + totalWidth*hpLen*0.5f - solid*0.5f, ay+overhead - 0.5f, index, 3, colour = hpColour, width = totalWidth*hpLen, height = 0.1f, sortX = ax, sortY = ay, lit = false)

			for (i in 0 until numHpPips)
			{
				renderer.queueTexture(hp_border, ax - 0.5f+i*spacePerPip, ay+overhead - 0.5f, index, 4, width = solid, height = 0.1f, sortX = ax, sortY = ay, lit = false)
			}

			for (i in 0 until stats.buffs.size)
			{
				val buff = stats.buffs[i]
				val icon = buff.icon ?: continue

				renderer.queueTexture(icon.currentTexture, ax - 0.5f+i*spacePerPip*3, ay+overhead - 0.5f+0.1f+spacePerPip, index, 4, width = spacePerPip*3, height = spacePerPip*3, sortX = ax, sortY = ay, colour = icon.colour, lit = false)
			}
		}
	}

	override fun drawExtraTile(tile: AbstractTile)
	{
		val slot = SpaceSlot.FLOOR.ordinal

		val tile = tile as Tile
		if (tile.predictedAttacksFrom.size > 0)
		{
			val turns = tile.predictedAttacksFrom.minOfOrNull { it.turns } ?: 1
			val rotation = (tile.x + tile.y).rem(3) * 90f
			renderer.queueTexture(selection, tile.x.toFloat(), tile.y.toFloat(), slot, colour = predictedAttackCol, scaleX = 0.95f, scaleY = 0.95f, rotation = rotation, lit = false)

			val num = when(turns)
			{
				0 -> num1
				1 -> num1
				2 -> num2
				else -> num3
			}
			renderer.queueTexture(num, tile.x.toFloat(), tile.y.toFloat(), slot, 1, colour = predictedAttackCol, scaleX = 0.9f, scaleY = 0.9f, lit = false)
		}
		if (tile.isSelectedTarget)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(selection, tile.x.toFloat(), tile.y.toFloat(), slot, colour = selectedTargetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
			renderer.queueTexture(target, tile.x.toFloat(), tile.y.toFloat(), slot, 1, colour = selectedTargetCol, scaleX = 0.9f, scaleY = 0.9f, lit = false)
		}
		else if (tile.isPreviewedTarget)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(selection, tile.x.toFloat(), tile.y.toFloat(), slot, colour = previewTargetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
		}
		else if (tile.isValidTarget)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(selection, tile.x.toFloat(), tile.y.toFloat(), slot, colour = validTargetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
		}
		else if (tile.isTargetted)
		{
			val rotation = (tile.x + tile.y + 1).rem(3) * 90f
			renderer.queueTexture(selection, tile.x.toFloat(), tile.y.toFloat(), slot, colour = targetCol, scaleX = 0.9f, scaleY = 0.9f, rotation = rotation, lit = false)
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