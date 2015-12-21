
#for the task2    the followee is the row key   
#and the followers are combine as a string 
with open('sorted.csv') as f:
	content = f.readlines()
f1 = open('done3','w')

b = '1'
c = ''
for line in content:
	fe = line.split(',')[0].strip();
	fr = line.split(',')[1].strip();
	if(b == fe):
		c = c + ' '+fr;
	if(b != fe):
		
		f1.write(b+','+c+'\n')
		c = fr
		b =fe



