namefile = open('q6','r')
f_dic = {}  #the film's view dic
v_dic={}   # the maxium view of film dic
a=[]
printline = ''
	
for name in namefile:   #get name's line data
	file = open('output','r')
	name = name.strip()
	#print name+"1231231"
	for line in file:
		if(line.split('\t')[1]==name):
			#print name
			f_dic[name] = line
for key in f_dic:   #get name's max daily views
	#print f_dic[key]
	for i in range(2,33):
		a.append (int(f_dic[key].split('\t')[i].split(':')[1]))
	v_dic[key]=max(a)
	#print a
	a= []
a = sorted(v_dic, key=v_dic.get, reverse=True) #sorted the dic by daily views
for key in a:  #print list by rank
	#print str(key)+str(v_dic[key])
	printline += key+','
printline = printline[:(len(printline)-1)]

print printline
		
			
