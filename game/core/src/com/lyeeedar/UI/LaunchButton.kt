package com.lyeeedar.UI

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.lyeeedar.UI.AbstractAbilityWidget.Companion.selectedAbility
import com.lyeeedar.Util.Statics
import ktx.actors.alpha

class LaunchButton : TextButton("Launch", Statics.skin)
{
	init
	{
		addClickListener {
			selectedAbility?.launch = true
		}
	}

	override fun act(delta: Float)
	{
		super.act(delta)

		if (selectedAbility?.selectedTargets?.notEmpty() == true)
		{
			alpha = 1f
		}
		else
		{
			alpha = 0f
		}
	}
}