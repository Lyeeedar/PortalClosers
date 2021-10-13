package com.lyeeedar.Screens

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lyeeedar.UI.ParticleEffectActor
import com.lyeeedar.UI.TextureDrawable
import com.lyeeedar.Util.AssetManager

class CityScreen : AbstractScreen()
{
	override fun create()
	{
		val texture = AssetManager.loadTexture("city.png")!!
		mainTable.background = TextureDrawable(texture)

		val portal = AssetManager.loadTexture("portal.png")!!
		val actor = Table()
		actor.background = TextureDrawable(portal)
		actor.setSize(150f, 150f)
		actor.setPosition(200f, 200f)
		stage.addActor(actor)
	}

	override fun doRender(delta: Float)
	{

	}
}