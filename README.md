# SpaceShips - DigiPen CSD3156 (~1.5 weeks) 

## Project Description

**SpaceShips** is a 2D space-themed Android game built with Kotlin and Gradle. The player controls a spaceship to navigate through 
waves of enemies, collecting power-ups and striving for the highest score. The game follows modern Android development practices 
and integrates multiple libraries and tools to enhance the user experience.

### Key Features:
- **Gameplay Mechanics**:
  - Control your spaceship using gyroscope-based motion controls.
  - Dynamic enemy spawning, with increasing difficulty over time.
  - Power-ups such as shields, health boosts, and damage boosts are available to enhance gameplay.
  - A scoring system that tracks player performance, with high scores stored locally in a database.

- **Game Architecture**:
  - Modular design, including classes for entities like `Player`, `Enemy`, `Projectile`, and `PowerUp`.
  - A central `GameEngine` class that handles game logic, physics, and rendering.
  - SQLite database integration via Room for saving high scores and game state.

- **Graphics and UI**:
  - Custom textures for the player, enemies, and projectiles.
  - Clean, minimalistic UI design for menus, high scores, and credits screens.
  - Dynamic UI elements displaying health and score indicators.

- **Audio**:
  - Background music and sound effects for various actions (shooting, damage, etc).
  - Audio management handled through a dedicated `SoundSystem` class.

- **Persistence**:
  - Game state persistence with Room database entities like `PlayerData`, `EnemyData`, and `ProjectilesData`.

### Contributions
- **Chia Yi Da**:
  - **Application Architecture**: Designed and implemented the core structure of the game, ensuring modularity and scalability.
  - **Game Engine**: Developed the `GameEngine` class, which handles game logic, physics updates, and rendering. 
  - **Database Integration**: Integrated Room database for high score and game state persistence.

- **Reuven Tan**:
  - **UI & Asset Design**: Designed and implemented the user interface, including menus, high scores screen, and credits, with a 
  clean and minimal design. Created custom textures for the player, enemies, and projectiles.
  - **UI Efficiency**: Managed ViewBinding for optimal UI handling and performance.
  - **Audio Axssets**: Developed backgroud music and sound effects for various in-game actions.

- **Ow Jian Wen**:
  - **Audio Systems**: Developed the audio system, to integrate custom audio assets into the application.
  - **Graphics Rendering**: Contributed to the rendering system, ensuring graphics were drawn smoothly on screen, focusing on
  rendering speed.

- **Jonathan Lim**:
  - **Core Gameplay Mechanics**: Developed the primary gameplay systems, including the enemy spawning logic, power-up mechanics, 
  and the overall scoring system.
  - **Gameplay Integration**: Focused on tuning the game mechanics, balancing difficulty, and ensuring a smooth gameplay experience.

### Gameplay Video

Watch the early-stage basic gameplay demonstration here:  
[![Basic Gameplay Video](https://img.youtube.com/vi/5mFxzlnwQf4/0.jpg)](https://www.youtube.com/watch?v=5mFxzlnwQf4)

### How to Play:
1. Launch the game from the main menu.
2. Tilt your device to control the spaceship's movement.
3. Avoid enemy projectiles and destroy enemies to earn points.
4. Collect power-ups to gain advantages.
5. Aim for the highest score possible!

### Credits:
- **Team Members**: Chia Yi Da, Reuven Tan, Ow Jian Wen, Jonathan Lim
- **Instructor**: Chen Kan
