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

abstract class AbstractAbilityPreviewWidget() : Widget()
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

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		if (ability.remainingUsages == 0 || !ability.available)
		{
			batch.color = Color.DARK_GRAY
		}
		else
		{
			batch.color = Color.LIGHT_GRAY
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

		batch.draw(border, x, y, iconWidth, iconHeight)
	}

	companion object
	{
		var selectedAbility: Ability? = null
	}
}

class AbilityPreviewWidget(override val ability: Ability) : AbstractAbilityPreviewWidget()
{
	init
	{
		initialise()
	}
}

class MovePreviewWidget(val move: WeaponMove) : AbstractAbilityPreviewWidget()
{
	override val ability: Ability
		get() = move.getAsAbility()

	init
	{
		initialise()
	}
}