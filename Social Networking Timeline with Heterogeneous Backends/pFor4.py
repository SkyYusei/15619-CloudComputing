#shuffle the data and make it in order 
#for the task4    the follower is the row key   
#and the followeee are combine as a string 
with open('sorted2.csv') as f:
	content = f.readlines()
f1 = open('done2','w')

b = ''
c = '1'
for line in content:
	fe = line.split(',')[0].strip();
	fr = line.split(',')[1].strip();
	if(c == fr):
		b = b + ' '+fe;
	if(c != fr):
		
		f1.write(c+','+b+'\n')
		c = fr
		b =fe



