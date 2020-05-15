package com.lyeeedar.headless

fun addGameAssetTests(funcs: com.badlogic.gdx.utils.Array<Pair<String, ()->Any>>)
{
	funcs.add(Pair("MapGeneration", { MapGenerationTest.runFast() }))
	funcs.add(Pair("Ability", { AbilityTest.runFast() }))
}

fun addGameLongTests(funcs: com.badlogic.gdx.utils.Array<Pair<String, ()->Any>>)
{
	funcs.add(Pair("MapGeneration", { MapGenerationTest.runSlow() }))
	funcs.add(Pair("Ability", { AbilityTest.runSlow() }))
}