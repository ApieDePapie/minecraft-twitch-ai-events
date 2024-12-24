package dev.allhands.twitchaievents;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class TwitchAIEvents implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("twitch-ai-events");
    private TwitchClient twitchClient;
    private static final Random RANDOM = new Random();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Twitch AI Events 2025!");
        
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            initializeTwitchClient();
        });
    }

    private void initializeTwitchClient() {
        OAuth2Credential credential = new OAuth2Credential(
            "twitch",
            System.getenv("TWITCH_ACCESS_TOKEN")
        );

        twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true)
            .withEnablePubSub(true)
            .withChatAccount(credential)
            .build();

        // Register reward redemption handler
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(
            credential,
            System.getenv("TWITCH_CHANNEL_ID")
        );

        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, this::handleReward);
    }

    private void handleReward(RewardRedeemedEvent event) {
        String rewardTitle = event.getRedemption().getReward().getTitle();
        String userName = event.getRedemption().getUser().getDisplayName();
        
        switch (rewardTitle) {
            case "AI Robot Invasion":
                spawnAIRobots(userName);
                break;
            case "Neural Network Storm":
                createNeuralStorm(userName);
                break;
            case "2025 Time Warp":
                trigger2025TimeWarp(userName);
                break;
            case "Quantum Computing Chaos":
                triggerQuantumChaos(userName);
                break;
            case "AI Fireworks Show":
                launchAIFireworks(userName);
                break;
        }
    }

    private void spawnAIRobots(String userName) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = player.getServerWorld();
                BlockPos pos = player.getBlockPos();

                // Spawn iron golems with glowing effect as "AI Robots"
                for (int i = 0; i < 3; i++) {
                    BlockPos spawnPos = pos.add(RANDOM.nextInt(10) - 5, 1, RANDOM.nextInt(10) - 5);
                    EntityType.IRON_GOLEM.spawn(world, spawnPos, SpawnReason.EVENT);
                }

                // Add visual effects
                world.spawnParticles(ParticleTypes.END_ROD, 
                    pos.getX(), pos.getY() + 1, pos.getZ(),
                    50, 3, 3, 3, 0.1);

                broadcastMessage(world, userName + " has initiated an AI Robot invasion!");
            }
        });
    }

    private void createNeuralStorm(String userName) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = player.getServerWorld();
                BlockPos pos = player.getBlockPos();

                // Create lightning storm effect
                for (int i = 0; i < 5; i++) {
                    BlockPos strikePos = pos.add(RANDOM.nextInt(20) - 10, 0, RANDOM.nextInt(20) - 10);
                    world.spawnEntity(EntityType.LIGHTNING_BOLT.create(world));
                }

                // Add "neural network" particle effects
                for (int i = 0; i < 100; i++) {
                    world.spawnParticles(ParticleTypes.ENCHANTED_HIT,
                        pos.getX() + RANDOM.nextDouble() * 10 - 5,
                        pos.getY() + RANDOM.nextDouble() * 5,
                        pos.getZ() + RANDOM.nextDouble() * 10 - 5,
                        1, 0, 0, 0, 0.1);
                }

                broadcastMessage(world, userName + " has unleashed a Neural Network Storm!");
            }
        });
    }

    private void trigger2025TimeWarp(String userName) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = player.getServerWorld();
                
                // Apply special effects to all players in range
                Box area = new Box(player.getBlockPos()).expand(30);
                List<ServerPlayerEntity> nearbyPlayers = world.getEntitiesByType(
                    EntityType.PLAYER, area, p -> true);

                for (ServerPlayerEntity nearbyPlayer : nearbyPlayers) {
                    // Add futuristic effects
                    nearbyPlayer.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1));
                    nearbyPlayer.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.GLOWING, 200, 1));
                    nearbyPlayer.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 1));
                }

                // Visual and sound effects
                world.playSound(null, player.getBlockPos(), 
                    SoundEvents.BLOCK_BEACON_ACTIVATE, 
                    SoundCategory.AMBIENT, 1.0F, 1.0F);

                broadcastMessage(world, userName + " has initiated a temporal shift to 2025!");
            }
        });
    }

    private void triggerQuantumChaos(String userName) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = player.getServerWorld();
                BlockPos pos = player.getBlockPos();

                // Teleport players randomly within a safe range
                BlockPos newPos = pos.add(
                    RANDOM.nextInt(20) - 10,
                    RANDOM.nextInt(5),
                    RANDOM.nextInt(20) - 10
                );
                player.teleport(newPos.getX(), newPos.getY(), newPos.getZ());

                // Quantum particle effects
                world.spawnParticles(ParticleTypes.PORTAL,
                    pos.getX(), pos.getY(), pos.getZ(),
                    100, 3, 3, 3, 0.1);

                broadcastMessage(world, userName + " has destabilized quantum reality!");
            }
        });
    }

    private void launchAIFireworks(String userName) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = player.getServerWorld();
                BlockPos pos = player.getBlockPos();

                // Launch multiple fireworks in patterns
                for (int i = 0; i < 10; i++) {
                    BlockPos fireworkPos = pos.add(
                        RANDOM.nextInt(20) - 10,
                        1,
                        RANDOM.nextInt(20) - 10
                    );
                    
                    // Create firework entity with custom effects
                    EntityType.FIREWORK_ROCKET.spawn(world, fireworkPos, SpawnReason.EVENT);
                }

                broadcastMessage(world, userName + " has launched an AI-powered fireworks show!");
            }
        });
    }

    private void broadcastMessage(World world, String message) {
        if (world.isClient) return;
        
        for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
            player.sendMessage(Text.literal("§b[AI Events] §r" + message));
        }
    }
}