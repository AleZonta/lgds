# lgds
Load GPS trajectories from file and simulate the people movement using those trajectories

This project implements classes to load trajectories datasets from file/files and simulate the movement of the people following the trajectories just uploaded.
Now the project support the [https://www.microsoft.com/en-us/download/details.aspx?id=52367&from=http%3A%2F%2Fresearch.microsoft.com%2Fen-us%2Fdownloads%2Fb16d359d-d164-469e-9fd4-daa38f2b2e13%2F](Geolife Dataset) and the trace obtained running the [https://github.com/TNOCS/idsa](IDSA simulator)


## How to build

The source code is written in Java 8 and uses Apache Maven for project dependencies and settings (see pom.xml). It can be built and run using popular Java IDEs such as Eclipse, NetBeans and IntelliJ.


## How to run

Complete the file settings.json with the information required and run the program. Normaly this project is used as a library for other bigger projects
