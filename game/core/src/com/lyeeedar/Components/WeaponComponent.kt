package com.lyeeedar.Components

import com.lyeeedar.Game.Weapon
import com.lyeeedar.Util.DataFileReference
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.getXml

class WeaponComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Weapon

	lateinit var weapon: Weapon
	var equipped = false

	var resources: Int = 0

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data: WeaponComponentData = data as WeaponComponentData
		weapon = Weapon()
		weapon.load(getXml(data.weapon))

		resources = weapon.defaultResources
	}

	override fun reset()
	{
		resources = 0
		equipped = false
	}

	fun buildResource(count: Int)
	{
		resources += count
		if (resources > weapon.maxResources)
		{
			resources = weapon.maxResources
		}
	}

	fun consumeResource(count: Int)
	{
		resources -= count
		if (resources < 0)
		{
			resources = 0
		}
	}
}

class WeaponComponentData : AbstractComponentData()
{
	@DataFileReference(resourceType = "Weapon")
	lateinit var weapon: String

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		weapon = xmlData.get("Weapon")
	}
	override val classID: String = "Weapon"
	//endregion
}