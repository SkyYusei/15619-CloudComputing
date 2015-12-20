file=open("pagecounts-20150801-000000","r")
sum = 0
for line in file:
	q=line.split(" ")
	sum = sum+int(q[2])
print sum	
