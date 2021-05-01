# Snake-Game-Machine-Learning
COSC 4P76 Machine Learning Final Project - Training AI to play Snake Game

Philip Akkerman - 5479613
David Hasler - 6041321

Compile and run the Main.java class in the src folder to run the program.
Follow the prompts in the command line to use the program, and enter your responses in the command prompt.
Options are as follows:
 1: Allows a human user to play a randomly generated game
 2: Allows the evaluation function user to play a randomly generated game
 3: Allows choosing of training parameters to train an AI, save it to an output file, and play test games with it
    - Example parameters are provided, and can be modified as needed
 4: Allows loading of a saved AI to then play games with using specified seeds (-1 for random)
 5: Runs pre-made training experiments
 6: Allows loading of a saved AI to run 1000 random testing games, printing out scores and seeds used

============================================================================================
Example Run Option 1: (note: game begins when an arrow key is pressed)
============================================================================================
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
1

--> The game window will appear, and let you play the game using the arrow keys after selecting the window

Random seed = 7839490641064919040
Gameover = true
1: Play another game
0: Quit
0

Process finished with exit code 0

============================================================================================
Example Run Option 2:
============================================================================================
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
2
Random seed = 4723895370892042240
--> The game window will appear, and let you watch a game played by the evaluation function
1: Play another game
0: Quit
0

Process finished with exit code 0

============================================================================================
Example Run Option 3: (this setup achieved a score of 11)
============================================================================================
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
3
Choose number of hidden layers
2
Choose number of nodes in hidden layer 1
100
Choose number of nodes in hidden layer 2
50
Set IncreaseRate (e.g. 1.2)
1.6
Set DecreaseRate (e.g. 0.5)
0.2
Set max step (e.g. 50.0)
10
Set min step (e.g. 0.0001)
0.000001
Set max epochs (e.g. 1000)
1000
Set moves per epoch (e.g. 1000)
1000
Choose a random seed (-1 for Math.random)
0
Epoch:0 Error:0.7553572375697567
Epoch:50 Error:0.43004503460627647
Epoch:100 Error:0.554181732876856
Epoch:150 Error:0.22669746689170148
Epoch:200 Error:0.197277154399613
Epoch:250 Error:0.17136845407501797
Epoch:300 Error:0.1700216954752945
Epoch:350 Error:0.14473949806513844
Epoch:400 Error:0.13633457644636568
Epoch:450 Error:0.12597531253335775
Epoch:500 Error:0.11938685197397496
Epoch:550 Error:0.11319011359285769
Epoch:600 Error:0.10784202320083335
Epoch:650 Error:0.10383842746403059
Epoch:700 Error:0.10047955665431287
Epoch:750 Error:0.09768136054171973
Epoch:800 Error:0.0948851042852975
Epoch:850 Error:0.09265767565334322
Epoch:900 Error:0.10710641838719713
Epoch:950 Error:0.08751833040488845
Epoch:999 Error:0.08471354597295916
Parameters used:
Layers & Nodes:
1	100
2	50
Learning rule: 	rProp
Increase rate:	1.6
Decrease rate:	0.2
Max step:	10.0
Min step:	1.0E-6
Max epochs:	1000
Moves per epoch:	1000
Random Seed (-1 for randoms):	0

Save neural network?
1: yes
2: no
1
Choose file path
C:\Users\phili\Google_Drive\Brock_Computer_Science_Degree\COSC_4P76_Machine_Learning\SavedNeuralNetworks\TestDemo
Choose a random seed (-1 for Math.random)
0
--> Game window appears here and you can watch the newly trained AI play a game on the chosen seed (0 in this case)
Play another game?
1: Yes
2: Quit
2
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
0

Process finished with exit code 0

============================================================================================
Example Run Option 4:
============================================================================================
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
4
Choose file path
C:\Users\phili\Google_Drive\Brock_Computer_Science_Degree\COSC_4P76_Machine_Learning\SavedNeuralNetworks\TestDemo
Layers.length = 4, weights&Biases.length = 3
inputNeurons = 12, outputNeurons = 3
Choose a random seed (-1 for Math.random)
0
--> Game window appears here and you can watch the saved AI play a game on the chosen seed (0 in this case)
Play another game?
1: Yes
2: Quit
2
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
0

Process finished with exit code 0
============================================================================================
Example Run Option 5:
============================================================================================
This option is used to run pre-set training examples. Modify the parameters in Main.runTrainingParameters() to setup a training experiment
============================================================================================
Example Run Option 6:
Note: When we saved the scores and seeds to Excel, it replaced the last 4 digits with 0's,
so the results in our ExperimentResults Excel files unfortunately cannot be reproduced
============================================================================================
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
6
Choose file path
C:\Users\phili\Google_Drive\Brock_Computer_Science_Degree\COSC_4P76_Machine_Learning\SavedNeuralNetworks\TestDemo
Layers.length = 4, weights&Biases.length = 3
inputNeurons = 12, outputNeurons = 3
Finished test 0
Finished test 1
Finished test 2
...
Finished test 997
Finished test 998
Finished test 999
Score = 14, seed = 7615158413181338624
Score = 15, seed = 3356212042775187456
Score = 16, seed = 3450311743271133184
...
Score = 19, seed = 6822371588386137088
Score = 10, seed = 3029568109522979840
Score = 16, seed = 7465350816774200320
1: Play a game (human)
2: Let the evaluation function play a game
3: Train AI
4: Play a game with a saved AI
5: Run training experiments
6: Run testing experiments
0: Quit
0

Process finished with exit code 0