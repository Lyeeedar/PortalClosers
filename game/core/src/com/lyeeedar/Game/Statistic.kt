package com.lyeeedar.Game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lyeeedar.UI.addTapToolTip
import com.lyeeedar.Util.*

// ----------------------------------------------------------------------
enum class Statistic private constructor(val min: Float, val max: Float, val modifiersAreAdded: Boolean)
{
	// Base
	MAX_HP(1f, Float.MAX_VALUE, false),
	MANA_REGEN(1f, Float.MAX_VALUE, false),

	// Offense
	ATK_POWER(1f, Float.MAX_VALUE, false),
	ABILITY_POWER(0f, Float.MAX_VALUE, true),
	CRIT_CHANCE(0f, 1f, true),

	// Defense
	ARMOUR(0f, Float.MAX_VALUE, false),
	DR(0f, 0.8f, true),
	REGENERATION(-1f, 1f, true),
	LIFESTEAL(-Float.MAX_VALUE, Float.MAX_VALUE, true),
	AEGIS(0f, 0.8f, true),

	// Buff
	HASTE(-1f, 1f, true),
	FLEET_FOOT(-1f, 1f, true),
	DERVISH(-1f, 1f, true),

	// Negative
	ROOT(0f, 1f, true),
	FUMBLE(0f, 1f, true),
	DISTRACTION(0f, 1f, true);

	val niceName: String
		get()
		{
			return when(this)
			{
				MAX_HP -> Localisation.getText("statistic.health", "UI")
				MANA_REGEN -> Localisation.getText("statistic.manaregen", "UI")

				ATK_POWER -> Localisation.getText("statistic.attackpower", "UI")
				ABILITY_POWER -> Localisation.getText("statistic.abilitypower", "UI")
				CRIT_CHANCE -> Localisation.getText("statistic.critchance", "UI")

				ARMOUR -> Localisation.getText("statistic.armour", "UI")
				DR -> Localisation.getText("statistic.dr", "UI")
				REGENERATION -> Localisation.getText("statistic.regen", "UI")
				LIFESTEAL -> Localisation.getText("statistic.lifesteal", "UI")
				AEGIS -> Localisation.getText("statistic.aegis", "UI")

				HASTE -> Localisation.getText("statistic.haste", "UI")
				FLEET_FOOT -> Localisation.getText("statistic.fleetfoot", "UI")
				DERVISH -> Localisation.getText("statistic.dervish", "UI")

				ROOT -> Localisation.getText("statistic.root", "UI")
				FUMBLE -> Localisation.getText("statistic.fumble", "UI")
				DISTRACTION -> Localisation.getText("statistic.distraction", "UI")
			}
		}

	val tooltip: String
		get()
		{
			return when(this)
			{
				MAX_HP -> Localisation.getText("statistic.health.description", "UI")
				MANA_REGEN -> Localisation.getText("statistic.manaregen.description", "UI")

				ATK_POWER -> Localisation.getText("statistic.attackpower.description", "UI")
				ABILITY_POWER -> Localisation.getText("statistic.abilitypower.description", "UI")
				CRIT_CHANCE -> Localisation.getText("statistic.critchance.description", "UI")

				ARMOUR -> Localisation.getText("statistic.armour.description", "UI")
				DR -> Localisation.getText("statistic.dr.description", "UI")
				REGENERATION -> Localisation.getText("statistic.regen.description", "UI")
				LIFESTEAL -> Localisation.getText("statistic.lifesteal.description", "UI")
				AEGIS -> Localisation.getText("statistic.aegis.description", "UI")

				HASTE -> Localisation.getText("statistic.haste.description", "UI")
				FLEET_FOOT -> Localisation.getText("statistic.fleetfoot.description", "UI")
				DERVISH -> Localisation.getText("statistic.dervish.description", "UI")

				ROOT -> Localisation.getText("statistic.root.description", "UI")
				FUMBLE -> Localisation.getText("statistic.fumble.description", "UI")
				DISTRACTION -> Localisation.getText("statistic.distraction.description", "UI")
			}
		}

	companion object
	{
		val Values = Statistic.values()
		val BaseValues = arrayOf(MAX_HP, MANA_REGEN, ARMOUR, ATK_POWER)
		val CoreValues = arrayOf(MAX_HP, MANA_REGEN, ARMOUR, ATK_POWER, ABILITY_POWER, CRIT_CHANCE)
		val AttackValues = arrayOf(ATK_POWER, ABILITY_POWER, CRIT_CHANCE)
		val DefenseValues = arrayOf(ARMOUR, DR, REGENERATION, LIFESTEAL, AEGIS)
		val SpeedValues = arrayOf(HASTE, FLEET_FOOT, DERVISH)
		val NegativeValues = arrayOf(ROOT, FUMBLE, DISTRACTION)

		fun parse(xmlData: XmlData, statistics: FastEnumMap<Statistic, Float>)
		{
			for (stat in Values)
			{
				var value = statistics[stat] ?: 0f
				value = xmlData.getFloat(stat.toString(), value)
				statistics[stat] = value
			}
		}

		enum class DisplayType
		{
			FLAT,
			COMPARISON,
			MODIFIER
		}
		fun createTable(stats: FastEnumMap<Statistic, Float>, type: DisplayType, other: FastEnumMap<Statistic, Float>? = null): Table
		{
			val table = Table()

			var bright = true
			for (stat in Values)
			{
				val statVal = stats[stat] ?: 0f
				val otherStatVal = other?.get(stat) ?: 0f

				if (statVal != 0f || otherStatVal != 0f)
				{
					val statTable = Table()

					if (bright)
					{
						statTable.background = TextureRegionDrawable(AssetManager.loadTextureRegion("white")).tint(Color(1f, 1f, 1f, 0.1f))
					}
					bright = !bright

					statTable.add(Label("${stat.niceName}: ", Statics.skin, "card")).expandX().left().pad(5f)
					statTable.addTapToolTip(stat.tooltip)

					when (type)
					{
						DisplayType.FLAT -> {
							statTable.add(Label(statVal.toString(), Statics.skin, "card")).pad(5f)
						}
						DisplayType.MODIFIER -> {
							if (statVal > 0)
							{
								val diff = statVal
								val diffLabel = Label("+$diff", Statics.skin, "cardwhite")
								diffLabel.color = Color(0f, 0.5f, 0f, 1f)
								statTable.add(diffLabel).pad(5f)
							}
							else if (statVal < 0)
							{
								val diff = statVal
								val diffLabel = Label(diff.toString(), Statics.skin, "cardwhite")
								diffLabel.color = Color(0.5f, 0f, 0f, 1f)
								statTable.add(diffLabel).pad(5f)
							}
						}
						DisplayType.COMPARISON -> {
							statTable.add(Label(statVal.toString(), Statics.skin, "card")).pad(5f)
							if (otherStatVal < statVal)
							{
								val diff = statVal - otherStatVal
								val diffLabel = Label("+$diff", Statics.skin, "cardwhite")
								diffLabel.color = Color(0f, 0.5f, 0f, 1f)
								statTable.add(diffLabel).pad(5f)
							}
							else if (statVal < otherStatVal)
							{
								val diff = otherStatVal - statVal
								val diffLabel = Label("-$diff", Statics.skin, "cardwhite")
								diffLabel.color = Color(0.5f, 0f, 0f, 1f)
								statTable.add(diffLabel).pad(5f)
							}
						}
					}

					table.add(statTable).growX()
					table.row()
				}
			}

			return table
		}
	}
}