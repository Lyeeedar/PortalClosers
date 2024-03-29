package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.EntityReference
import com.lyeeedar.Components.isAllies
import com.lyeeedar.Components.isEnemies
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Buff
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.DataXml
import com.lyeeedar.Util.XmlData
import ktx.collections.set

@DataClass(name = "AddBuff")
class BuffAction : AbstractDurationActionSequenceAction()
{
	@DataXml(actualClass = "Buff")
	lateinit var buff: XmlData

	var isBuff: Boolean = true
	var removeOnExit: Boolean = true

	override fun enter(state: ActionSequenceState)
	{
		val createdBuffs = Array<Pair<EntityReference, Buff>>()
		for (target in state.targets)
		{
			val tile = state.world.grid.tryGet(target, null) ?: continue
			for (slot in SpaceSlot.Values)
			{
				val entityRef = tile.contents[slot] ?: continue
				val entity = entityRef.get() ?: continue

				var add = false
				if (isBuff)
				{
					if (entity.isAllies(state.source.get()!!))
					{
						add = true
					}
				}
				else
				{
					if (entity.isEnemies(state.source.get()!!))
					{
						add = true
					}
				}

				if (add)
				{
					val buff = Buff()
					buff.load(this.buff)
					buff.source = state.source

					entity.statistics()!!.buffs.add(buff)

					if (removeOnExit)
					{
						createdBuffs.add(Pair(entityRef, buff))
					}
				}
			}
		}

		if (removeOnExit)
		{
			val key = "buffs${state.uid}"
			state.data[key] = createdBuffs
		}
	}

	override fun exit(state: ActionSequenceState)
	{
		if (removeOnExit)
		{
			val key = "buffs${state.uid}"
			val buffs = state.data[key] as Array<Pair<EntityReference, Buff>>

			for (buff in buffs)
			{
				val entity = buff.first.get() ?: continue
				entity.statistics()!!.buffs.removeValue(buff.second, true)
			}
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		buff = xmlData.getChildByName("Buff")!!
		isBuff = xmlData.getBoolean("IsBuff", true)
		removeOnExit = xmlData.getBoolean("RemoveOnExit", true)
	}
	override val classID: String = "Buff"
	//endregion
}