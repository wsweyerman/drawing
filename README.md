Simulates a drawing hat full of names where each person has the opportunity to
draw a slip of paper from the hat to be matched to another person.

Enforces basic rules such as a person cannot draw their own name and two people
may not draw each other's names.

Input to the program allows the user to specify the names that should go in the
hat as well as further combinations that are disallowed in a file. The format
for this file is
person_name_1
disallowed_1
...
disallowed_n

person_name_2
disallowed_1
...

Of note, is that each name is on its own line, all names following the first
are disallowed names until an empty line is reached. The next line containing
text following an empty line is the next name that is in the hat.

The program writes output to both stdout and a text file in the format:
drawer -> drawee.

As an example usage, suppose the drawing is being performed for a gift exchange
between three individuals, Alice, Bob, and Eve where Alice may not purchase a
gift for Eve due to company policy (yes, the output is fully determined based
on these constraints). The input file is:
Alice
Eve

Bob

Eve

If my name is Bob, I would search for the line:
Alice -> Bob
to discover that Alice will be getting me a gift. Wait. No. I would search for
the line
Bob -> Eve
to discover that I will be getting a gift for Eve.

Due to the rules mentioned above, the following are disallowed:

* Alice may not draw Alice
Alice -> Alice.

* Alice may not draw Bob if Bob draws Alice:
Alice -> Bob
Bob -> Alice.

* Alice may not draw Eve
Alice -> Eve.

The program can also be used to simulate multiple subsequent drawings where
previous combinations are also disallowed. Thus, if I am Bob and the previous
drawing contained the line
Bob -> Eve,
this line may not be allowed in the new drawing.

The program does not attempt to determine if based on previous drawings there
does not exist a valid drawing and in this case will not terminate.

Usage of the program is:
usage: DrawingHat --input <input> --output <output> [--prev <prev_0> ... <prev_n>]
    --input <input>     Input file with people to include in drawing and
                        disallowed pairings.
    --output <output>   Filename to write the output of the drawing.
    --prev <prev>       Previous output files to read as disallowed
                        pairings.