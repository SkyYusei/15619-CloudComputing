import re
file=open("output","r")
num = 0
for line in file:
	if(re.search(r"^List_of.*episodes$",line.split("\t")[0])):
		num = num + int(line.split("\t")[1])
print num
