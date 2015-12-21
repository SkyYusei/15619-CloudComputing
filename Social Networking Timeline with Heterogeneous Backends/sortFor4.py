#sorted for task2  
#first use the follower to sort
#and then use followee
import sys, csv , operator
data = csv.reader(open
('links.csv'),delimiter=',')
sortedlist = sorted(data, key = lambda x: (x[1], int(x[0])))

with open("sorted2.csv", "wb") as f:
	fileWriter = csv.writer(f, delimiter=',')
	for row in sortedlist:
		fileWriter.writerow(row)
f.close()