package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.HandlerAction
import com.lyeeedar.Util.Statics

class AbilityWidget(val ability: Ability, val world: World<*>, val background: SpriteWidget) : Table()
{
	val empty = AssetManager.loadSprite("GUI/power_empty")
	val full = AssetManager.loadSprite("GUI/power_full")
	val border = AssetManager.loadTextureRegion("GUI/border")

	val padding = 2

	val widget = SpriteWidget(ability.data.icon!!.copy(), 32f, 32f)

	init
	{
		val stack = Stack()

		val widgetTable = Table()
		widgetTable.add(widget).pad(10f)
		stack.add(widgetTable)

		val infoButton = Button(Statics.skin, "info")
		infoButton.setSize(16f, 16f)
		infoButton.addTapToolTip("${ability.name}\n${ability.description}")
		val infoButtonTable = Table()
		infoButtonTable.add(infoButton).size(16f).expand().top().right().pad(5f)

		stack.add(infoButtonTable)

		add(stack).grow()

		updateEnabled()

		touchable = Touchable.enabled

		world.onTurnEvent += {
			updateEnabled()
			HandlerAction.KeepAttached
		}

		widget.addClickListener {
			if (isAbilityEnabled())
			{
				ability.isSelected = !ability.isSelected
			}
		}
	}

	private fun isAbilityEnabled(): Boolean = ability.remainingCooldown <= 0 && ability.getValidTargets(world.player!!, world).isNotEmpty()

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

	override fun draw(batch: Batch?, parentAlpha: Float)
	{
		if (ability.isSelected)
		{
			widget.color = Color.GOLD

			batch?.color = Color.GOLD
			batch?.draw(border, x, y, width, height)
		}
		else
		{
			widget.color = colour
		}

		super.draw(batch, parentAlpha)

		if (ability.remainingCooldown > 0)
		{
			batch!!.color = Color.WHITE
		}
		else
		{
			batch!!.color = Color.DARK_GRAY
		}

		val bounds = background.getBounds()
		val pipSize = (bounds.width - (ability.data.cooldown+1) * padding) / ability.data.cooldown
		for (i in 1..ability.data.cooldown)
		{
			val sprite = if (i <= (ability.data.cooldown-ability.remainingCooldown)) full else empty

			sprite.render(batch as SpriteBatch, bounds.x + padding * i + (i - 1) * pipSize, bounds.y, pipSize, 7f)
		}
	}
}
