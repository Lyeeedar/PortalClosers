package com.lyeeedar.UI

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Buff
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.*
import ktx.collections.set

class EntityWidget(var entity: EntityReference, val world: World<*>) : Widget()
{
	val background = AssetManager.loadTextureRegion("Icons/Empty")!!
	val border = AssetManager.loadTextureRegion("GUI/RewardChanceBorder")!!
	val white = AssetManager.loadTextureRegion("white")!!
	val hp_border = AssetManager.loadTextureRegion("Sprites/GUI/health_border.png")!!

	val whiteColour = Colour.WHITE
	val hpColour = Colour.GREEN
	val healCol = Colour.GREEN
	val lostHpCol = Colour.ORANGE
	val emptyCol = Colour.BLACK
	val greyOut = Colour(0f, 0f, 0f, 0.8f)
	val lightGray = Colour.LIGHT_GRAY
	val darkGray = Colour.DARK_GRAY
	val gold = Colour.GOLD

	class BuffCounter(val buff: Buff, var count: Int)
	private val buffCounterMap = ObjectMap<String, BuffCounter>()

	val font = Statics.skin.getFont("small")

	var lastRenderable: Sprite? = null

	val tempAbilityArray = Array<Ability>()
	override fun draw(batch: Batch, parentAlpha: Float)
	{
		if (entity.get() == null)
		{
			batch.setColor(darkGray)
		}
		else
		{
			batch.setColor(whiteColour)
		}

		batch.draw(background, x, y, width, height)

		batch.setColor(whiteColour)
		var renderable = entity.get()?.renderable()?.renderable as? Sprite
		if (renderable == null)
		{
			renderable = lastRenderable
		}

		if (renderable != null)
		{
			lastRenderable = renderable
			batch.draw(renderable.textures[0], x, y, width, height)
		}

		val stats = entity.get()?.statistics()
		if (stats != null)
		{
			val totalWidth = width-10f

			val hp = max(0f, stats.hp)
			val maxhp = stats.getStat(Statistic.MAX_HP)

			val solidSpaceRatio = 0.05f
			val space = totalWidth
			val spacePerPip = space / 10
			val spacing = spacePerPip * solidSpaceRatio
			val solid = spacePerPip - spacing

			batch.setColor(emptyCol)
			batch.draw(white, x+5f, y+5f, totalWidth, 5f)

			val lostLen = (hp + stats.lostHp) / maxhp
			batch.setColor(lostHpCol)
			batch.draw(white, x+5f, y+5f, totalWidth*lostLen, 5f)

			val hpLen = hp / maxhp
			batch.setColor(hpColour)
			batch.draw(white, x+5f, y+5f, totalWidth*hpLen, 5f)

			batch.setColor(whiteColour)
			for (i in 0 until 10)
			{
				batch.draw(hp_border, x+5f+i*spacePerPip, y+5f, solid, 5f)
			}

			if (stats.hp > 0)
			{
				buffCounterMap.clear()
				for (i in 0 until stats.buffs.size)
				{
					val buff = stats.buffs[i]

					val icon = buff.icon ?: continue

					var existing = buffCounterMap[buff.name]
					if (existing == null)
					{
						existing = BuffCounter(buff, 0)
						buffCounterMap[buff.name] = existing
					}
					existing.count++
				}

				var i = 0
				for (buffCounter in buffCounterMap.values())
				{
					batch.setColor(buffCounter.buff.icon!!.colour)
					batch.draw(buffCounter.buff.icon!!.currentTexture, x + 5f + i * solid * 3, y + 10f, solid * 3, solid * 3)
					batch.setColor(Colour.WHITE)
					font.draw(batch, buffCounter.count.toString(), x + 5f + i * solid * 3 + solid * 2, y + 20f)
					i++
				}
			}

			val ability = entity?.get()?.ability()
			if (ability != null)
			{
				val abx = x+width-17f
				val aby = y+height-17f

				tempAbilityArray.clear()

				for (ab in ability.abilities)
				{
					tempAbilityArray.add(ab)
				}

				val spacing = height / tempAbilityArray.size
				val size = min(spacing-2f, 10f)

				for (i in 0 until tempAbilityArray.size)
				{
					val ab = tempAbilityArray[i]

					batch.setColor(whiteColour)
					batch.draw(background, abx, aby-i*spacing, size, size)

					val abilityAvailable = ab.cooldown == 0 && ab.remainingUsages != 0

					if (abilityAvailable)
					{
						val alpha = 1f - (ab.cooldown / ab.data.cooldown.toFloat())
						batch.setColor(greyOut)
						batch.draw(white, abx, aby-i*spacing, size, size * alpha)
					}

					if (abilityAvailable || ab.getValidTargets(entity.entity, world, null).isEmpty())
					{
						batch.setColor(lightGray)
					}
					else
					{
						batch.setColor(gold)
					}

					batch.draw(border, abx, aby-i*spacing, size, size)
				}
			}

			batch.draw(border, x, y, width, height)

			if (hp == 0f)
			{
				batch.setColor(greyOut)
				batch.draw(white, x, y, width, height)
			}
		}
	}
}