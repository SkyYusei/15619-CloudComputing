file=open('output','r')
num = 0
for line in file:
	if(int(line.split("\t")[1])>2500):
		num=num+1
print num
