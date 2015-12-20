osfile = open('q7','r')
a={} #dic value = OS name    key = views
printline = ''
for osname in osfile:
	file = open('output', 'r')
	osname = osname.strip()
	#print osname	
	for line in file:
		title = (line.split('\t')[1]).strip()
		if(title == osname):
		#	print title
			a[osname]=line.split('\t')[0]
c=sorted(a, key=a.get, reverse=True)  #sorted according to the value
for key in c:
	printline +=key+','
printline = printline[:(len(printline)-1)] #ignore the last ','
print printline
	
		
