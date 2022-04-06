package com.teamremastered.endrem.world.structures;

import com.google.common.collect.ImmutableList;
import com.teamremastered.endrem.EndRemastered;
import com.teamremastered.endrem.config.ERConfig;
import com.teamremastered.endrem.utils.ERUtils;
import com.teamremastered.endrem.world.structures.config.ERStructures;
import com.teamremastered.endrem.world.structures.utils.CustomMonsterSpawn;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.entity.MobCategory.MONSTER;


public class EndGate extends StructureFeature<JigsawConfiguration> {
    private static final ResourceLocation START_POOL = new ResourceLocation(EndRemastered.MOD_ID, "end_gate/start_pool");
    private static final List<CustomMonsterSpawn> MONSTER_SPAWN_LIST =  ImmutableList.of(
            new CustomMonsterSpawn(EntityType.SKELETON, 30, 30, 35),
            new CustomMonsterSpawn(EntityType.ZOMBIE, 20, 25, 30),
            new CustomMonsterSpawn(EntityType.CAVE_SPIDER, 20, 25, 30),
            new CustomMonsterSpawn(EntityType.WITCH, 10, 10, 15)
    );

    public EndGate() {
        super(JigsawConfiguration.CODEC, EndGate::createPiecesGenerator, PostPlacementProcessor.NONE);
    }

    @Override
    public GenerationStep.@NotNull Decoration step() {
        return GenerationStep.Decoration.STRONGHOLDS;
    }

    private BlockPos getLocatedRoom(StructureStart structureStart) {
        List<StructurePiece> structurePieces = structureStart.getPieces();
        BoundingBox bbox = structurePieces.get(Math.min(16, structurePieces.size()) - 1).getBoundingBox();
        return new BlockPos(bbox.minX(), bbox.minY(), bbox.minZ());
    }

    public static void setupStructureSpawns(final StructureSpawnListGatherEvent event) {
        if(event.getStructure() == ERStructures.END_GATE.get()) {
            for (CustomMonsterSpawn monsterSpawn : MONSTER_SPAWN_LIST) {
                event.addEntitySpawn(MONSTER, monsterSpawn.getIndividualMobSpawnInfo());
            }
        }
    }

    private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
        return ERUtils.getChunkDistanceFromSpawn(context.chunkPos()) >= ERConfig.END_GATE_SPAWN_DISTANCE.getRaw();
    }

    public static @NotNull Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
        if (!EndGate.isFeatureChunk(context)) {
            return Optional.empty();
        }

        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0).atY(ERConfig.END_GATE_HEIGHT.getRaw());

        Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator = JigsawPlacement.addPieces(
                context,
                PoolElementStructurePiece::new,
                blockpos,
                false,
                false
        );
        return structurePiecesGenerator;
    }
}
