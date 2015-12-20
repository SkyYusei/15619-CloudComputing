file = open('output','r')
b={} # dic value = views  key = title
for line in file:
	name = line.split('\t')[1].strip()
	if name == "NASDAQ-100":
		#print line
		for i in range (2,33):
			b[(line.split('\t')[i]).split(':')[0]]=int((line.split('\t')[i]).split(':')[1]);
#print b
a = sorted(b, key=b.get, reverse=True)
for key in a:     #just print the first line 
	print key
	break
	
