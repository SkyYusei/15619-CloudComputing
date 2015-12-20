import sys

first = sys.argv[1]  #get the first name
second = sys.argv[2]  #get the second name
file = open('output','r')
for line in file:
	title = line.split("\t")[1]
	if(title == first):       #get the 1st name's line
		first_con = line   
	if(title == second):     #get the 2nd name's line
		second_con = line
sum = 0

#print (first_con.split("\t"))
#print second_con.split("\t")
for i in range (2,33): #get the daily views of 1st and 2nd name
	f_d = int((first_con.split("\t")[i]).split(":")[1])
	s_d = int((second_con.split("\t")[i]).split(":")[1])
	if(f_d > s_d):	
		sum+=1
print sum
