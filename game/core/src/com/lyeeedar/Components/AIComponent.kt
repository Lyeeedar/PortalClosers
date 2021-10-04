package com.lyeeedar.Components

import com.lyeeedar.AI.BehaviourTree.BehaviourTree
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.Util.DataFileReference
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.getXml

class AIComponentData : AbstractComponentData()
{
	@DataFileReference(resourceType = "BehaviourTree")
	lateinit var aiPath: String

	var activeOnLoad = false

	//region non-data
	lateinit var ai: BehaviourTree
	//endregion

	override fun afterLoad()
	{
		val xml = getXml(aiPath)

		ai = BehaviourTree()
		ai.load(xml)
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		aiPath = xmlData.get("AiPath")
		afterLoad()
	}
	override val classID: String = "AI"
	//endregion
}

class AIComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.AI

	lateinit var ai: BehaviourTree

	val state = BehaviourTreeState()

	var isActive = false

	fun activate(entity: Entity)
	{
		if (!isActive)
		{
			isActive = true
		}
	}

	override fun reset()
	{
		isActive = false
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as AIComponentData
		ai = data.ai
		isActive = data.activeOnLoad
	}

	override fun onAddedToEntity(entity: Entity)
	{
		entity.addComponent(ComponentType.Task)
	}
}