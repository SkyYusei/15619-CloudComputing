import re
file=open('output','r')
num = 0
for line in file:
	if(re.search(r".*\(film\).*",line)):
		num = line.split()[1]
		break
print num
