# FirstTask
The file is located there - https://wri-dataportal-prod.s3.amazonaws.com/27c/271/ef-63c3-49c5-a06a-f21bb7b96371/globalpowerplantdatabasev110.zip

  Tasks:
1. Implement a class that calculates the following information in CVS format into the file powerplants.csv using the file data:
    1. The total installed capacity of all existing power plants
    2. Countries with the largest and smallest amount of gas power plants
    3. The year in which the largest number of power plants was put into operation
2. Implement a class that displays in the geo-stats.csv file information on the number of power plants on each continent 
(for this task additional file data.csv, which contains information about contries in each continent, was used) 

  Additional tasks: 
1. Split the source file into 3 parts and rewrite program for their competitive processing
1. Add logging using third-party libraries.
2. Create a utility service for parsing resources using third-party libraries.
3. Move all utility classes to a separate package.
4. Use the typesafe config library to load the config.

Path to files are defined in src/main/resources/app.conf
Log config location: src/main/resources/logback.xml

