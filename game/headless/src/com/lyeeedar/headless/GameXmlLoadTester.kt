package com.lyeeedar.headless

import com.lyeeedar.Game.Buff
import com.lyeeedar.Util.XmlData
import java.util.*

class GameXmlLoadTester
{
	companion object
	{
		fun testLoad(xml: XmlData, path: String)
		{
			when (xml.name.toUpperCase(Locale.ENGLISH))
			{
				"BUFF" -> Buff.load(xml)
			}
		}
	}
}