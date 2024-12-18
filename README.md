# Auto Golden Head Mod

Eats a Golden Head whenever your health is under a threshold specified by the user 

**NOTE**: **This mod may be blacklisted on some servers, PLEASE check server rules to avoid being banned. It was not made for the intent of cheating on servers.**
## Features

- **Toggle Command**: `/AutoGoldenHead` to toggle the mod on or off.
- **Health Threshold Command**: `/AutoGoldenHead health <value>` to set the health threshold at which the Golden Head is used.

## Commands

### `/AutoGoldenHead`

Toggles the Auto Golden Head mod on or off.

- **Usage**: `/AutoGoldenHead`
- **Response**: Sends a message to the player indicating whether the mod is **enabled** (green) or **disabled** (red).

### `/AutoGoldenHead health <integer>`

Sets the health threshold at which the Golden Head is used. The default threshold is set to 3 (1.5 hearts).

- **Usage**: `/AutoGoldenHead health <value>`
- **Valid Range**: From 1 to 20 (1 = 0.5 hearts, 20 = 10 hearts).
- **Example**: `/AutoGoldenHead health 5` sets the threshold to 2.5 hearts.
- **Response**: Sends a message confirming the new health threshold.

