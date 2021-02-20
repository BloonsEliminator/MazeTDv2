# MazeTD
[![mazetd gameplay demo video](https://i.imgur.com/ZHog3cc.png)](https://youtu.be/Azjz-Dk3fXo)

## Welcome to MazeTD

Redefine the map by building a maze with randomly generated towers to stop the onslaught of enemies!

## How to play
1. Select a game mode
2. Place 5 towers to build your maze
3. Keep only 1 tower / Combine only 1 tower up 1 tier if there is a duplicate / Repick towers if not used yet
4. Watch your towers automatically battle the enemies!
5. Repeat steps 2-4 to continue building your maze
6. Have fun!

## Game Modes
- SURVIVAL MODE: Complete 40 levels then survive as long as possible!
- TIME TRIAL MODE: Complete 40 levels as fast as possible!
- LAST STAND MODE: Deal the most amount of damage after 40 levels!

## Hotkeys
- (T) place towers/repick towers
- (R) keep towers/remove walls
- (C) combine towers
- (Q) toggle hyper speed
- (H) view highscores
- (esc) return/exit game


## Key changes since inital design
- Only 1 difficulty mode, but maintained the same 3 different game modes
- Time Trial now tracks not only time spent in battle but also time spent in tower placement phase (essentially tracks real time)
- 14 classes instead of the projected 7 classes


## Key additions since initial design
Game Class:
- Maintains a highscore system with 3 text files to store the top 5 scores of each game mode
- Game mode can be selected and changed at any time within the game
- Game selection (1) and all game modes (3) all have their own unique music for a total of 4 background songs added

DrawingPanel Class:
- Clicking on an enemy will show its stats now
- Can combine towers if duplicate exists instead of just keeping them (however, only 1 tier can be combined at a time; can do t1+t1 = t2 or t3+t3 = t4; cannot do t1+t1+t1+t1 = t3 or t1+t1+t2 = t3)
- Score is calculated differently depending on game mode: Survival is based on level, Time Trial is based on elapsed # of seconds, and Last Stand is based on amount of damage dealt on the last level (41)
- Removed swap enemies function because it produced the unforseen consequence of many enemies being stacked on top of each other after a while of battle(caused by the slow debuff always being applied to the front most enemy)
- Tier chances increase every other level
- Prompts user for a username if they set a new highscore and saves it to the highscore txt files

Other Classes:
- OptionPanel: Added hotkeys for buttons
- StatPanel: More stats like enemy type and tier chances
- SelectedPanel: Has a separate set of labels for when an enemy is selected vs when a tower is selected
- ScorePanel (new class): Shows highscores read from text files


## Known potential bugs
- projectiles may not always hit the center of their target enemy
- enemy stats may display numbers with over 10 decimal places and get cut off
- screen may lag or stutter in hyperspeed mode
- mouse clicks may not always register (if you move your mouse while clicking, the mouse is considered dragged, not clicked; only mouse clicks register as actions)
