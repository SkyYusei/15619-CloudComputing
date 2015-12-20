import re
file=open('output','r')
num2014=0
num2015=0
for line in file:
	if(re.search(r".*\(2014_film\).*",line)):
		num2014=num2014+int(line.split("\t")[1])
	if(re.search(r".*\(2015_film\).*",line)):
		num2015=num2015+int(line.split("\t")[1])
if(num2015<num2014):
	print 2014
else:
	print 2015
