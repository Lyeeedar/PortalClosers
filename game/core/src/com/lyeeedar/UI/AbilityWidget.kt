package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.lyeeedar.Components.position
import com.lyeeedar.Components.tile
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Tile
import com.lyeeedar.Game.WeaponMove
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Statics

abstract class AbstractAbilityWidget(val world: World<*>) : Widget()
{
	val background = AssetManager.tryLoadTextureRegion("Icons/Active")
	val border = AssetManager.tryLoadTextureRegion("Icons/BorderGrey")
	val white = AssetManager.tryLoadTextureRegion("white")

	val iconWidth = 32f
	val iconHeight = 32f

	val font = Statics.skin.getFont("small")
	val fontLayout = GlyphLayout()

	abstract val ability: Ability

	fun initialise()
	{
		touchable = Touchable.enabled
		this.addListener(object : ActorGestureListener(20f, 0.4f, longPressDuration, Int.MAX_VALUE.toFloat()) {
			var longPressed = false
			override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
			{
				super.touchDown(event, x, y, pointer, button)
				longPressed = false
			}

			override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
			{
				super.touchUp(event, x, y, pointer, button)

				if (!longPressed && ability.isUsable())
				{
					if (ability.isSelected)
					{
						ability.isSelected = false
						ability.launch = false
						ability.selectedTargets.clear()
						if (selectedAbility == ability)
						{
							selectedAbility = null
						}
					}
					else
					{
						ability.isSelected = true
						ability.launch = false
						ability.selectedTargets.clear()

						if (ability.data.targetType == AbilityData.TargetType.SELF)
						{
							ability.selectedTargets.add(world.player!!.position()!!.tile)
						}
						else
						{
							val valid = ability.getValidTargets(world.player!!, world, null)
							if (valid.size == 1)
							{
								ability.selectedTargets.add(valid[0] as Tile)
							}
						}

						selectedAbility?.isSelected = false
						selectedAbility = ability
					}
				}
			}

			override fun longPress(actor: Actor?, x: Float, y: Float): Boolean
			{
				super.longPress(actor, x, y)
				actor?.showTooltip("${ability.name}\n${ability.description}", x, y)
				longPressed = true
				return true
			}
		})
	}

	override fun getPrefWidth(): Float
	{
		return iconWidth
	}

	override fun getPrefHeight(): Float
	{
		return iconHeight// + usageHeight
	}

	private fun hasValidTargets(): Boolean = ability.getValidTargets(world.player!!, world, null).isNotEmpty()

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		val hasTargets = hasValidTargets()

		if (ability.remainingUsages == 0 || !ability.available)
		{
			batch.color = Color.DARK_GRAY
		}
		else
		{
			batch.color = Color.WHITE
		}

		batch.draw(background, x, y, iconWidth, iconHeight)

		if (ability.icon != null)
		{
			batch.draw(ability.icon!!.currentTexture, x, y, iconWidth, iconHeight)
		}

		if (ability.remainingUsages > -1)
		{
			fontLayout.setText(font, "${ability.remainingUsages}/${ability.data.usages}")
			font.draw(batch, fontLayout, x+iconWidth - fontLayout.width - 2f, y+2f + fontLayout.height)
		}

		if (ability.cooldown > 0)
		{
			val alpha = ability.cooldown.toFloat() / ability.data.cooldown.toFloat()

			batch.setColor(0f, 0f, 0f, 0.5f)
			batch.draw(white, x, y, iconWidth, iconHeight * alpha)

			fontLayout.setText(font, "${ability.cooldown}")
			font.draw(batch, fontLayout, x+iconWidth/2f - fontLayout.width/2f, y+iconHeight/2f + fontLayout.height/2f)
		}

		if (ability.isSelected)
		{
			batch.color = Color.GOLD
		}
		batch.draw(border, x, y, iconWidth, iconHeight)

		if (!hasTargets)
		{
			batch.color = Color.MAROON
			batch.draw(white, x, y, iconWidth, 3f)
		}
	}

	companion object
	{
		var selectedAbility: Ability? = null
	}
}

class AbilityWidget(override val ability: Ability, world: World<*>) : AbstractAbilityWidget(world)
{
	init
	{
		initialise()
	}
}

class MoveWidget(val move: WeaponMove, world: World<*>) : AbstractAbilityWidget(world)
{
	override val ability: Ability
		get() = move.getAsAbility()

	init
	{
		initialise()
	}
}