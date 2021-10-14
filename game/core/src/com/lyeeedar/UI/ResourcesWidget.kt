package com.lyeeedar.UI

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

		for (i in 0 until entity.weapon()!!.resources)
		{
			val x = i * size + (i+1) * pad * 2f + this.x

			batch.draw(resourcesImage, x, pad + this.y, size, size)
		}
	}
}