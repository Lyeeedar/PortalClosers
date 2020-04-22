package com.lyeeedar.Util


class XmlDataClassLoader
{
	companion object
	{
		fun loadAbstractActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractActionSequenceAction
		{
			return when (classID)
			{
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"Repeat" -> com.lyeeedar.ActionSequence.Actions.RepeatAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"SelectSelf" -> com.lyeeedar.ActionSequence.Actions.SelectSelfAction()
				"SelectEntities" -> com.lyeeedar.ActionSequence.Actions.SelectEntitiesAction()
				"BlockTurn" -> com.lyeeedar.ActionSequence.Actions.BlockTurnAction()
				"RestoreTargets" -> com.lyeeedar.ActionSequence.Actions.RestoreTargetsAction()
				"LockEntityTargets" -> com.lyeeedar.ActionSequence.Actions.LockEntityTargetsAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractActionSequenceAction!")
			}
		}
		fun loadAbstractOneShotActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractOneShotActionSequenceAction
		{
			return when (classID)
			{
				"Permute" -> com.lyeeedar.ActionSequence.Actions.PermuteAction()
				"SelectEntities" -> com.lyeeedar.ActionSequence.Actions.SelectEntitiesAction()
				"SelectSelf" -> com.lyeeedar.ActionSequence.Actions.SelectSelfAction()
				"SelectTiles" -> com.lyeeedar.ActionSequence.Actions.SelectTilesAction()
				"StoreTargets" -> com.lyeeedar.ActionSequence.Actions.StoreTargetsAction()
				"BlockTurn" -> com.lyeeedar.ActionSequence.Actions.BlockTurnAction()
				"RestoreTargets" -> com.lyeeedar.ActionSequence.Actions.RestoreTargetsAction()
				"LockEntityTargets" -> com.lyeeedar.ActionSequence.Actions.LockEntityTargetsAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractOneShotActionSequenceAction!")
			}
		}
		fun loadAbstractDurationActionSequenceAction(classID: String): com.lyeeedar.ActionSequence.Actions.AbstractDurationActionSequenceAction
		{
			return when (classID)
			{
				"Repeat" -> com.lyeeedar.ActionSequence.Actions.RepeatAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractDurationActionSequenceAction!")
			}
		}
		fun loadAbstractComponentData(classID: String): com.lyeeedar.Components.AbstractComponentData
		{
			return when (classID)
			{
				"AdditionalRenderable" -> com.lyeeedar.Components.AdditionalRenderableComponentData()
				"DirectionalSprite" -> com.lyeeedar.Components.DirectionalSpriteComponentData()
				"Renderable" -> com.lyeeedar.Components.RenderableComponentData()
				"Statistics" -> com.lyeeedar.Components.StatisticsComponentData()
				"Name" -> com.lyeeedar.Components.NameComponentData()
				"Dialogue" -> com.lyeeedar.Components.DialogueComponentData()
				"Empty" -> com.lyeeedar.Components.EmptyComponentData()
				"Position" -> com.lyeeedar.Components.PositionComponentData()
				"MetaRegion" -> com.lyeeedar.Components.MetaRegionComponentData()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractComponentData!")
			}
		}
		fun loadAbstractMapGenerationAction(classID: String): com.lyeeedar.MapGeneration.Nodes.AbstractMapGenerationAction
		{
			return when (classID)
			{
				"ChambersGenerator" -> com.lyeeedar.MapGeneration.Nodes.ChambersGeneratorAction()
				"Condition" -> com.lyeeedar.MapGeneration.Nodes.ConditionAction()
				"DefineVariable" -> com.lyeeedar.MapGeneration.Nodes.DefineVariableAction()
				"Fill" -> com.lyeeedar.MapGeneration.Nodes.FillAction()
				"Filter" -> com.lyeeedar.MapGeneration.Nodes.FilterAction()
				"FindRooms" -> com.lyeeedar.MapGeneration.Nodes.FindRoomsAction()
				"Flip" -> com.lyeeedar.MapGeneration.Nodes.FlipAction()
				"PerPoint" -> com.lyeeedar.MapGeneration.Nodes.PerPointAction()
				"Repeat" -> com.lyeeedar.MapGeneration.Nodes.RepeatAction()
				"Scale" -> com.lyeeedar.MapGeneration.Nodes.ScaleAction()
				"SelectNamedArea" -> com.lyeeedar.MapGeneration.Nodes.SelectNamedAreaAction()
				"Split" -> com.lyeeedar.MapGeneration.Nodes.SplitAction()
				"SquidlibDungeonGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibDungeonGeneratorAction()
				"SquidlibLanesMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibLanesMapGeneratorAction()
				"SquidlibOrganicMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibOrganicMapGeneratorAction()
				"SquidlibSerpentMapGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSerpentMapGeneratorAction()
				"SquidlibSymmetryGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSymmetryGeneratorAction()
				"Symbol" -> com.lyeeedar.MapGeneration.Nodes.SymbolAction()
				"Translate" -> com.lyeeedar.MapGeneration.Nodes.TranslateAction()
				"SquidlibDenseRoomGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibDenseRoomGeneratorAction()
				"Node" -> com.lyeeedar.MapGeneration.Nodes.NodeAction()
				"Divide" -> com.lyeeedar.MapGeneration.Nodes.DivideAction()
				"ConnectRooms" -> com.lyeeedar.MapGeneration.Nodes.ConnectRoomsAction()
				"Defer" -> com.lyeeedar.MapGeneration.Nodes.DeferAction()
				"SquidlibFlowingCaveGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibFlowingCaveGeneratorAction()
				"Take" -> com.lyeeedar.MapGeneration.Nodes.TakeAction()
				"Datascope" -> com.lyeeedar.MapGeneration.Nodes.DatascopeAction()
				"Rotate" -> com.lyeeedar.MapGeneration.Nodes.RotateAction()
				"SetNamedArea" -> com.lyeeedar.MapGeneration.Nodes.SetNamedAreaAction()
				"SquidlibSectionGenerator" -> com.lyeeedar.MapGeneration.Nodes.SquidlibSectionGeneratorAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractMapGenerationAction!")
			}
		}

	}
}
