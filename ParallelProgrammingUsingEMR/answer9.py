file = open('output','r')
a={} #dic  key = title  value = views
maxnum = 0
for line in file:
	line = line.strip()
	linecont = line.split('\t')
	title = linecont[1]
	count = 0
	for i in range (2,32):  #get the longest decreasing array
		date = linecont[i].split(':')[0]
		num = int(linecont[i].split(':')[1])
		numn = int(linecont[i+1].split(':')[1])
		if(num>numn):
			count+=1
		else:
			count = 0
	a[title] = count

c = sorted(a.items(),key=lambda e:e[1],reverse=True) #sorted according to the value
#print c
d={} # key = title   value = views
tcount = 0
maxnum = c[0][1] #max decreasing dates number
for key in c:  # count the total number of max dates
#	print key
	if key[1]==maxnum:
		tcount+=1
	else:
		break

print tcount
	 
