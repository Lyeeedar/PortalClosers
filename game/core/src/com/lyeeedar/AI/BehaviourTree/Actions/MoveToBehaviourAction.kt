package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskMove
import com.lyeeedar.Components.isOnTile
import com.lyeeedar.Components.position
import com.lyeeedar.Components.task
import com.lyeeedar.Direction
import com.lyeeedar.Pathfinding.Pathfinder
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.XmlData

class MoveToBehaviourAction : AbstractBehaviourAction()
{
	val lastPosKey = "lastPos"

	var dst: Int = 0
	var towards: Boolean = true
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val target = state.getData<Point>(key, 0)
		val posData = state.entity.position()
		val position = posData?.position
		val taskData = state.entity.task()

		// doesnt have all the needed data, fail
		if ( target == null || posData == null || position == null || taskData == null )
		{
			return EvaluationState.FAILED
		}

		if (!wasEvaluatedLastTime(state))
		{
			state.removeData(lastPosKey, dataGuid)
		}

		// if we arrived at our target, succeed
		if (
			(towards && (position.taxiDist(target) <= dst || posData.isOnTile(target)))
			||
			(!towards && position.taxiDist(target) >= dst) )
		{
			state.removeData(lastPosKey, dataGuid)
			return EvaluationState.COMPLETED
		}

		val pathFinder = Pathfinder(state.world.grid, position.x, position.y, target.x, target.y, posData.data.size, state.entity)
		val path = pathFinder.getPath( posData.data.slot )

		if (path == null)
		{
			state.removeData(lastPosKey, dataGuid)
			return EvaluationState.FAILED
		}

		// if couldnt find a valid path, fail
		if ( path.size < 2 )
		{
			state.removeData(lastPosKey, dataGuid)
			Point.freeAll(path)
			return EvaluationState.FAILED
		}

		val nextTile = state.world.grid.tryGet( path.get( 1 ), null )

		// possible loop, quit just in case
		val lastPos = state.getData(lastPosKey, dataGuid, Point.MINUS_ONE)
		if (nextTile == lastPos)
		{
			state.removeData(lastPosKey, dataGuid)
			Point.freeAll(path)
			return EvaluationState.FAILED
		}

		// if next step is impassable then fail
		if (nextTile?.getPassable(posData.data.slot, state.entity) != true)
		{
			state.removeData(lastPosKey, dataGuid)
			Point.freeAll(path)
			return EvaluationState.FAILED
		}

		val offset = path.get( 1 ) - path.get( 0 )

		// if moving towards path to the object
		if ( towards )
		{
			if ( path.size - 1 <= dst || offset == Point.ZERO )
			{
				state.removeData(lastPosKey, dataGuid)
				Point.freeAll(path)
				offset.free()
				return EvaluationState.COMPLETED
			}

			state.setData(lastPosKey, dataGuid, position)
			taskData.tasks.add(TaskMove.obtain().set(Direction.getDirection(offset)))
		}
		// if moving away then just run directly away
		else
		{
			if ( path.size - 1 >= dst || offset == Point.ZERO )
			{
				state.removeData(lastPosKey, dataGuid)
				Point.freeAll(path)
				offset.free()
				return EvaluationState.COMPLETED
			}

			state.setData(lastPosKey, dataGuid, position)
			val opposite = offset * -1
			taskData.tasks.add(TaskMove.obtain().set(Direction.getDirection(opposite)))
			opposite.free()
		}

		Point.freeAll(path)
		offset.free()
		return EvaluationState.RUNNING
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		dst = xmlData.getInt("Dst", 0)
		towards = xmlData.getBoolean("Towards", true)
		key = xmlData.get("Key")
		afterLoad()
	}
	override val classID: String = "MoveTo"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}