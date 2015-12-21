#sorted for task2  
#first use the followee to sort
#and then use followers
import sys, csv , operator
data = csv.reader(open
('links.csv'),delimiter=',')
sortedlist = sorted(data, key = lambda x: (x[0], int(x[1])))

with open("sorted2.csv", "wb") as f:
	fileWriter = csv.writer(f, delimiter=',')
	for row in sortedlist:
		fileWriter.writerow(row)
f.close()