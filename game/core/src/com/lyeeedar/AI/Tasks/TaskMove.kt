package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.Animation.MoveAnimation
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Random
import squidpony.squidmath.LightRNG

class TaskMove : AbstractTask()
{
	lateinit var direction: Direction

	fun set(direction: Direction): TaskMove
	{
		this.direction = direction

		return this
	}

	override fun execute(e: Entity, world: World<*>, rng: LightRNG)
	{
		val pos = e.position() ?: return
		if (pos.moveLocked) return

		val stats = e.statistics()

		if (stats != null)
		{
			val root = stats.getStat(Statistic.ROOT)
			if (root > 0f && Random.random(rng) < root)
			{
				val stunParticle = AssetManager.loadParticleEffect("Stunned").getParticleEffect()
				stunParticle.addToWorld(world, e.position()!!.position, Vector2(0f, 0.8f))

				stats.addMessage("Rooted!", Colour.YELLOW, 0.4f)

				return
			}
		}

		e.directionalSprite()?.currentAnim = "move"

		if (EventSystem.isEventRegistered(EventType.MOVE, e))
		{
			world.eventSystem()?.addEvent(EventType.MOVE, e, e)
		}

		if (pos.position is AbstractTile)
		{
			val prev = (pos.position as Tile)
			val next = world.grid.tryGet(prev, direction, null) ?: return

			if (pos.isValidTile(next, e))
			{
				pos.doMove(next, e)

				e.renderable()!!.renderable.animation = MoveAnimation.obtain().set(next, prev, 0.15f)
			}
		}
		else
		{
			pos.position.x += direction.x
			pos.position.y += direction.y
		}
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<TaskMove> = object : Pool<TaskMove>() {
			override fun newObject(): TaskMove
			{
				return TaskMove()
			}
		}

		@JvmStatic fun obtain(): TaskMove
		{
			val anim = pool.obtain()

			if (anim.obtained) throw RuntimeException()

			anim.obtained = true
			return anim
		}
	}
	override fun free() { if (obtained) { pool.free(this); obtained = false } }
}