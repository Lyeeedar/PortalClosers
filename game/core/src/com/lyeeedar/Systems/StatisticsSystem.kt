package com.lyeeedar.Systems

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Array
import com.lyeeedar.AI.Tasks.TaskInterrupt
import com.lyeeedar.Components.*
import com.lyeeedar.Game.DamageType
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Renderables.Animation.BlinkAnimation
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.UI.lambda
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.Statics
import ktx.actors.then

class StatisticsSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.Statistics).get())
{
	val lostHpClearSpeed = 5f

	val blockEffect = AssetManager.loadParticleEffect("Block")
	val blockBrokenEffect = AssetManager.loadParticleEffect("BlockBroken")
	val hitEffect = AssetManager.loadParticleEffect("Hit")

	val messageList = Array<Label>()

	override fun updateEntity(entity: Entity, deltaTime: Float)
	{
		val stats = entity.statistics()!!

		if (stats.hp <= 0f)
		{
			entity.markForDeletion(0f, "died")
		}

		if (stats.lostHp > 0)
		{
			stats.lostHp -= deltaTime * lostHpClearSpeed * (stats.maxLostHp / 2f)
			if (stats.lostHp < 0f)
			{
				stats.lostHp = 0f
				stats.maxLostHp = 0f
			}
		}

		if (stats.tookDamage)
		{
			val sprite = entity.renderable()?.renderable as? Sprite

			if (sprite != null)
			{
				sprite.colourAnimation = BlinkAnimation.obtain().set(Colour(1f, 0.5f, 0.5f, 1f), sprite.colour, 0.15f, true)
			}

			val pos = entity.position()!!
			val p = hitEffect.getParticleEffect()
			p.size[0] = pos.size
			p.size[1] = pos.size

			p.addToWorld(world, pos.position)

			stats.tookDamage = false
		}

		if (stats.blockBroken)
		{
			stats.blockBroken = false

			val pos = entity.position()!!
			val p = blockBrokenEffect.getParticleEffect()
			p.size[0] = pos.size
			p.size[1] = pos.size

			p.addToWorld(world, pos.position)

			entity.task()?.tasks?.add(TaskInterrupt())
		}
		else if (stats.blockedDamage)
		{
			stats.blockedDamage = false

			val pos = entity.position()!!
			val p = blockEffect.getParticleEffect()
			p.size[0] = pos.size
			p.size[1] = pos.size

			p.addToWorld(world, pos.position)
		}

		if (RenderSystemWidget.instance != null)
		{
			for (message in stats.messagesToShow)
			{
				val offset = Vector2(world.tileSize / 2f + Random.random(Random.sharedRandom, -5, 5).toFloat(), world.tileSize*2.2f + Random.random(Random.sharedRandom, -5, 5).toFloat())

				val label = Label(message.text, Statics.skin, "popup")
				label.color = message.colour.color()
				label.setFontScale(message.size)
				label.rotation = -60f
				label.setPosition(offset.x - label.prefWidth / 2f, offset.y)

				val sequence =
					Actions.alpha(0f) then
						Actions.fadeIn(0.1f) then
						Actions.parallel(
							Actions.moveBy(2f, 3f, 1f),
							Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.5f))) then
						lambda { messageList.removeValue(label, true) } then
						Actions.removeActor()

				label.addAction(sequence)

				messageList.add(label)
				Statics.stage.addActor(label)
				RenderSystemWidget.instance!!.addAttachedToEntityWidget(entity.getRef(), label)
			}
		}

		for (message in stats.messagesToShow)
		{
			message.free()
		}
		stats.messagesToShow.clear()
	}

	override fun onTurnEntity(entity: Entity)
	{
		val stats = entity.statistics()!!

		val maxHP = stats.getStat(Statistic.MAX_HP)
		val manaRegen = stats.getStat(Statistic.MANA_REGEN)
		val regen = stats.getStat(Statistic.REGENERATION)

		entity.ability()?.gainMana(manaRegen.toInt())

		if (regen > 0f)
		{
			stats.regenerate(maxHP * regen)
		}
		else if (regen < 0f)
		{
			stats.damage(maxHP * regen)
		}

		val itr = stats.buffs.iterator()
		while (itr.hasNext())
		{
			val buff = itr.next()

			if (buff.duration > 0)
			{
				buff.duration--
				if (buff.duration == 0)
				{
					itr.remove()
				}
			}
		}
	}

}