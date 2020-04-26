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

class AIComponent(data: AIComponentData) : AbstractComponent<AIComponentData>(data)
{
	override val type: ComponentType = ComponentType.AI

	val state = BehaviourTreeState()

	override fun reset()
	{

	}

}