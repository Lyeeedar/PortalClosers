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
				val stunParticle = AssetManager.loadParticleEffect("StatusAndEffects/Stunned").getParticleEffect()
				stunParticle.addToWorld(world, e.position()!!.position, Vector2(0f, 0.8f))

				stats.addMessage("Rooted!", Colour.YELLOW, 0.4f)

				return
			}
		}

		e.directionalSprite()?.currentAnim = "move"

		if (EventSystem.isEventRegistered(EventType.MOVE, e))
		{
			val ref = e.getRef()
			world.eventSystem()?.addEvent(EventType.MOVE, ref, ref)
		}

		val prev = world.grid.tryGet(pos.position, null) ?: return
		val next = world.grid.tryGet(prev, direction, null) ?: return

		if (pos.isValidTile(next, e))
		{
			pos.doMove(next, e)
			e.renderable()?.renderable?.animation = MoveAnimation.obtain().set(next, prev, 0.2f)
		}
		else if (pos.canSwap)
		{
			val contents = next.contents[pos.slot]?.get()
			if (contents != null && e.isAllies(contents))
			{
				val opos = contents.position()!!
				if (opos.moveable && !opos.moveLocked)
				{
					val task = contents.task()
					if (task != null)
					{
						task.tasks.clear()
						task.tasks.add(TaskWait.obtain())
					}

					opos.removeFromTile(contents)

					pos.doMove(next, e)
					e.renderable()?.renderable?.animation = MoveAnimation.obtain().set(next, prev, 0.2f)

					opos.doMove(prev, contents)
					contents.renderable()?.renderable?.animation = MoveAnimation.obtain().set(prev, next, 0.2f)
				}
			}
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