package com.lyeeedar.UI

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.WeaponComponent
import com.lyeeedar.Components.weapon
import com.lyeeedar.Game.Weapon
import com.lyeeedar.Util.AssetManager

class ResourcesWidget(val entity: Entity) : Table()
{
	val resourcesImage = AssetManager.loadTextureRegion("Sprites/Oryx/uf_split/uf_items/crystal_blood.png")

	override fun draw(batch: Batch?, parentAlpha: Float)
	{
		super.draw(batch!!, parentAlpha)

		val pad = 2f
		val size = height

		val weapon = entity.weapon()!!
		for (i in 0 until weapon.weapon.maxResources)
		{
			val x = i * size + (i+1) * pad * 2f + this.x

			if (i < weapon.resources)
			{
				batch.color = Color.WHITE
				batch.draw(resourcesImage, x, pad + this.y, size, size)
			}
			else
			{
				batch.color = Color.DARK_GRAY
				batch.draw(resourcesImage, x, pad + this.y, size, size)
			}
		}
	}
}