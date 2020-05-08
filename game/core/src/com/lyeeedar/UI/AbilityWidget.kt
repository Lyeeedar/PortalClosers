package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lyeeedar.Game.Ability
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Statics

class AbilityWidget(val ability: Ability, val world: World<*>) : Table()
{
	val empty = AssetManager.loadSprite("GUI/power_empty")
	val full = AssetManager.loadSprite("GUI/power_full")
	val border = AssetManager.loadTextureRegion("GUI/border")

	val padding = 2

	val widget = SpriteWidget(ability.data.icon!!.copy(), 32f, 32f)

	val usagesLabel = Label("", Statics.skin)

	init
	{
		val stack = Stack()
		stack.add(widget)

		val infoButton = Button(Statics.skin, "info")
		infoButton.setSize(16f, 16f)
		infoButton.addClickListener {

		}
		val infoButtonTable = Table()
		infoButtonTable.add(infoButton).size(16f).expand().top().right().pad(5f)

		stack.add(usagesLabel)

		stack.add(infoButtonTable)

		add(stack).grow()

		updateEnabled()

		touchable = Touchable.enabled
	}

	var colour: Color = Color.DARK_GRAY
	fun updateEnabled()
	{
		if (ability.remainingCooldown <= 0 && ability.getValidTargets(world.player!!, world).isNotEmpty())
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

		val pipSize = (width - (ability.data.cooldown+1) * padding) / ability.data.cooldown

		batch!!.color = Color.WHITE

		if (ability.remainingCooldown > 0)
		{
			for (i in 1..ability.data.cooldown)
			{
				val sprite = if (i <= (ability.data.cooldown-ability.remainingCooldown)) full else empty

				sprite.render(batch as SpriteBatch, x + padding * i + (i - 1) * pipSize, y, pipSize, 10f)
			}
		}
	}
}
