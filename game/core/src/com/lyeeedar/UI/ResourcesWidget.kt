package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.equipment
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Localisation

class ResourcesWidget(val entity: Entity) : Table()
{
	init
	{
		background = NinePatchDrawable(NinePatch(AssetManager.tryLoadTextureRegion("Icons/Active"), 4, 4, 4, 4))

		addHoldToolTip {
			"The ${Localisation.getText(entity.equipment()!!.weapon.resourcesName, "Weapon")} you have available to use your ${Localisation.getText(entity.equipment()!!.weapon.name, "Weapon")} abilities."
		}
	}

	override fun draw(batch: Batch?, parentAlpha: Float)
	{
		super.draw(batch!!, parentAlpha)

		val pad = 3f
		val size = height - pad * 2f

		val weapon = entity.equipment()!!
		for (i in 0 until weapon.weapon.maxResources)
		{
			val x = i * size + (i+1) * pad * 2f + this.x

			if (i < weapon.resources)
			{
				batch.color = Color.WHITE
			}
			else
			{
				batch.color = Color.DARK_GRAY
			}
			batch.draw(weapon.weapon.resourcesIcon.currentTexture, x, y + pad, size, size)
		}
	}
}