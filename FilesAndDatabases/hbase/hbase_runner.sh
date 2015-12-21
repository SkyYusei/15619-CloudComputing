#! /bin/bash

######################################################################
# Answer script for Project 3 module 1 Fill in the functions below ###
# for each question. This is the HBase section of the tasks.       ###
# Be sure to read the tutorial on how to write HBase queries       ###
# before attempting this section.                                  ###
######################################################################

#HBase Qustion 1
#What was that song whose name started with "Total" and ended with "Water"?
#Write an HBase query that finds the track that the person is looking for.
#The title starts with "Total" and ends with "Water", both are case sensitive
hbase_answer_1() {
    # Write an HBase query to get the answer to the question. 
    # Please put your HBase statement inside a file named exactly answer_1
    # Do not modify the scripts below, put all your code in answer_1
    # The code here will clean the hbase response to only contain the 
    # title of the song that is returned by your query

    if [ -f answer_1 ]
    then
     	cat answer_1 | hbase shell > results
     	cat results | python process.py
    else
        echo "answer_1 does not exist"
    fi
}

#HBase Qustion 2
#I don't remember the exact title, it was that song by "Kanye West", and the
#title started with either "Apologies" or "Confessions". Not sure which...
#Write an HBase query that finds the track that the person is looking for.
#The artist_name contains "Kanye West", and the title starts with either 
#"Apologies", or "Confessions", or both. (Case sensitive)
hbase_answer_2() {
    # Write an HBase query to get the answer to the question. 
    # Please put your HBase statement inside a file named exactly answer_2
    # Do not modify the scripts below, put all your code in answer_2
    # The code here will clean the hbase response to only contain the 
    # title of the song that are returned by your query

    if [ -f answer_2 ]
    then
     	cat answer_2 | hbase shell > results
     	cat results | python process.py
    else
        echo "answer_2 does not exist"
    fi
}

#HBase Question 3
#There was that new track by "Bob Marley" that was really long. Do you know?
#Write an HBase query that finds the track the person is looking for.
#The artist_name has a prefix of "Bob Marley", duration greater than 400, 
#and year 2000 and onwards. (Case sensitive)
hbase_answer_3() {
    # Write an HBase query to get the answer to the question. 
    # Please put your HBase statement inside a file named exactly answer_3
    # Do not modify the scripts below, put all your code in answer_3
    # The code here will clean the hbase response to only contain the 
    # title of the song that is returned by your query

    if [ -f answer_3 ]
    then
     	cat answer_3 | hbase shell > results
     	cat results | python process.py
    else
        echo "answer_3 does not exist"
    fi
}

#HBase Qustion 4
#I heard a really great song about "Family" by this really cute singer, 
#I think his name was "Consequence" or something...
#Write an HBase query that finds the track the person is looking for.
#The track has an artist_hotttnesss of at least 1, and the artist_name 
#contains "Consequence". Also, the title contains "Family" (Case sensitive)
hbase_answer_4() {
    # Write an HBase query to get the answer to the question. 
    # Please put your HBase statement inside a file named exactly answer_4
    # Do not modify the scripts below, put all your code in answer_4
    # The code here will clean the hbase response to only contain the 
    # title of the song that is returned by your query

    if [ -f answer_4 ]
    then
     	cat answer_4 | hbase shell > results
     	cat results | python process.py
    else
        echo "answer_4 does not exist"
    fi
}


# HBase Qustion 5
# Hey what was that "Love" song that "Gwen Guthrie" came out with in 1990?
# No, no, it wasn't the sad one, nothing "Bitter" or "Never"...
# Write an HBase query that finds the track the person is looking for.
# The track has an artist_name prefix of "Gwen Guthrie", the title contains "Love"
# but does NOT contain "Bitter" or "Never"
hbase_answer_5() {
    # Write an HBase query to get the answer to the question. 
    # Please put your HBase statement inside a file named exactly answer_5
    # Do not modify the scripts below, put all your code in answer_5
    # The code here will clean the hbase response to only contain the 
    # title of the song that is returned by your query

    if [ -f answer_5 ]
    then
     	cat answer_5 | hbase shell > results
     	cat results | python process.py
    else
        echo "answer_5 does not exist"
    fi
}

# DO NOT MODIFY ANYTHING BELOW THIS LINE

echo "{"

echo -en ' '\"hbase_answer1\": \"`hbase_answer_1`\"
echo ","

echo -en ' '\"hbase_answer2\": \"`hbase_answer_2`\"
echo ","

echo -en ' '\"hbase_answer3\": \"`hbase_answer_3`\"
echo ","

echo -en ' '\"hbase_answer4\": \"`hbase_answer_4`\"
echo ","

echo -en ' '\"hbase_answer5\": \"`hbase_answer_5`\"
echo
echo  "}"
