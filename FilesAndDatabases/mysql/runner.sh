#! /bin/bash

######################################################################
# Answer script for Project 3 module 1 Fill in the functions below ###
# for each question. You may use any other files/scripts/languages ###
# in these functions as long as they are in the submission folder. ###
######################################################################

# Qustion 1
# How many rows match 'Aerosmith' (Case sensitive) through the grep command?
# Run your commands/code to process the dataset and output a
# single number to standard output 
answer_1() {

	# use grep the choose Aerosmith and use wc -l to count the number
	grep -P 'Aerosmith' million_songs_metadata.csv| wc -l
}

# Qustion 2
# Write grep commands that result in the total number of track_id(s) with 
# artist_name containing "Bob Marley" (Case sensitive)
# Run your commands to process the dataset and output a single number 
# to standard output 
answer_2() {
	#cut the 7th col and use grep to select the Bob and use wc -l to count the number
	cut -d ',' -f7  million_songs_metadata.csv | grep -P 'Bob Marley' | wc -l
}

# Qustion 3
# How many rows match 'The Beatles' (Case sensitive) through the awk command 
# on column 7? The output should be a single number
# Your script should be a single awk command
answer_3() {
	#use i to count the number the lines which has The Beatles inside
	 awk ' BEGIN {FS = ","} ; {if ($7 ~ /The Beatles/) i=i+1};END{print i}' million_songs_metadata.csv
}

# Qustion 4
# Write awk code to do the equivalent of the SQL query SELECT
# AVG(duration) FROM songs;. The code should output a single number.
# Your script should be a single awk command
answer_4() {
	#use i to count the sum of duration and use j to count the total lines
	 awk ' BEGIN {FS = ",";i=0; j=0} ; {i += $8 ; j++};END{print i/j}' million_songs_metadata.csv
}

# Qustion 5
# Invoke the awk / shell program or the set of commands that you wrote
# to merge the two files into the file million_songs_metadata_and_sales.csv
# in current folder.
answer_5() {
	# just use join the join two tables
	join -t ',' million_songs_sales_data.csv million_songs_metadata.csv > million_songs_metadata_and_sales.csv
}

# Question 6 
# Find the artist with maximum sales using the file million_songs_metadata_and_sales.csv.
# The output of your command(s) should be the artist name.
# NOTE: Artists can have many different artist_names, but only one artist_id, 
# which is unique to each artist. You should find the maximum sales based 
# on artist_id, and return any of that artist_id’s 
# valid artist_name as the result.
answer_6() {
	#just do like map-reduce
	grep `cat million_songs_metadata_and_sales.csv | sort -t"," -k 7 |awk 'BEGIN{FS = ","};{if((key == $7)||(key == "")) {sum += $3}};{if(($7 != key)&&(key != "")){print key "#" sum; sum = $3}};{key = $7}' | sort -nr -t"#" -k2 | head -n 1 | cut -d"#" -f 1` million_songs_metadata_and_sales.csv | head -n 1 | cut -d"," -f 9
} 


# Qustion 7
# Write a SQL query that returns the trackid of the song with the maximum duration
answer_7() {
    # Write a SQL query to get the answer to Q7. Do not just echo the answer.
    # Please put your SQL statement within the double quotation marks, and 
    # don't modify the command outside the double quotation marks.
    # If you need to use quotation marks in you SQL statment, please use
    # single quotation marks instead of double.

    #select the max duration and select track_id whose duration = max
    mysql --skip-column-names --batch -u root -pdb15319root song_db -e "SELECT track_id FROM songs WHERE duration = (SELECT MAX(duration) FROM songs);" 
}

# Question 8
# A database index is a data structure that improves the speed of data retreival.
# Identify the field that will improve the performance of query in question 9 
# and create a database index on that field
INDEX_NAME="d_index"
answer_8() {
	# Write a SQL query that will create a index on the field
	mysql --skip-column-names --batch -u root -pdb15319root song_db -e "CREATE INDEX d_index ON songs (duration);"
}

# Question 9
# Write a SQL query that returns the trackid of the song with the maximum duration
# This is the same query as Question 7. Do you see any difference in performance?
answer_9() {
	# Write a SQL query to get the answer to Q9. Do not just echo the answer.
	# Please put your SQL statement within the double quotation marks, and 
	# don't modify the command outside the double quotation marks.
	# If you need to use quotation marks in you SQL statment, please use
	# single quotation marks instead of double.

	#order first and choose the first line 
	mysql --skip-column-names --batch -u root -pdb15319root song_db -e "SELECT track_id FROM songs ORDER BY duration DESC LIMIT 1;"
}

#Question 10
# Write the SQL query that returns all matches (across any column), 
# similar to the command grep -P 'The Beatles' | wc -l:
answer_10() {
	# Write a SQL query to get the answer to Q10. Do not just echo the answer.
	# Please put your SQL statement within the double quotation marks, and 
	# don't modify the command outside the double quotation marks.
	# If you need to use quotation marks in you SQL statment, please use
	# single quotation marks instead of double.
	
	#use regex to select the The Beatles
	mysql --skip-column-names --batch -u root -pdb15319root song_db -e "SELECT COUNT(*) FROM songs WHERE title REGEXP BINARY '.*(The Beatles).*' OR songs.release REGEXP BINARY '.*(The Beatles).*' OR artist_name REGEXP BINARY '.*(The Beatles).*';"
}

#Question 11
# Which artist has the third-most number of rows in Table songs?
# The output should be the name of the artist.
# Please use artist_id as the unique identifier of the artist
answer_11() {
	# Write a SQL query to get the answer to Q11. Do not just echo the answer.
	# Please put your SQL statement within the double quotation marks, and 
	# don't modify the command outside the double quotation marks.
	# If you need to use quotation marks in you SQL statment, please use
	# single quotation marks instead of double.
	
	#choose the third's artist_id first and select artist_name	
	mysql --skip-column-names --batch -u root -pdb15319root song_db -e "SELECT artist_name FROM songs WHERE artist_id = (SELECT artist_id FROM songs GROUP BY artist_id ORDER BY count(*) DESC LIMIT 2, 1) LIMIT 1;"
}


# Answer the following questions corresponding to your experiments 
# with sysbench benchmarks in Step 3: Vertical Scaling

# Answer the following questions corresponding to your experiments on t1.micro instance

# Question 12
# Please output the RPS (Request per Second) values obtained from 
# the first three iterations of FileIO sysbench executed on t1.micro 
# instance with magnetic EBS attached. 
answer_12() {
	# Echo single numbers on line 1, 3, and 5 within quotation marks
	echo "100.33"
	echo ,
	echo "97.21"
	echo ,
	echo "107.85"
}

# Question 13
# Please output the RPS (Request per Second) values obtained from
# the first three iterations of FileIO sysbench executed on t1.micro
# instance with SSD EBS attached. 
answer_13() {
	# Echo single numbers on line 1, 3, and 5 within quotation marks
	echo "475.66" 
	echo ,
	echo "476.73"
	echo ,
	echo "503.52"
}

# Answer the following questions corresponding to your experiments on m3.large instance

# Question 14
# Please output the RPS (Request per Second) values obtained from
# the first three iterations of FileIO sysbench executed on m3.large
# instance with magnetic EBS attached. 
answer_14() {
	# Echo single numbers on line 1, 3, and 5 within quotation marks
	echo "111.74"
	echo ,
	echo "230.11"
	echo ,
	echo "322.05"
}

# Question 15
# Please output the RPS (Request per Second) values obtained from
# the first three iterations of FileIO sysbench executed on m3.large
# instance with SSD EBS attached.
answer_15() {
	# Echo single numbers on line 1, 3, and 5 within quotation marks
	echo "916.34"
	echo ,
	echo "1455.52"
	echo ,
	echo "1486.97"
}

# Question 16
# For the FileIO benchmark in m3.large, why does the RPS value vary in each run
# for both Magnetic and SSD-backed EBS volumes? Did the RPS value in t1.micro
# vary as significantly as in m3.large? Why do you think this is the case?
answer_16() {
	# Put your answer with a simple paragraph in a file called "answer_16"
	# Do not change the code below
	if [ -f answer_16 ]
	then
		echo "Answered"
	else
		echo "Not answered"
	fi
}


# DO NOT MODIFY ANYTHING BELOW THIS LINE

answer_5 &> /dev/null
echo "{"

echo -en ' '\"answer1\": \"`answer_1`\"
echo ","

echo -en ' '\"answer2\": \"`answer_2`\"
echo ","

echo -en ' '\"answer3\": \"`answer_3`\"
echo ","

echo -en ' '\"answer4\": \"`answer_4`\"
echo ","

if [ -f 'million_songs_metadata_and_sales.csv' ]
then
	echo -en ' '\"answer5\": \"'million_songs_metadata_and_sales.csv' file created\"
	echo ","
else
	echo -en ' '\"answer5\": \"'million_songs_metadata_and_sales.csv' file not created\"
	echo ","
fi

echo -en ' '\"answer6\": \"`answer_6`\"
echo ","

`mysql --skip-column-names --batch -u root -pdb15319root song_db -e "set global query_cache_size = 0" &> /dev/null`
`mysql --skip-column-names --batch -u root -pdb15319root song_db -e "drop index $INDEX_NAME on songs" > /dev/null`
START_TIME=$(date +%s.%N)
TID=`answer_7 | tail -1`
END_TIME=$(date +%s.%N)
RUN_TIME=$(echo "$END_TIME - $START_TIME" | bc)
echo -en ' '\"answer7\": \"$TID,$RUN_TIME\"
echo ","

answer_8 > /dev/null
INDEX_FIELD=`mysql --skip-column-names --batch -u root -pdb15319root song_db -e "describe songs" | grep MUL | cut -f1`
echo -en ' '\"answer8\": \"$INDEX_FIELD\"
echo ","

START_TIME=$(date +%s.%N)
TID=`answer_9 | tail -1`
END_TIME=$(date +%s.%N)
RUN_TIME=$(echo "$END_TIME - $START_TIME" | bc)
echo -en ' '\"answer9\": \"$TID,$RUN_TIME\"
echo ","

echo -en ' '\"answer10\": \"`answer_10`\"
echo ","

echo -en ' '\"answer11\": \"`answer_11`\"
echo ","

echo -en ' '\"answer12\": \"`answer_12`\"
echo ","

echo -en ' '\"answer13\": \"`answer_13`\"
echo ","

echo -en ' '\"answer14\": \"`answer_14`\"
echo ","

echo -en ' '\"answer15\": \"`answer_15`\"
echo ","

echo -en ' '\"answer16\": \"`answer_16`\"
echo 
echo  "}"



