# Advent of Code 2022

Contains my Kotlin solutions for Advent of Code 2022.

## Directory structure
```
.
├── README.md               README file
└── src/main/kotlin
    ├── util             Utils for reading the input files etc
    ├── day1
        ├── main.kt      An optional Day 01 test input data used for checks
        ├── input.txt    Input is gitignored as requested. Download from AoC-website.
    ├── day2
        ├── main.kt      An optional Day 01 test input data used for checks
        ├── input.txt    Input is gitignored. Download from AoC-website.
    ├── dayN             ...
```

## Puzzles and solutions

| Puzzle                                                               | Solution                                       | Themes                                                                               |
|----------------------------------------------------------------------|------------------------------------------------|--------------------------------------------------------------------------------------|
| [Day 1 - calory counting](https://adventofcode.com/2022/day/1)       | [Day 1 solution](src/main/kotlin/day1/main.kt) | Group nums. Sum and find groups with max sum.                                        |
| [Day 2 - rock, paper, scissors](https://adventofcode.com/2022/day/2) | [Day 2 solution](src/main/kotlin/day2/main.kt) | Implement logic for a simple game. Resolve moves, play rounds, calculate points, ... |
| [Day 3 - rucksack organization](https://adventofcode.com/2022/day/3) | [Day 3 solution](src/main/kotlin/day3/main.kt) | Find letters in groups of CharArrays.                                                |
| [Day 4 - camp cleanup](https://adventofcode.com/2022/day/4)          | [Day 4 solution](src/main/kotlin/day4/main.kt) | Find overlapping pairs of IntRanges.                                                 |