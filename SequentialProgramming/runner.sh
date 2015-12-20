#! /bin/bash

######################################################################
# Answer script for Project 1 module 1 Fill in the functions below ###
# for each question. You may use any other files/scripts/languages ###
# in these functions as long as they are in the submission folder. ###
######################################################################

# Write or invoke the code to perform filtering on the dataset. Redirect 
# the filtered output to a file called 'output' in the current folder.

answer_0() {
        # Fill in this Bash function to filter the dataset and redirect the 
        # output to a file called 'output'. 
	# Example: 
	#	python filter.py > output
	python filter.py 
	cat temp | sort -nr -t$'\t' -k2 > output
}

# How many lines (items) were originally present in the input file 
# pagecounts-20150801-000000 i.e line count before filtering
# Run your commands/code to process the dataset and echo a 
# single number to standard output
answer_1() {
        # Write a function to get the answer to Q1. Do not just echo the answer.
	python answer1.py
}

# Before filtering, what was the total number of requests made to all 
# of wikipedia (all subprojects, all elements, all languages) during 
# the hour covered by the file pagecounts-20150801-000000
# Run your commands/code to process the dataset and echo a 
# single number to standard output
answer_2() {
        # Write a function to get the answer to Q2. Do not just echo the answer.
	python answer2.py
}

# How many lines emerged after applying all the filters?
# Run your commands/code to process the dataset and echo a 
# single number to standard output
answer_3() {
        # Write a function to get the answer to Q3. Do not just echo the answer.
	python answer3.py
}

# What was the most popular article in the filtered output?
# Run your commands/code to process the dataset and echo a 
# single word to standard output
answer_4() {
        # Write a function to get the answer to Q4. Do not just echo the answer.
	python answer4.py
}

# How many views did the most popular article get?
# Run your commands/code to process the dataset and echo a 
# single number to standard output
answer_5() {
        # Write a function to get the answer to Q5. Do not just echo the answer.
	python answer5.py
}

# What is the count of the most popular movie in the filtered output? 
# (Hint: Entries for movies have "(film)" in the article name)
# Run your commands/code to process the dataset and echo a 
# single number to standard output
answer_6() {
        # Write a function to get the answer to Q6. Do not just echo the answer.
	python answer6.py
}

# How many articles have more than 2500 views in the filtered output?
# Run your commands/code to process the dataset and echo a 
# single number to standard output
answer_7() {
        # Write a function to get the answer to Q7. Do not just echo the answer.
	python answer7.py
}


# How many views are there in the filtered dataset for all "episode lists".
# Episode list articles have titles that start with "List_of" and end with "episodes"
# Run your commands/code to process the dataset and echo a number to standard output
# Both strings above are case sensitive
answer_8() {
        # Write a function to get the answer to Q8. Do not just echo the answer.
	python answer8.py
}

# What is most popular in this hour, "(2014_film)" or "(2015_film)" articles?
# Both strings above are case sensitive
answer_9() {
        # Write a function to get the answer to Q9. Do not just echo the answer.
        # The function should return either 2014 or 2015.
	python answer9.py
}


# DO NOT MODIFY ANYTHING BELOW THIS LINE
answer_0 &> /dev/null

echo "The results of this run are : "
echo "{"

if [ -f 'output' ]
then
        echo -en ' '\"answer0\": \"'output' file created\"
        echo ","
else
        echo -en ' '\"answer0\": \"No 'output' file created\"
        echo ","
fi

a1=`answer_1`
echo -en ' '\"answer1\": \"$a1\"
echo $a1 > .1.out
echo ","

a2=`answer_2`
echo -en ' '\"answer2\": \"$a2\"
echo $a2 > .2.out
echo ","

a3=`answer_3`
echo -en ' '\"answer3\": \"$a3\"
echo $a3 > .3.out
echo ","

a4=`answer_4`
echo -en ' '\"answer4\": \"$a4\"
echo $a4 > .4.out
echo ","

a5=`answer_5`
echo -en ' '\"answer5\": \"$a5\"
echo $a5 > .5.out
echo ","

a6=`answer_6`
echo -en ' '\"answer6\": \"$a6\"
echo $a6 > .6.out
echo ","

a7=`answer_7`
echo -en ' '\"answer7\": \"$a7\"
echo $a7 > .7.out
echo ","

a8=`answer_8`
echo -en ' '\"answer8\": \"$a8\"
echo $a8 > .8.out
echo ","

a9=`answer_9`
echo -en ' '\"answer9\": \"$a9\"
echo $a9 > .9.out
echo ""

 
echo  "}"

echo ""
echo "If you feel these values are correct please run:"
echo "./submitter -a andrewID -p submission_password"
