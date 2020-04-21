package com.lyeeedar.Util

import com.lyeeedar.MapGeneration.Nodes.ChambersGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.DefineVariableAction
import com.lyeeedar.Components.AdditionalRenderableComponentData
import com.lyeeedar.MapGeneration.Nodes.ConditionAction
import com.lyeeedar.MapGeneration.Nodes.SquidlibSymmetryGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.ScaleAction
import com.lyeeedar.MapGeneration.Nodes.SquidlibLanesMapGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.NodeAction
import com.lyeeedar.MapGeneration.Nodes.FilterAction
import com.lyeeedar.Components.RenderableComponentData
import com.lyeeedar.Components.DirectionalSpriteComponentData
import com.lyeeedar.MapGeneration.Nodes.DivideAction
import com.lyeeedar.MapGeneration.Nodes.SplitAction
import com.lyeeedar.MapGeneration.Nodes.FillAction
import com.lyeeedar.MapGeneration.Nodes.SquidlibSerpentMapGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.PerPointAction
import com.lyeeedar.MapGeneration.Nodes.FindRoomsAction
import com.lyeeedar.MapGeneration.Nodes.SquidlibFlowingCaveGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.SymbolAction
import com.lyeeedar.MapGeneration.Nodes.TakeAction
import com.lyeeedar.Components.PositionComponentData
import com.lyeeedar.MapGeneration.Nodes.TranslateAction
import com.lyeeedar.MapGeneration.Nodes.ConnectRoomsAction
import com.lyeeedar.MapGeneration.Nodes.RepeatAction
import com.lyeeedar.Components.MetaRegionComponentData
import com.lyeeedar.MapGeneration.Nodes.SelectNamedAreaAction
import com.lyeeedar.MapGeneration.Nodes.SquidlibOrganicMapGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.DatascopeAction
import com.lyeeedar.Components.NameComponentData
import com.lyeeedar.MapGeneration.Nodes.RotateAction
import com.lyeeedar.MapGeneration.Nodes.FlipAction
import com.lyeeedar.Components.AbstractComponentData
import com.lyeeedar.Components.DialogueComponentData
import com.lyeeedar.MapGeneration.Nodes.SetNamedAreaAction
import com.lyeeedar.MapGeneration.Nodes.SquidlibSectionGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.AbstractMapGenerationAction
import com.lyeeedar.Components.EmptyComponentData
import com.lyeeedar.MapGeneration.Nodes.SquidlibDenseRoomGeneratorAction
import com.lyeeedar.MapGeneration.Nodes.DeferAction
import com.lyeeedar.MapGeneration.Nodes.SquidlibDungeonGeneratorAction

class XmlDataClassLoader
{
	companion object
	{
		fun loadAbstractComponentData(classID: String): AbstractComponentData
		{
			return when (classID)
			{
				"AdditionalRenderable" -> AdditionalRenderableComponentData()
				"DirectionalSprite" -> DirectionalSpriteComponentData()
				"Renderable" -> RenderableComponentData()
				"Name" -> NameComponentData()
				"Dialogue" -> DialogueComponentData()
				"Empty" -> EmptyComponentData()
				"Position" -> PositionComponentData()
				"MetaRegion" -> MetaRegionComponentData()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractComponentData!")
			}
		}
		fun loadAbstractMapGenerationAction(classID: String): AbstractMapGenerationAction
		{
			return when (classID)
			{
				"ChambersGenerator" -> ChambersGeneratorAction()
				"Condition" -> ConditionAction()
				"DefineVariable" -> DefineVariableAction()
				"Fill" -> FillAction()
				"Filter" -> FilterAction()
				"FindRooms" -> FindRoomsAction()
				"Flip" -> FlipAction()
				"PerPoint" -> PerPointAction()
				"Repeat" -> RepeatAction()
				"Scale" -> ScaleAction()
				"SelectNamedArea" -> SelectNamedAreaAction()
				"Split" -> SplitAction()
				"SquidlibDungeonGenerator" -> SquidlibDungeonGeneratorAction()
				"SquidlibLanesMapGenerator" -> SquidlibLanesMapGeneratorAction()
				"SquidlibOrganicMapGenerator" -> SquidlibOrganicMapGeneratorAction()
				"SquidlibSerpentMapGenerator" -> SquidlibSerpentMapGeneratorAction()
				"SquidlibSymmetryGenerator" -> SquidlibSymmetryGeneratorAction()
				"Symbol" -> SymbolAction()
				"Translate" -> TranslateAction()
				"SquidlibDenseRoomGenerator" -> SquidlibDenseRoomGeneratorAction()
				"Node" -> NodeAction()
				"Divide" -> DivideAction()
				"ConnectRooms" -> ConnectRoomsAction()
				"Defer" -> DeferAction()
				"SquidlibFlowingCaveGenerator" -> SquidlibFlowingCaveGeneratorAction()
				"Take" -> TakeAction()
				"Datascope" -> DatascopeAction()
				"Rotate" -> RotateAction()
				"SetNamedArea" -> SetNamedAreaAction()
				"SquidlibSectionGenerator" -> SquidlibSectionGeneratorAction()
				else -> throw RuntimeException("Unknown classID '$classID' for AbstractMapGenerationAction!")
			}
		}

	}
}
