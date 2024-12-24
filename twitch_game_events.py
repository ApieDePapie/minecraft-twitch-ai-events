import twitchio
from twitchio.ext import commands, eventsub
import minecraft_launcher_lib
import pyautogui
import json
import asyncio
import os
from datetime import datetime

# Configuration
TWITCH_CLIENT_ID = "YOUR_CLIENT_ID"
TWITCH_CLIENT_SECRET = "YOUR_CLIENT_SECRET"
TWITCH_ACCESS_TOKEN = "YOUR_ACCESS_TOKEN"
CHANNEL_NAME = "YOUR_CHANNEL_NAME"

# Game event configurations
GAME_EVENTS = {
    "duck_game": {
        "spawn_duck": {
            "min_donation": 5,
            "key_sequence": ["f4"]  # Example key to trigger duck spawn
        },
        "random_weapon": {
            "min_donation": 3,
            "key_sequence": ["f5"]  # Example key to give random weapon
        }
    },
    "minecraft": {
        "spawn_creeper": {
            "min_donation": 2,
            "command": "/summon creeper ~ ~ ~"
        },
        "lightning_strike": {
            "min_donation": 5,
            "command": "/summon lightning_bolt ~ ~ ~"
        },
        "give_diamond": {
            "min_donation": 10,
            "command": "/give @p diamond 1"
        }
    }
}

class TwitchBot(commands.Bot):
    def __init__(self):
        super().__init__(
            token=TWITCH_ACCESS_TOKEN,
            client_id=TWITCH_CLIENT_ID,
            nick=CHANNEL_NAME,
            prefix="!",
            initial_channels=[CHANNEL_NAME]
        )
        self.current_game = None
        
    async def event_ready(self):
        print(f"Bot ready | {self.nick}")
        
    async def handle_donation(self, donation_amount, donor_name):
        if not self.current_game:
            print("No game currently selected!")
            return
            
        game_events = GAME_EVENTS.get(self.current_game.lower())
        if not game_events:
            print(f"No events configured for game: {self.current_game}")
            return
            
        triggered_events = []
        for event_name, event_config in game_events.items():
            if donation_amount >= event_config["min_donation"]:
                triggered_events.append((event_name, event_config))
                
        if not triggered_events:
            print(f"Donation amount {donation_amount} not enough to trigger any events")
            return
            
        # Execute random event from possible triggered events
        import random
        event_name, event_config = random.choice(triggered_events)
        
        print(f"Executing event {event_name} for donation of ${donation_amount} from {donor_name}")
        
        if self.current_game == "minecraft":
            await self.execute_minecraft_event(event_config["command"])
        elif self.current_game == "duck_game":
            await self.execute_duck_game_event(event_config["key_sequence"])
            
    async def execute_minecraft_event(self, command):
        # Simulate pressing 't' to open chat, type command, and press enter
        pyautogui.press('t')
        await asyncio.sleep(0.1)
        pyautogui.write(command)
        await asyncio.sleep(0.1)
        pyautogui.press('enter')
        
    async def execute_duck_game_event(self, key_sequence):
        for key in key_sequence:
            pyautogui.press(key)
            await asyncio.sleep(0.1)
            
    @commands.command(name="setgame")
    async def set_game(self, ctx, game_name: str):
        game_name = game_name.lower()
        if game_name in GAME_EVENTS:
            self.current_game = game_name
            await ctx.send(f"Current game set to: {game_name}")
        else:
            await ctx.send(f"Game {game_name} not supported. Supported games: {', '.join(GAME_EVENTS.keys())}")

async def main():
    bot = TwitchBot()
    
    # Set up EventSub for donations (you'll need to implement this based on your donation service)
    # This is a placeholder for donation webhook handling
    
    await bot.start()

if __name__ == "__main__":
    asyncio.run(main())