package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.WeaponMove
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Statics

abstract class AbstractAbilityWidget(val world: World<*>) : Widget()
{
	val empty = AssetManager.loadTextureRegion("GUI/power_empty")
	val full = AssetManager.loadTextureRegion("GUI/power_full")
	val background = AssetManager.loadTextureRegion("Icons/Active")
	val border = AssetManager.loadTextureRegion("Icons/BorderGrey")
	val white = AssetManager.loadTextureRegion("white")

	val iconWidth = 32f
	val iconHeight = 32f
	val usageHeight = 8f

	val font = Statics.skin.getFont("small")
	val fontLayout = GlyphLayout()

	abstract val ability: Ability

	fun initialise()
	{
		updateEnabled()

		touchable = Touchable.enabled

		addClickListener {
			if (isAbilityEnabled())
			{
				ability.isSelected = !ability.isSelected
			}
		}
	}

	override fun getPrefWidth(): Float
	{
		return iconWidth
	}

	override fun getPrefHeight(): Float
	{
		return iconHeight// + usageHeight
	}

	private fun isAbilityEnabled(): Boolean = ability.available && ability.cooldown == 0 && ability.remainingUsages != 0 && ability.getValidTargets(world.player!!, world, null).isNotEmpty()

	var colour: Color = Color.DARK_GRAY
	fun updateEnabled()
	{
		if (isAbilityEnabled())
		{
			colour = Color.WHITE
		}
		else
		{
			colour = Color.DARK_GRAY
		}
	}

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		updateEnabled()

		batch.color = colour

		batch.draw(background, x, y, iconWidth, iconHeight)

		if (ability.icon != null)
		{
			batch.draw(ability.icon!!.currentTexture, x, y, iconWidth, iconHeight)
		}

		if (ability.remainingUsages > -1)
		{
			val pipSize = (iconWidth - (ability.data.usages+1) * 1) / ability.data.usages
			for (i in 0 until ability.data.usages)
			{
				val tex = if (i < ability.remainingUsages) full else empty
				//batch.draw(tex, x + (i+1) + i * pipSize, y+iconHeight, pipSize, usageHeight)
			}

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
		else
		{
			batch.color = colour
		}
		batch.draw(border, x, y, iconWidth, iconHeight)
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