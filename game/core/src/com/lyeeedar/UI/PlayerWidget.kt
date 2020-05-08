package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.lyeeedar.Components.ability
import com.lyeeedar.Game.Ability
import com.lyeeedar.Systems.World
import com.lyeeedar.Systems.eventSystem
import com.lyeeedar.Util.AssetManager

class PlayerWidget(val world: World<*>) : Table()
{
	val emptySlot = AssetManager.loadSprite("Icons/Empty")

	init
	{
		update()
	}

	fun update()
	{
		clear()

		val player = world.player!!

		val hp = HealthBarWidget(player)
		add(hp).growX().height(10f).pad(3f)
		row()

		val ability = player.ability()!!

		val abilitiesTable = Table()
		addAbility(ability.ability1, abilitiesTable)
		addAbility(ability.ability2, abilitiesTable)
		addAbility(ability.ability3, abilitiesTable)
		addAbility(ability.ability4, abilitiesTable)
		addAbility(ability.ultimate, abilitiesTable)

		add(abilitiesTable).growX()
	}

	private fun addAbility(ability: Ability?, table: Table)
	{
		val stack = Stack()
		val background = SpriteWidget(emptySlot, 32f, 32f)

		background.color = Color.DARK_GRAY

		stack.add(background)

		if (ability != null)
		{
			val widget = AbilityWidget(ability, world, background)
			stack.add(widget)
		}

		table.add(stack).grow().width(Value.percentWidth(0.2f, this))
	}
}