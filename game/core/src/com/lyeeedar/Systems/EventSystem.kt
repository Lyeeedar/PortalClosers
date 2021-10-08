package com.lyeeedar.Systems

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.*
import com.lyeeedar.Util.FastEnumMap
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.set

enum class EventType
{
	// Actions
	MOVE,
	USE_ABILITY,

	// Self events
	DEAL_DAMAGE,
	TAKE_DAMAGE,
	HEALED,
	CRIT,
	BLOCK,
	KILL,
	AGRO_CHANGED,

	// General events
	DEATH,
	ON_TURN;
}

fun World<*>.eventSystem() = systems.filterIsInstance<EventSystem>().firstOrNull()
class EventSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.EventHandler).get())
{
	private val queuedEvents = Array<EventData>()
	private val executingArray = Array<EventData>()

	override fun updateEntity(entity: Entity, deltaTime: Float)
	{

	}

	override fun onTurnEntity(entity: Entity)
	{
		if (isEventRegistered(EventType.ON_TURN, entity))
		{
			addEvent(EventType.ON_TURN, entity.getRef(), entity.getRef())
		}
	}

	override fun beforeOnTurn()
	{
		executingArray.clear()
		executingArray.addAll(queuedEvents)
		queuedEvents.clear()

		for (i in 0 until executingArray.size)
		{
			val event = executingArray[i]

			val source = event.source.get()
			if (source == null || source.isMarkedForDeletion())
			{
				event.free()
				continue
			}

			val stats = source.statistics()
			if (stats != null)
			{
				for (i in 0 until stats.buffs.size)
				{
					val buff = stats.buffs[i]
					checkHandlers(event, buff.eventHandlers, buff.source)
				}
			}

			val eventHandler = source.eventHandler()
			if (eventHandler != null)
			{
				checkHandlers(event, eventHandler.handlers)
			}

			event.free()
		}
	}

	private fun checkHandlers(eventData: EventData, eventHandler: FastEnumMap<EventType, Array<EventAndCondition>>, overrideSource: EntityReference? = null)
	{
		val handlers = eventHandler[eventData.type]

		if (handlers != null)
		{
			for (handler in handlers)
			{
				if (handler.condition.evaluate(eventData.variables, world.rng) != 0f)
				{
					val entity = spawnedEventEntityArchetype.build()

					val comp = entity.actionSequence()!!
					comp.actionSequence = handler.sequence

					val source = if (overrideSource?.get() != null) overrideSource else eventData.source
					comp.actionSequenceState.set(source, comp.actionSequence, world, world.rng.nextLong())
					comp.actionSequenceState.targets.clear()
					comp.actionSequenceState.targets.addAll(eventData.targets)

					if (eventData.targetEntity != null)
					{
						comp.actionSequenceState.lockedEntityTargets.add(eventData.targetEntity!!)
					}

					world.addEntity(entity)
				}
			}
		}
	}

	fun addEvent(type: EventType, source: EntityReference, target: EntityReference, inputVariables: Map<String, Float>? = null)
	{
		queuedEvents.add(EventData.obtain().set(type, source, target, inputVariables))
	}

	companion object
	{
		val spawnedEventEntityArchetype = EntityArchetypeBuilder()
			.add(ComponentType.ActionSequence)
			.add(ComponentType.Transient)

		fun isEventRegistered(type: EventType, entity: Entity): Boolean
		{
			val stats = entity.statistics()
			if (stats != null)
			{
				for (i in 0 until stats.buffs.size)
				{
					val buff = stats.buffs[i]
					if ((buff.eventHandlers[type]?.size ?: 0) > 0)
					{
						return true
					}
				}
			}

			val eventHandler = entity.eventHandler()
			if (eventHandler != null)
			{
				if ((eventHandler.handlers[type]?.size ?: 0) > 0)
				{
					return true
				}
			}

			return false
		}
	}
}

class EventData()
{
	lateinit var type: EventType
	lateinit var source: EntityReference
	val targets = Array<Point>(1)
	val variables = ObjectFloatMap<String>()

	var targetEntity: EntityReference? = null

	fun set(type: EventType, source: EntityReference, target: EntityReference, inputVariables: Map<String, Float>? = null): EventData
	{
		this.type = type
		this.source = source
		this.targets.add(target.entity.position()!!.position)
		targetEntity = target

		if (inputVariables != null)
		{
			for (pair in inputVariables)
			{
				variables[pair.key] = pair.value
			}
		}

		source.entity.statistics()?.write(variables, "self")
		target.entity.statistics()?.write(variables, "target")

		return this
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<EventData> = object : Pool<EventData>() {
			override fun newObject(): EventData
			{
				return EventData()
			}
		}

		@JvmStatic fun obtain(): EventData
		{
			val obj = pool.obtain()

			if (obj.obtained) throw RuntimeException()

			obj.obtained = true
			return obj
		}
	}
	fun free() { if (obtained) { pool.free(this); obtained = false } }
}