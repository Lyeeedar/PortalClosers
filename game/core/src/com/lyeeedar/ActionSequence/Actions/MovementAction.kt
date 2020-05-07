package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.Pool
import com.lyeeedar.AI.Tasks.TaskInterrupt
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Game.Tile
import com.lyeeedar.Pathfinding.BresenhamLine
import com.lyeeedar.Renderables.Animation.ExpandAnimation
import com.lyeeedar.Renderables.Animation.LeapAnimation
import com.lyeeedar.Renderables.Animation.MoveAnimation
import com.lyeeedar.Renderables.Animation.SpinAnimation
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.randomOrNull
import java.util.*
import squidpony.squidmath.LightRNG

enum class MovementType
{
	MOVE,
	LEAP,
	ROLL,
	TELEPORT
}

class MoveSourceAction() : AbstractOneShotActionSequenceAction()
{
	lateinit var type: MovementType

	override fun enter(state: ActionSequenceState)
	{
		val srcEntity = state.source.get() ?: return
		val srcTile = state.world.grid.tryGet(srcEntity.position()!!.position, null) ?: return
		val dst = state.targets.randomOrNull(state.rng) ?: return
		val dstTile = state.world.grid.tryGet(dst, null) ?: return

		doMove(srcTile, dstTile, type, false, state)
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		type = MovementType.valueOf(xmlData.get("Type").toUpperCase(Locale.ENGLISH))
	}
	override val classID: String = "MoveSource"
	//endregion
}

class PullAction() : AbstractOneShotActionSequenceAction()
{
	lateinit var type: MovementType

	override fun enter(state: ActionSequenceState)
	{
		val source = state.source.get() ?: return

		val dstTile = state.world.grid.tryGet(state.sourcePoint, null) ?: return
		for (src in state.targets)
		{
			val srcTile = state.world.grid.tryGet(src, null) ?: continue

			for (slot in SpaceSlot.EntityValues)
			{
				val entity = srcTile.contents[slot]?.get() ?: continue

				if (entity.isEnemies(source))
				{
					doMove(srcTile, dstTile, type, true, state)
				}
			}
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		type = MovementType.valueOf(xmlData.get("Type").toUpperCase(Locale.ENGLISH))
	}
	override val classID: String = "Pull"
	//endregion
}

class KnockbackAction() : AbstractOneShotActionSequenceAction()
{
	lateinit var type: MovementType
	var dist: Int = 1

	override fun enter(state: ActionSequenceState)
	{
		val source = state.source.get() ?: return

		val srcTile = state.world.grid.tryGet(state.sourcePoint, null) ?: return
		for (target in state.targets)
		{
			val targetTile = state.world.grid.tryGet(target, null) ?: continue

			for (slot in SpaceSlot.EntityValues)
			{
				val entity = targetTile.contents[slot]?.get() ?: continue

				if (entity.isEnemies(source))
				{
					val dir = Direction.getDirection(srcTile, targetTile)
					val dstPoint = Point.obtain().set(dir.x, dir.y)
					dstPoint.timesAssign(dist)
					dstPoint.plusAssign(targetTile)
					val dst = state.world.grid.getClamped(dstPoint)

					doMove(targetTile, dst, type, true, state)

					dstPoint.free()
				}
			}
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		type = MovementType.valueOf(xmlData.get("Type").toUpperCase(Locale.ENGLISH))
		dist = xmlData.getInt("Dist", 1)
	}
	override val classID: String = "Knockback"
	//endregion
}

private fun doMove(src: AbstractTile, dst: AbstractTile, type: MovementType, interrupt: Boolean, state: ActionSequenceState): AbstractTile
{
	val entity = src.contents[SpaceSlot.ENTITY]?.get() ?: return dst
	val pos = entity.position() ?: return dst
	if (!pos.moveable) return dst
	val stats = entity.statistics() ?: return dst
	if (stats.invulnerable || stats.blocking || state.rng.nextFloat() < stats.getStat(Statistic.AEGIS))
	{
		stats.blockedDamage = true
		return dst
	}

	if (stats.hp <= 0 || entity.isMarkedForDeletion())
	{
		return dst
	}

	if (interrupt)
	{
		val task = entity.task()
		if (task != null)
		{
			if (task.tasks.size == 0 || task.tasks[0] !is TaskInterrupt)
			{
				task.tasks.clear()
				task.tasks.add(TaskInterrupt())
			}
		}
	}

	val moveSlot = when (type)
	{
		MovementType.MOVE, MovementType.ROLL -> SpaceSlot.ENTITY
		MovementType.TELEPORT, MovementType.LEAP -> SpaceSlot.LIGHT
	}
	val path = BresenhamLine.lineNoDiag(src.x, src.y, dst.x, dst.y, state.world.grid, true, moveSlot, entity)

	var actualDst: AbstractTile? = null
	outer@for (point in path.reversed())
	{
		val tile = state.world.grid[point]
		if (pos.isValidTile(tile, entity))
		{
			actualDst = tile
			break
		}

		for (dir in Direction.CardinalValues)
		{
			val tile = state.world.grid.tryGet(point, dir, null) ?: continue
			if (pos.isValidTile(tile, entity))
			{
				actualDst = tile
				break@outer
			}
		}
	}

	if (actualDst == null) return src
	if (actualDst == src) return actualDst

	pos.doMove(actualDst, entity)

	val animSpeed = 0.1f + src.euclideanDist(actualDst) * 0.015f

	val renderable = entity.renderable()?.renderable
	if (type == MovementType.MOVE)
	{
		renderable?.animation = MoveAnimation.obtain().set(actualDst, src, animSpeed)
	}
	else if (type == MovementType.ROLL)
	{
		renderable?.animation = MoveAnimation.obtain().set(actualDst, src, animSpeed)

		val direction = Direction.getDirection(src, actualDst)
		if (direction == Direction.EAST)
		{
			renderable?.animation = SpinAnimation.obtain().set(animSpeed, -360f)
		}
		else
		{
			renderable?.animation = SpinAnimation.obtain().set(animSpeed, 360f)
		}

		renderable?.animation = ExpandAnimation.obtain().set(animSpeed * 0.9f, 1f, 0.8f, false)
	}
	else if (type == MovementType.LEAP)
	{
		renderable?.animation = LeapAnimation.obtain().setRelative(animSpeed, src, actualDst, 3f)

		if (renderable is Sprite)
		{
			renderable.removeAmount = 0f
		}
	}

	return actualDst
}