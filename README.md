# Snake-Game-Machine-Learning
COSC 4P76 Machine Learning Final Project - Training AI to play Snake Game

Input:
Gameboard
Current direction
Apple location?
Snake head location?

Input Heuristics (three possible moves, with results below):
Gameboard spaces available
Distance to apple
Apple eaten
Death

Outputs:
Turn left
Turn Right
Go straight

Strategy:
AI chooses move
Evaluation function calculates best move
Error = usual error
eg.
AI:
Left = 0.3
Right = 0.6
Straight = 0.2
Evaluation:
Left = 0.0
Right = 0.0
Straight = 1.0
Error:
0.3 - 0.0 + 0.6 - 0.0 + 1.0 - 0.2 = 0.3 + 0.6 + 0.8 = 1.7 error
--> use error for back prop