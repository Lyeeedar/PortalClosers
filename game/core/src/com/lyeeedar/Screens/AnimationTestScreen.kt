package com.lyeeedar.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.renderable
import com.lyeeedar.Renderables.Renderer.SortedRenderer
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour


class AnimationTestScreen : AbstractScreen()
{
	lateinit var renderer: SortedRenderer

	lateinit var entity: Entity
	var floor = AssetManager.loadSprite("white")
	var player = AssetManager.loadSprite("player")
	val selection = AssetManager.tryLoadTextureRegion("Sprites/darkest/terrain/selection.png")!!

	override fun create()
	{
		renderer = SortedRenderer(48f, 1f, 2f, SpaceSlot.values().size, true)

		entity = EntityLoader.load("Entities/player")
	}

	override fun doRender(delta: Float)
	{
		renderer.begin(delta, stage.width * 0.5f, stage.height * 0.5f, Colour.WHITE)
		renderer.queue(entity.renderable()!!.renderable, 0f, 0f, SpaceSlot.ENTITY.ordinal, index = 2)
		//renderer.queue(floor, 0f, 0f, SpaceSlot.FLOOR.ordinal, index = 1)
		//renderer.queue(player, 0f, 0f, SpaceSlot.ENTITY.ordinal, index = 3)
		renderer.queueTexture(selection, 0f, 0f, SpaceSlot.FLOOR.ordinal, 1, scaleX = 0.9f, scaleY = 0.9f, lit = false)
		renderer.end(stage.batch)
	}
}