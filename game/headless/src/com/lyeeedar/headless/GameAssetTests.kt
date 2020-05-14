package com.lyeeedar.headless

fun addGameAssetTests(funcs: com.badlogic.gdx.utils.Array<Pair<String, ()->Any>>)
{
	funcs.add(Pair("MapGeneration", { MapGenerationTest.runFast() }))
}

fun addGameLongTests(funcs: com.badlogic.gdx.utils.Array<Pair<String, ()->Any>>)
{
	funcs.add(Pair("MapGeneration", { MapGenerationTest.runSlow() }))
}